package br.uniriotec.bsi.jogotrivia.service;

import static com.monitorjbl.json.Match.match;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.JsonViewModule;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

public abstract class ServiceUtils {

	public static Usuario obterUsuarioPorSecurityContext(SecurityContext context) {
		if (context == null || context.getUserPrincipal() == null) {
			return null;
		}
		int idUsuarioAutenticado = Integer.valueOf(context.getUserPrincipal().getName());
		Usuario usuarioAutenticado = new UsuarioDao().select(idUsuarioAutenticado);
		return usuarioAutenticado;
	}
	
	public static Response buildResponse(Status status) {
		return Response.status(status).build();
	}

	public static Response buildResponse(Object entity) {
		return Response.status(Status.OK).entity(entity).build();
	}

	public static Response buildResponse(Status status, Object entity) {
		return Response.status(status).entity(entity).build();
	}

	public static Response buildResponse(Status status, Object entity, ParExclusoes... exclusoes) {
		ObjectMapper mapper = new ObjectMapper().registerModule(new JsonViewModule());
		Response resposta;

		try {
			JsonView<Object> view = JsonView.with(entity);
			for (ParExclusoes par : exclusoes) {
				view = view.onClass(par.classe, match().exclude(par.exclusoes));
			}
			String json = mapper.writeValueAsString(view);

			resposta = Response.status(status).entity(json).build();
		} catch (JsonProcessingException ex) {
			resposta = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			ex.printStackTrace();
		}

		return resposta;
	}

	protected static class ParExclusoes {
		public final Class<?> classe;
		public final String[] exclusoes;

		public ParExclusoes(Class<?> classe, String... exclusoes) {
			this.classe = classe;
			this.exclusoes = exclusoes;
		}
	}
}
