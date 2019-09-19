package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
	public static final String[] EXCLUSODES_TOKEN_AUTENTICACAO = { "id", "usuario.hashSenha" };

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

		try {
			validarUsuario(usuarioSanetizado);
		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, ex.getMessage());
		}
		
		if (ud.selectByEmail(usuarioSanetizado.getEmail()) != null) {
			return buildResponse(Status.BAD_REQUEST, "Usuário já cadastrado");
		}

		ud.insert(usuarioSanetizado);
		return buildResponse(Status.ACCEPTED, usuarioSanetizado, EXCLUSODES_USUARIO);
	}

	@PUT
	public Response atualizar(Usuario usuarioJson) {
		UsuarioDao ud = new UsuarioDao();
		Usuario usuarioSanetizado = ud.select(usuarioJson.getId());
		
		if(usuarioSanetizado == null) {
			return buildResponse(Status.BAD_REQUEST, "Usuário não encontrado");
		}
		
		usuarioSanetizado.setNome(usuarioJson.getNome());
		usuarioSanetizado.setEmail(usuarioJson.getEmail());
		usuarioSanetizado.setAtivo(usuarioJson.getAtivo());
		
		if(usuarioJson.getHashSenha() != null) {
			usuarioSanetizado.setHashSenha(BCrypt.hashpw(usuarioJson.getHashSenha(), BCrypt.gensalt()));
		}

		try {
			validarUsuario(usuarioSanetizado);
		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, ex.getMessage());
		}
		
		ud.update(usuarioSanetizado);
		
		return buildResponse(Status.ACCEPTED, usuarioSanetizado, EXCLUSODES_USUARIO);
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

		return buildResponse(Status.ACCEPTED, tokenNovo, EXCLUSODES_TOKEN_AUTENTICACAO);
	}

	@GET
	public Response get() {
		return buildResponse(new Usuario("Mateus", "1234", "mateus.albertin@uniriotec.br", new Date(), true));
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
