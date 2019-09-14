package br.uniriotec.bsi.jogotrivia.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

@Path("/usuarioService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class UsuarioService {
	
	class Resposta{
		public final Usuario usuario;
		public final String mensagem;
		
		public Resposta(Usuario usuario, String mensagem) {
			this.usuario = usuario;
			this.mensagem = mensagem;
		}
	}

	@POST
	public Resposta cadastrar(Usuario u) {
		return new Resposta(u, "I got it!");
	}
	
	@GET
	public Usuario get() {
		return new Usuario();
	}
}
