package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.uniriotec.bsi.jogotrivia.gameplay.Questao;
import br.uniriotec.bsi.jogotrivia.persistence.QuestaoDao;

@Path("/questaoService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class QuestaoService {

	@POST
	public Response cadastrar(Questao q) {
		QuestaoDao qd = new QuestaoDao();

		if (q.getOpcoes().size() != 4) {
			return buildResponse(Status.BAD_REQUEST, "A questão precisa ter 4 opções");
		}

		qd.insert(q);

		return buildResponse(Status.ACCEPTED, q);
	}

	@GET
	@Consumes()
	public Response get(@QueryParam("idQuestao") Integer idQuestao) {
		QuestaoDao qd = new QuestaoDao();
		
		Questao q = qd.select(idQuestao);

		return buildResponse(Status.OK, q);
	}

	// public static void main(String[] args) {
	// Questao q = new Questao("Quantos cocos uma andorinha carrega?", 10, 5);
	// List<Opcao> opcoes = new ArrayList<>();
	// opcoes.add(new Opcao("1", false));
	// opcoes.add(new Opcao("0", true));
	// opcoes.add(new Opcao("Europeias ou americanas?", false));
	// opcoes.add(new Opcao("Guarapari Búzios", true));
	//
	// q.setOpcoes(opcoes, opcoes.get(2));
	//
	// QuestaoDao qd = new QuestaoDao();
	//
	// qd.insert(q);
	// qd.tearDown();
	// }
}
