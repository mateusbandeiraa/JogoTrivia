package br.uniriotec.bsi.jogotrivia.service;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

@Autenticado
@Provider
@Priority(Priorities.AUTHORIZATION)
public class FiltroAutorizacao implements ContainerRequestFilter {
	// https://stackoverflow.com/a/45814178/7320994
	@Context
	private ResourceInfo resourceInfo;
	@Context
	SecurityContext securityContext;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the resource class which matches with the requested URL
		// Extract the roles declared by it
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Privilegio> privilegiosClasse = extrairPrivilegios(resourceClass);

		// Get the resource method which matches with the requested URL
		// Extract the roles declared by it
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Privilegio> privilegiosMetodo = extrairPrivilegios(resourceMethod);

		try {

			// Check if the user is allowed to execute the method
			// The method annotations override the class annotations
			if (privilegiosMetodo.isEmpty()) {
				checarPermissoes(privilegiosClasse);
			} else {
				checarPermissoes(privilegiosMetodo);
			}

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}

	// Extract the roles from the annotated element
	private List<Privilegio> extrairPrivilegios(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return new ArrayList<Privilegio>();
		} else {
			Autenticado autenticado = annotatedElement.getAnnotation(Autenticado.class);
			if (autenticado == null) {
				return new ArrayList<Privilegio>();
			} else {
				Privilegio[] privilegiosAutorizados = autenticado.value();
				return Arrays.asList(privilegiosAutorizados);
			}
		}
	}

	private void checarPermissoes(List<Privilegio> privilegiosAutorizados) throws Exception {
		String userId = securityContext.getUserPrincipal().getName();
		Usuario usuario = new UsuarioDao().select(Integer.valueOf(userId));
		
		for(Privilegio privilegio : privilegiosAutorizados) {
			if(privilegio.equals(usuario.getPrivilegio())) {
				return;
			}
		}
		throw new Exception();
	}
}
