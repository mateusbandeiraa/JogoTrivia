package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mindrot.jbcrypt.BCrypt;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

@Path("/usuarioService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {

	public static final String[] exclusoes = { "hashSenha" };

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
		return buildResponse(Status.ACCEPTED, usuarioSanetizado, exclusoes);
	}

	@GET
	public Response get() {
		return buildResponse(new Usuario("Mateus", "1234", "mateus.albertin@uniriotec.br", new Date(), true));
	}
}
