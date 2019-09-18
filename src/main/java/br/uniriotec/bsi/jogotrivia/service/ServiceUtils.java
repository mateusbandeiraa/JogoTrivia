package br.uniriotec.bsi.jogotrivia.service;

import static com.monitorjbl.json.Match.match;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.JsonViewModule;

public abstract class ServiceUtils {

	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	public static Response buildResponse(Status status) {
		return Response.status(status).build();
	}

	public static Response buildResponse(Object entity) {
		return Response.status(Status.OK).entity(entity).build();
	}

	public static Response buildResponse(Status status, Object entity) {
		return Response.status(status).entity(entity).build();
	}

	public static Response buildResponse(Status status, Object entity, String[] exclusoes) {
		ObjectMapper mapper = new ObjectMapper().registerModule(new JsonViewModule());
		Response resposta;

		try {
			String json = mapper
					.writeValueAsString(JsonView.with(entity).onClass(entity.getClass(), match().exclude(exclusoes)));

			resposta = Response.status(status).entity(json).build();
		} catch (JsonProcessingException ex) {
			resposta = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			ex.printStackTrace();
		}

		return resposta;
	}
}
