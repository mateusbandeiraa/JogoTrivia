package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

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

import org.mindrot.jbcrypt.BCrypt;

import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.ServiceUtils.ParExclusoes;

@Path("/usuarioService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final ParExclusoes EXCLUSOES_USUARIO = new ParExclusoes(Usuario.class, "hashSenha");
	public static final ParExclusoes EXCLUSOES_TOKEN_AUTENTICACAO = new ParExclusoes(TokenAutenticacao.class, "id");

	@POST
	public Response cadastrar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();

		if (usuarioJson.getHashSenha() == null) {
			return buildResponse(Status.BAD_REQUEST);
		}

		String hashSenha = BCrypt.hashpw(usuarioJson.getHashSenha(), BCrypt.gensalt());
		/**
		 * usuarioSanetizado é utilizado para evitar que campos indesejados inseridos no
		 * JSON do request possam ser salvos no banco de dados.
		 */
		Usuario usuarioSanetizado = new Usuario(usuarioJson.getNome(), hashSenha, usuarioJson.getEmail(), new Date(),
				true);

		try {
			validarUsuario(usuarioSanetizado);
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

		usuarioSanetizado.setNome(usuarioJson.getNome());
		usuarioSanetizado.setEmail(usuarioJson.getEmail());
		usuarioSanetizado.setAtivo(usuarioJson.getAtivo());

		if (usuarioJson.getHashSenha() != null) {
			usuarioSanetizado.setHashSenha(BCrypt.hashpw(usuarioJson.getHashSenha(), BCrypt.gensalt()));
		}

		try {
			validarUsuario(usuarioSanetizado);
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

		if (usuario == null || !BCrypt.checkpw(usuarioJson.getHashSenha(), usuario.getHashSenha())) {
			return buildResponse(Status.UNAUTHORIZED, "Usuário ou senha incorretos");
		}

		TokenAutenticacaoDao tad = new TokenAutenticacaoDao();

		TokenAutenticacao tokenExistente = tad.selectByUser(usuario);
		if (tokenExistente != null) {
			tad.delete(tokenExistente);
		}

		TokenAutenticacao tokenNovo = new TokenAutenticacao(usuario);

		tad.insert(tokenNovo);

		return buildResponse(Status.ACCEPTED, tokenNovo, EXCLUSOES_TOKEN_AUTENTICACAO, EXCLUSOES_USUARIO);
	}

	@GET
	@Autenticado
	public Response get(@QueryParam("id") String idUsuario, @Context SecurityContext securityContext) {
		UsuarioDao ud = new UsuarioDao();
		Response response;
		if (idUsuario != null) {
			Usuario usuario = ud.select(Integer.valueOf(idUsuario));
			if (usuario != null) {
				response = buildResponse(Status.OK, usuario, EXCLUSOES_USUARIO);
			} else {
				response = buildResponse(Status.NOT_FOUND, "Usuário não encontrado");
			}
		} else {
			List<Usuario> usuarios = ud.selectAll();
			response = buildResponse(Status.OK, usuarios, EXCLUSOES_USUARIO, EXCLUSOES_TOKEN_AUTENTICACAO);
		}
		return response;
	}

	private static boolean validarUsuario(Usuario usuario) {
		if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
			throw new IllegalArgumentException("Nome inválido");
		}
		if (!usuario.getEmail().matches(ServiceUtils.EMAIL_REGEX)) {
			throw new IllegalArgumentException("E-mail inválido");
		}
		return true;
	}
}
