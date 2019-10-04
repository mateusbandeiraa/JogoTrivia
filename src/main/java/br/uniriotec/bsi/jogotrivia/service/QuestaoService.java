package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.gameplay.Opcao;
import br.uniriotec.bsi.jogotrivia.gameplay.Questao;
import br.uniriotec.bsi.jogotrivia.persistence.QuestaoDao;

@Path("/questaoService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class QuestaoService {

	@POST
	@Autenticado(Privilegio.MODERADOR)
	public Response cadastrar(Questao questaoJson) {
		QuestaoDao qd = new QuestaoDao();

		Questao questaoSanetizada = new Questao(questaoJson.getTextoPergunta(), questaoJson.getTempoDisponivel());

		List<Opcao> opcoesSanetizadas = new ArrayList<>();
		Opcao opcaoCorreta = null;

		for (Opcao opcao : questaoJson.getOpcoes()) {
			Opcao opcaoSanetizada = new Opcao(opcao.getTexto(), opcao.isRemovivel());
			opcoesSanetizadas.add(opcaoSanetizada);
			if (opcao.isCorreta()) {
				if (opcaoCorreta == null) {
					opcaoCorreta = opcao;
				} else {
					return buildResponse(Status.BAD_REQUEST, "A questão só pode ter 1 opção correta");
				}
			}
		}

		try {
			questaoSanetizada.setOpcoes(opcoesSanetizadas, opcaoCorreta);
		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, "A questão precisa ter uma opção correta");
		}

		if (questaoSanetizada.getOpcoes().size() != 4) {
			return buildResponse(Status.BAD_REQUEST, "A questão precisa ter 4 opções");
		}

		qd.insert(questaoSanetizada);

		return buildResponse(Status.ACCEPTED, questaoSanetizada);
	}

	@PUT
	@Autenticado(Privilegio.MODERADOR)
	public Response editar(Questao questaoJson) {
		QuestaoDao qd = new QuestaoDao();

		Questao questaoSanetizada = qd.select(questaoJson.getId());

		if (questaoSanetizada == null) {
			return cadastrar(questaoJson);
		}

		questaoSanetizada.setTextoPergunta(questaoJson.getTextoPergunta());
		questaoSanetizada.setTempoDisponivel(questaoJson.getTempoDisponivel());

		List<Opcao> opcoesSanetizadas = new ArrayList<>();
		Opcao opcaoCorreta = null;

		for (Opcao opcao : questaoJson.getOpcoes()) {
			Opcao opcaoSanetizada = new Opcao(opcao.getTexto(), opcao.isRemovivel());
			opcoesSanetizadas.add(opcaoSanetizada);
			if (opcao.isCorreta()) {
				if (opcaoCorreta == null) {
					opcaoCorreta = opcao;
				} else {
					return buildResponse(Status.BAD_REQUEST, "A questão só pode ter 1 opção correta");
				}
			}
		}

		try {
			questaoSanetizada.setOpcoes(opcoesSanetizadas, opcaoCorreta);
		} catch (IllegalArgumentException ex) {
			return buildResponse(Status.BAD_REQUEST, "A questão precisa ter uma opção correta");
		}

		if (questaoSanetizada.getOpcoes().size() != 4) {
			return buildResponse(Status.BAD_REQUEST, "A questão precisa ter 4 opções");
		}

		qd.update(questaoSanetizada);

		return buildResponse(Status.ACCEPTED, questaoSanetizada);
	}
	
	@DELETE
	@Autenticado(Privilegio.MODERADOR)
	public Response excluir(Questao questaoJson) {
		new QuestaoDao().delete(questaoJson);
		return buildResponse(Status.OK, "Questão excluída");
	}

	@GET
	@Autenticado(Privilegio.MODERADOR)
	public Response get(@QueryParam("idQuestao") Integer idQuestao) {
		QuestaoDao qd = new QuestaoDao();
		if (idQuestao != null) {
			Questao questao = qd.select(idQuestao);
			return buildResponse(Status.OK, questao);
		} else {
			List<Questao> questoes = qd.selectAll();
			return buildResponse(Status.OK, questoes);
		}
	}
}
