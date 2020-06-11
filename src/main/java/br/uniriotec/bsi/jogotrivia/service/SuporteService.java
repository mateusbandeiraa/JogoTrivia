package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.User;
import br.uniriotec.bsi.jogotrivia.persistence.TicketDao;
import br.uniriotec.bsi.jogotrivia.suporte.EstadoTicket;
import br.uniriotec.bsi.jogotrivia.suporte.Mensagem;
import br.uniriotec.bsi.jogotrivia.suporte.Ticket;

@Path("/suporteService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class SuporteService {

	@POST
	@Autenticado
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response abrirTicket(@FormParam("assunto") String assunto, @FormParam("mensagem") String mensagem,
			@Context SecurityContext securityContext) {
		User usuarioAutenticado = obterUsuarioPorSecurityContext(securityContext);

		Ticket ticket = usuarioAutenticado.abrirTicket(assunto, mensagem);

		new TicketDao().insert(ticket);

		return buildResponse(Status.ACCEPTED, ticket);
	}

	@GET
	@Autenticado
	public Response obterTicket(@QueryParam("ticketId") String ticketId, @Context SecurityContext securityContext) {
		if (ticketId != null) {
			Ticket ticket = new TicketDao().select(Integer.valueOf(ticketId));
			return buildResponse(Status.OK, ticket);
		} else {
			List<Ticket> tickets = new TicketDao()
					.selectByUser(Integer.valueOf(securityContext.getUserPrincipal().getName()));
			return buildResponse(Status.OK, tickets);
		}
	}

	@GET
	@Autenticado(Privilegio.MODERADOR)
	@Path("/todos")
	public Response obterTodosTickets() {
		List<Ticket> tickets = new TicketDao().selectAll();
		return buildResponse(Status.OK, tickets);
	}

	@POST
	@Autenticado
	@Path("/mensagem")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response adicionarMensagem(@FormParam("ticketId") String ticket, @FormParam("mensagem") String mensagem,
			@Context SecurityContext securityContext) {
		User usuarioAutenticado = obterUsuarioPorSecurityContext(securityContext);

		TicketDao td = new TicketDao();
		Ticket ticketSanetizado = td.select(Integer.valueOf(ticket));

		ticketSanetizado.adicionarMensagem(new Mensagem(mensagem, usuarioAutenticado));

		td.update(ticketSanetizado);

		return buildResponse(Status.ACCEPTED, ticketSanetizado);
	}

	@GET
	@Path("/estado")
	public Response getEstados() {
		return buildResponse(Status.OK, EstadoTicket.values());
	}

	@POST
	@Autenticado(Privilegio.MODERADOR)
	@Path("/estado")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setEstado(@FormParam("ticketId") String ticketId, @FormParam("estado") String estado,
			@Context SecurityContext securityContext) {
		EstadoTicket estadoTicket = null;
		
		for(EstadoTicket e: EstadoTicket.values()) {
			if(e.toString().equals(estado)) {
				estadoTicket = e;
				break;
			}
		}
		if(estadoTicket == null) {
			buildResponse(Status.BAD_REQUEST, "Estado inválido");
		}
		
		TicketDao td = new TicketDao();
		
		Ticket ticket = td.select(Integer.valueOf(ticketId));
		
		if(ticket == null) {
			buildResponse(Status.BAD_REQUEST, "Ticket inválido");
		}
		
		ticket.setEstadoAtual(estadoTicket);
		
		td.update(ticket);
		
		return buildResponse(Status.OK, ticket);
	}

}
