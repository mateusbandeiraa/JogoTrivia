package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mindrot.jbcrypt.BCrypt;

import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

@Path("/usuarioService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final String[] EXCLUSODES_USUARIO = { "hashSenha" };
	public static final String[] EXCLUSODES_TOKENAUTENTICACAO = { "usuario.hashSenha" };

	@POST
	public Response cadastrar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();

		String hashSenha = BCrypt.hashpw(usuarioJson.getHashSenha(), BCrypt.gensalt());
		/**
		 * usuarioSanetizado é utilizado para evitar que campos indesejados inseridos no
		 * JSON do request possam ser salvos no banco de dados.
		 */
		Usuario usuarioSanetizado = new Usuario(usuarioJson.getNome(), hashSenha, usuarioJson.getEmail(), new Date(),
				true);

		if (usuarioSanetizado == null || usuarioSanetizado.getNome().isEmpty()) {
			return buildResponse(Status.BAD_REQUEST, "Nome inválido");
		}
		if (!usuarioSanetizado.getEmail().matches(ServiceUtils.EMAIL_REGEX)) {
			return buildResponse(Status.BAD_REQUEST, "E-mail inválido");
		}
		if (ud.selectByEmail(usuarioSanetizado.getEmail()) != null) {
			return buildResponse(Status.BAD_REQUEST, "Usuário já cadastrado");
		}

		ud.insert(usuarioSanetizado);
		return buildResponse(Status.ACCEPTED, usuarioSanetizado, EXCLUSODES_USUARIO);
	}

	@POST
	@Path("/autenticar")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response autenticar(@FormParam("email") String email, @FormParam("senha") String senha) {
		UsuarioDao ud = new UsuarioDao();
		Usuario usuario = ud.selectByEmail(email);

		if (usuario == null || !BCrypt.checkpw(senha, usuario.getHashSenha())) {
			return buildResponse(Status.UNAUTHORIZED, "Usuario ou senha incorretos");
		}

		TokenAutenticacaoDao tad = new TokenAutenticacaoDao();

		TokenAutenticacao tokenExistente = tad.selectByUser(usuario);
		if (tokenExistente != null) {
			tad.delete(tokenExistente);
		}

		TokenAutenticacao tokenNovo = new TokenAutenticacao(usuario);

		return buildResponse(Status.ACCEPTED, tokenNovo, EXCLUSODES_TOKENAUTENTICACAO);
	}

	@GET
	public Response get() {
		return buildResponse(new Usuario("Mateus", "1234", "mateus.albertin@uniriotec.br", new Date(), true));
	}
}
