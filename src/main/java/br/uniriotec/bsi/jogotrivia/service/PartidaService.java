package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.gameplay.Partida;

@Path("/partidaService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class PartidaService {

	@GET
	public Response getPartidas(@QueryParam("idPartida") Integer idPartida) {
		List<Partida> partidas = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Partida p = new Partida("Conhecimentos gerais", new Date(1571268777000L + (929203 * i)));
			p.setId(i);
			partidas.add(p);
		}

		return buildResponse(Status.OK, partidas);
	}
	
	@POST
	@Autenticado({Privilegio.MODERADOR})
	public Response criarPartida(Partida partida) {
		return null;
	}

}
