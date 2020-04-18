package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario.SenhaInvalidaException;
import br.uniriotec.bsi.jogotrivia.gameplay.Participante;
import br.uniriotec.bsi.jogotrivia.persistence.ParticipanteDao;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.ServiceUtils.ParExclusoes;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewHistorico;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;
import br.uniriotec.bsi.jogotrivia.view.ViewUsuario;

@Path("/usuario")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final ParExclusoes EXCLUSOES_USUARIO = new ParExclusoes(Usuario.class, "hashSenha");
	public static final ParExclusoes EXCLUSOES_TOKEN_AUTENTICACAO = new ParExclusoes(TokenAutenticacao.class, "id");

	@Context
	SecurityContext securityContext;

	@POST
	@JsonView(ViewUsuario.Proprio.class)
	public Usuario cadastrar(@JsonView(ViewUsuario.Proprio.Parametros.Cadastrar.class) Usuario usuario) {
		UsuarioDao ud = new UsuarioDao();

		try {
			usuario.setDataCadastro(new Date());
			usuario.setAtivo(true);

			usuario.gerarHashSenha(usuario.getSenha());
		} catch (IllegalArgumentException | SenhaInvalidaException ex) {
			throw new BadRequestException(Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build());
		}

		if (ud.selectByEmail(usuario.getEmail()) != null) {
			throw new BadRequestException(Response.status(Status.BAD_REQUEST).entity("Usuário já cadastrado").build());
		}

		ud.insert(usuario);
		return usuario;
	}

	@PUT
	@Autenticado
	public Response atualizar(@JsonView(ViewUsuario.Moderador.Parametros.Atualizar.class) Usuario usuarioDadosNovos) {
		UsuarioDao ud = new UsuarioDao();

		Usuario usuarioRequerente = obterUsuarioPorSecurityContext(securityContext);
		Usuario usuarioBD = ud.select(usuarioDadosNovos.getId());

		if (usuarioRequerente.getPrivilegio().equals(Privilegio.USUARIO)) {
			if (usuarioRequerente.getId() != usuarioDadosNovos.getId()) {
				throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity("Sem autorização para alterar dados de outro usuário.").build());
			}
			if (usuarioDadosNovos.getPrivilegio() != null
					&& !usuarioBD.getPrivilegio().equals(usuarioDadosNovos.getPrivilegio())) {
				throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED)
						.entity("Sem autorização para alterar privilégios.").build());
			}
		}

		if (usuarioBD == null) {
			throw new BadRequestException(
					Response.status(Status.BAD_REQUEST).entity("Usuário não encontrado.").build());
		}

		try {
			usuarioBD.copiarAtributosNãoDefault(usuarioDadosNovos);
		} catch (SenhaInvalidaException ex) {
			throw new BadRequestException(ex.getMessage());
		}

		ud.update(usuarioBD);

		String jsonString;
		try {
			jsonString = usuarioBD.serializar(usuarioRequerente);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			throw new InternalServerErrorException();
		}

		return Response.accepted().entity(jsonString).build();
	}

	@POST
	@Path("/autenticar")
	public Response autenticar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();
		Usuario usuario = ud.selectByEmail(usuarioJson.getEmail());

		TokenAutenticacao tokenNovo;

		try {
			if (usuario == null) {
				throw new Exception();
			}

			tokenNovo = usuario.autenticar(usuarioJson.getHashSenha());

		} catch (Exception ex) {
			return buildResponse(Status.UNAUTHORIZED, "Usuário ou senha incorretos");
		}

		TokenAutenticacaoDao tad = new TokenAutenticacaoDao();

		TokenAutenticacao tokenExistente = tad.selectByUser(usuario);
		if (tokenExistente != null) {
			tad.delete(tokenExistente);
		}

		tad.insert(tokenNovo);

		return buildResponse(Status.ACCEPTED, tokenNovo, ViewAutenticado.class);
	}

	@GET
	@Autenticado(Privilegio.MODERADOR)
	public Response get(@QueryParam("id") String idUsuario, @Context SecurityContext securityContext) {
		UsuarioDao ud = new UsuarioDao();
		Response response;
		if (idUsuario != null) {
			Usuario usuario = ud.select(Integer.valueOf(idUsuario));
			ud.selectLancamentos(usuario);
			if (usuario != null) {
				response = buildResponse(Status.OK, usuario, ViewAutenticado.class);
			} else {
				response = buildResponse(Status.NOT_FOUND, "Usuário não encontrado");
			}
		} else {
			List<Usuario> usuarios = ud.selectAll();
			response = buildResponse(Status.OK, usuarios, ViewPublico.class);
		}
		return response;
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
		Usuario usuarioAutenticado = obterUsuarioPorSecurityContext(securityContext);
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
