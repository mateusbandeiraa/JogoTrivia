package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.financeiro.DirecaoLancamento;
import br.uniriotec.bsi.jogotrivia.financeiro.Lancamento;
import br.uniriotec.bsi.jogotrivia.financeiro.LancamentoMonetario;
import br.uniriotec.bsi.jogotrivia.persistence.LancamentoDao;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;

@Path("/financeiroService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class FinanceiroService {
	@Context
	SecurityContext securityContext;

	@POST
	@Autenticado
	public Response depositar(Integer valor) {
		LancamentoMonetario lancamento = new LancamentoMonetario(new BigDecimal(valor),
				obterUsuarioPorSecurityContext(securityContext), DirecaoLancamento.ENTRADA);
		LancamentoDao ld = new LancamentoDao();

		ld.insert(lancamento);

		lancamento.solicitarAutorizacao();

		ld.update(lancamento);

		return buildResponse(Status.ACCEPTED, lancamento, ViewAutenticado.class);
	}

	@GET
	@Path("/atualizarLancamentos")
	@Autenticado
	public Response atualizarLancamentos() {
		LancamentoDao ld = new LancamentoDao();
		Usuario usuarioLogado = ServiceUtils.obterUsuarioPorSecurityContext(securityContext);
		new UsuarioDao().selectLancamentos(usuarioLogado);

		for (Lancamento lancamento : usuarioLogado.getLancamentos()) {
			if (lancamento instanceof LancamentoMonetario) {
				LancamentoMonetario lm = (LancamentoMonetario) lancamento;

				if (!lm.isEstaPago() && (lm.verificarPagamento())) {
					ld.update(lm);
				}
			}
		}
		return buildResponse(Status.OK);
	}
}
