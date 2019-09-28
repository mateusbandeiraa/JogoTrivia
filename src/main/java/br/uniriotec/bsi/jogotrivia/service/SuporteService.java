package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.TicketDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.suporte.Ticket;

@Path("/suporteService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class SuporteService {

	@POST
	@Autenticado()
	public Response abrirTicket(String assunto, String mensagem, @Context SecurityContext securityContext) {
		int idUsuarioAutenticado = Integer.valueOf(securityContext.getUserPrincipal().getName());
		Usuario usuarioAutenticado = new UsuarioDao().select(idUsuarioAutenticado);
		
		Ticket ticket = usuarioAutenticado.abrirTicket(assunto, mensagem);
		
		new TicketDao().insert(ticket);
		
		return buildResponse(Status.ACCEPTED, ticket);
	}

}
