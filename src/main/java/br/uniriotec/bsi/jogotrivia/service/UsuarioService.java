package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.User;
import br.uniriotec.bsi.jogotrivia.administrativo.User.InvalidPasswordException;
import br.uniriotec.bsi.jogotrivia.gameplay.Participante;
import br.uniriotec.bsi.jogotrivia.persistence.ParticipanteDao;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.ServiceUtils.ParExclusoes;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewHistorico;
import br.uniriotec.bsi.jogotrivia.view.ViewTokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.view.ViewUsuario;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final ParExclusoes EXCLUSOES_USUARIO = new ParExclusoes(User.class, "hashSenha");
	public static final ParExclusoes EXCLUSOES_TOKEN_AUTENTICACAO = new ParExclusoes(
			TokenAutenticacao.class, "id");

	@Context
	SecurityContext securityContext;

	private interface SignUpView extends ViewUsuario.Proprio, ViewTokenAutenticacao.Proprio {
	};

	@POST
	@JsonView(SignUpView.class)
	public Map<String, Object> signUp(
			@JsonView(ViewUsuario.Proprio.Parametros.Cadastrar.class) User user)
			throws JsonProcessingException {
		UsuarioDao ud = new UsuarioDao();

		try {
			user.setRegisterDate(new Date());
			user.setActive(true);
			user.setAuthorization(Privilegio.USUARIO);

			user.generatePasswordHash(user.getPassword());
		} catch (IllegalArgumentException | InvalidPasswordException ex) {
			throw new BadRequestException(
					Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build());
		}

		if (ud.selectByEmail(user.getEmail()) != null) {
			throw new BadRequestException(
					Response.status(Status.BAD_REQUEST).entity("E-mail already in use").build());
		}
		// TODO CHECK IF USERNAME IS TAKEN

		ud.insert(user);
		TokenAutenticacao token = autenticar(user);

		Map<String, Object> response = new HashMap<>();
		response.put("user", user);
		response.put("token", token);
		return response;
	}

	@PUT
	@Autenticado
	public Response atualizar(
			@JsonView(ViewUsuario.Moderador.Parametros.Atualizar.class) User usuarioDadosNovos) {
		UsuarioDao ud = new UsuarioDao();

		User usuarioRequerente = obterUsuarioPorSecurityContext(securityContext);
		User usuarioBD = ud.select(usuarioDadosNovos.getId());

		if (usuarioRequerente.getAuthorization().equals(Privilegio.USUARIO)) {
			if (usuarioRequerente.getId() != usuarioDadosNovos.getId()) {
				throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity("Sem autorização para alterar dados de outro usuário.").build());
			}
			if (usuarioDadosNovos.getAuthorization() != null
					&& !usuarioBD.getAuthorization().equals(usuarioDadosNovos.getAuthorization())) {
				throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity("Sem autorização para alterar privilégios.").build());
			}
		}

		if (usuarioBD == null) {
			throw new BadRequestException(
					Response.status(Status.BAD_REQUEST).entity("Usuário não encontrado.").build());
		}

		try {
			usuarioBD.copyNonDefaultAttributes(usuarioDadosNovos);
		} catch (InvalidPasswordException ex) {
			throw new BadRequestException(ex.getMessage());
		}

		ud.update(usuarioBD);

		String jsonString;
		try {
			jsonString = usuarioBD.serialize(usuarioRequerente);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			throw new InternalServerErrorException();
		}

		return Response.accepted().entity(jsonString).build();
	}

	@POST
	@Path("/auth")
	@JsonView(ViewTokenAutenticacao.Proprio.class)
	public TokenAutenticacao autenticar(
			@JsonView(ViewUsuario.Proprio.Parametros.Autenticar.class) User usuarioRequerente) {
		UsuarioDao ud = new UsuarioDao();
		User usuario = ud.selectByEmail(usuarioRequerente.getEmail());

		if (usuario == null) {
			throw new NotFoundException(
					Response.status(Status.NOT_FOUND).entity("E-mail não encontrado.").build());
		}

		TokenAutenticacao tokenNovo = usuario.authenticate(usuarioRequerente.getPassword());

		if (tokenNovo == null) {
			throw new NotAuthorizedException(
					Response.status(Status.UNAUTHORIZED).entity("Senha incorreta.").build());
		}

		TokenAutenticacaoDao tad = new TokenAutenticacaoDao();

		TokenAutenticacao tokenExistente = tad.selectByUser(usuario);
		if (tokenExistente != null) {
			tad.delete(tokenExistente);
		}

		tad.insert(tokenNovo);

		return tokenNovo;
	}

	@GET
	@Autenticado
	@Path("/{id}")
	public Response obter(@PathParam("id") Integer idUsuario) {
		User usuarioRequerente = obterUsuarioPorSecurityContext(securityContext);

		if (usuarioRequerente.getAuthorization().equals(Privilegio.USUARIO)
				&& !idUsuario.equals(usuarioRequerente.getId())) {

			throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
					.entity("Sem autorização para exibir dados deste usuário.").build());

		}

		UsuarioDao ud = new UsuarioDao();

		User usuarioBD = ud.select(idUsuario);

		if (usuarioBD == null) {
			throw new NotFoundException(
					Response.status(Status.NOT_FOUND).entity("Usuário não encontrado.").build());
		}

		String jsonUsuario;
		try {
			jsonUsuario = usuarioBD.serialize(usuarioRequerente);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			throw new InternalServerErrorException();
		}

		return Response.ok(jsonUsuario).build();
	}

	@GET
	@Autenticado({ Privilegio.MODERADOR })
	@JsonView(ViewUsuario.Moderador.class)
	public List<User> obterTodos() {
		UsuarioDao ud = new UsuarioDao();
		List<User> listaUsuarios = ud.selectAll();
		return listaUsuarios;
	}

	@GET
	@Path("/privilegios")
	public Response getPrivilegios() {
		return buildResponse(Status.OK, Privilegio.values());
	}

	@POST
	@Path("/obterMaisAjudas")
	@Autenticado
	public Response obterMaisAjudas() {
		User usuarioAutenticado = obterUsuarioPorSecurityContext(securityContext);
		SecureRandom random = new SecureRandom();
		Integer numeroAleatorio = random.nextInt(3);
		Integer quantidadeAleatoria = random.nextInt(3) + 1;

		switch (numeroAleatorio) {
		case 0:
			usuarioAutenticado.incrementarQuantidadeAjudasBomba(quantidadeAleatoria);
			break;
		case 1:
			usuarioAutenticado.incrementarQuantidadeAjudasBonus(quantidadeAleatoria);
			break;
		case 2:
			usuarioAutenticado.incrementarQuantidadeAjudasPopular(quantidadeAleatoria);
			break;
		default:
			break;
		}

		new UsuarioDao().update(usuarioAutenticado);

		String urlCompartilhar = "https://twitter.com/intent/tweet?text=Estou%20jogando%20JogoTrivia!";

		return buildResponse(Status.OK, urlCompartilhar);

	}

	@GET
	@Path("/obterHistorico")
	@Autenticado
	public Response obterHistorico() {
		List<Participante> historico = new ParticipanteDao()
				.selectPorUsuario(obterUsuarioPorSecurityContext(securityContext).getId());
		return buildResponse(Status.OK, historico, ViewHistorico.class);
	}
}
