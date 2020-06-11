package br.uniriotec.bsi.jogotrivia.service;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.security.sasl.AuthenticationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.User;
import br.uniriotec.bsi.jogotrivia.persistence.TokenAutenticacaoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

@Autenticado
@Provider
@Priority(Priorities.AUTHENTICATION)
// https://stackoverflow.com/a/45814178/7320994
public class FiltroAutenticacao implements ContainerRequestFilter {

	private static final String REALM = "jogotrivia";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Get the Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Validate the Authorization header
		if (!isTokenBasedAuthentication(authorizationHeader)) {
			abortWithUnauthorized(requestContext);
			return;
		}

		// Extract the token from the Authorization header
		String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

		try {

			// Validate the token
			TokenAutenticacao tokenAutenticacao = validateToken(token);
			User usuario = new UsuarioDao().select(tokenAutenticacao.getUsuario().getId());

			final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
			requestContext.setSecurityContext(new SecurityContext() {

				@Override
				public Principal getUserPrincipal() {
					return () -> String.valueOf(usuario.getId());
				}

				@Override
				public boolean isUserInRole(String role) {
					return usuario.getAuthorization().toString().equals(role);
				}

				@Override
				public boolean isSecure() {
					return currentSecurityContext.isSecure();
				}

				@Override
				public String getAuthenticationScheme() {
					return AUTHENTICATION_SCHEME;
				}
			});

		} catch (Exception e) {
			abortWithUnauthorized(requestContext);
		}
	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {

		// Check if the Authorization header is valid
		// It must not be null and must be prefixed with "Bearer" plus a whitespace
		// The authentication scheme comparison must be case-insensitive
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {

		// Abort the filter chain with a 401 status code response
		// The WWW-Authenticate header is sent along with the response
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
	}

	private TokenAutenticacao validateToken(String tokenString) throws Exception {
		// Check if the token was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid

		TokenAutenticacaoDao tad = new TokenAutenticacaoDao();
		TokenAutenticacao token = tad.selectByToken(tokenString);
		if (token == null) {
			throw new AuthenticationException("Token inválido");
		}
		return token;
	}
}