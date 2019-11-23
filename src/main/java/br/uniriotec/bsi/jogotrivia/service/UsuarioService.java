package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.ServiceUtils.ParExclusoes;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;

@Path("/usuarioService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final ParExclusoes EXCLUSOES_USUARIO = new ParExclusoes(Usuario.class, "hashSenha");
	public static final ParExclusoes EXCLUSOES_TOKEN_AUTENTICACAO = new ParExclusoes(TokenAutenticacao.class, "id");

	@Context
	SecurityContext securityContext;

	@POST
	public Response cadastrar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();
		Usuario usuarioSanetizado;

		try {
			/**
			 * usuarioSanetizado é utilizado para evitar que campos indesejados inseridos no
			 * JSON do request possam ser salvos no banco de dados.
			 */
			usuarioSanetizado = new Usuario(usuarioJson.getNome(), usuarioJson.getEmail(), new Date(), true);

			usuarioSanetizado.setSenha(usuarioJson.getHashSenha());
		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, ex.getMessage());
		}

		if (ud.selectByEmail(usuarioSanetizado.getEmail()) != null) {
			return buildResponse(Status.BAD_REQUEST, "Usuário já cadastrado");
		}

		ud.insert(usuarioSanetizado);
		return buildResponse(Status.ACCEPTED, usuarioSanetizado, EXCLUSOES_USUARIO);
	}

	@PUT
	public Response atualizar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();
		Usuario usuarioSanetizado = ud.select(usuarioJson.getId());

		if (usuarioSanetizado == null) {
			return buildResponse(Status.BAD_REQUEST, "Usuário não encontrado.");
		}

		try {
			usuarioSanetizado.setNome(usuarioJson.getNome());
			usuarioSanetizado.setEmail(usuarioJson.getEmail());
			usuarioSanetizado.setAtivo(usuarioJson.getAtivo());
			usuarioSanetizado.setPrivilegio(usuarioJson.getPrivilegio());

			if (usuarioJson.getHashSenha() != null && !usuarioJson.getHashSenha().isEmpty()) {
				usuarioSanetizado.setSenha(usuarioJson.getHashSenha());
			}

		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, ex.getMessage());
		}

		ud.update(usuarioSanetizado);

		return buildResponse(Status.ACCEPTED, usuarioSanetizado, EXCLUSOES_USUARIO);
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
}
