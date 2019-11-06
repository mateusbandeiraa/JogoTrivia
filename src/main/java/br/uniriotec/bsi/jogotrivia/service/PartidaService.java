package br.uniriotec.bsi.jogotrivia.service;

import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.buildResponse;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterIp;
import static br.uniriotec.bsi.jogotrivia.service.ServiceUtils.obterUsuarioPorSecurityContext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.x.protobuf.Mysqlx.Ok;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.gameplay.EstadoPartida;
import br.uniriotec.bsi.jogotrivia.gameplay.Participante;
import br.uniriotec.bsi.jogotrivia.gameplay.Partida;
import br.uniriotec.bsi.jogotrivia.gameplay.Questao;
import br.uniriotec.bsi.jogotrivia.persistence.PartidaDao;
import br.uniriotec.bsi.jogotrivia.persistence.QuestaoDao;
import br.uniriotec.bsi.jogotrivia.service.ServiceUtils.ParExclusoes;

@Path("/partidaService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class PartidaService {

	public static final ParExclusoes EXCLUSOES_PARTIDA = new ParExclusoes(Partida.class, "rodadas", "rodadaAtual",
			"anfitriao");

	@Context
	SecurityContext securityContext;

	private Usuario usuarioAutenticado;

	private Usuario getUsuarioAutenticado() {
		if (usuarioAutenticado == null) {
			usuarioAutenticado = obterUsuarioPorSecurityContext(securityContext);
		}

		return usuarioAutenticado;
	}

	@GET
	public Response getPartidas(@QueryParam("idPartida") Integer idPartida) {
		if (idPartida == null) {
			List<Partida> partidas = new PartidaDao().selectAll();
			return buildResponse(Status.OK, partidas);
		} else {
			Partida partida = new PartidaDao().select(idPartida);
			if(partida != null) {
				return buildResponse(Status.OK, partida);
			} else {
				return buildResponse(Status.NOT_FOUND);
			}
		}
	}
	
	@Path("avancarQuestao")
	@POST
	@Autenticado({Privilegio.ANFITRIAO, Privilegio.MODERADOR})
	public Response proximaQuestao(Integer idPartida) {
		PartidaDao pd = new PartidaDao();
		
		Partida partida = pd.select(idPartida);
		partida.proximaQuestao();
		
		pd.update(partida);
		
		return buildResponse(Status.OK, partida);
	}

	@POST
	@Autenticado({ Privilegio.MODERADOR, Privilegio.ANFITRIAO })
	public Response criarPartida(JsonNode message) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Partida partida = mapper.treeToValue(message.get("partida"), Partida.class);
		String[] idsQuestoes = mapper.treeToValue(message.get("idsQuestoes"), String[].class);

		partida.setEstadoAtual(EstadoPartida.AGENDADA);

		PartidaDao pd = new PartidaDao();
		for (String id : idsQuestoes) {
			Questao q = new Questao();
			q.setId(Integer.valueOf(id));
			partida.inserirRodada(q);
		}
		pd.insert(partida);
		return buildResponse(Status.ACCEPTED, "Partida criada");
	}

	@Path("criarPartidaPadrao")
	@POST
	@Autenticado({ Privilegio.MODERADOR, Privilegio.ANFITRIAO })
	public Response criarPartidaPadrao() {
		QuestaoDao qd = new QuestaoDao();

		List<Questao> questoes = qd.selectAll();

		Partida p = new Partida("Conhecimentos gerais", new Date(new Date().getTime() + 1000 * 60 * 5),
				getUsuarioAutenticado(), new BigDecimal(50), new BigDecimal(10));

		for (Questao questao : questoes) {
			p.inserirRodada(questao);
		}

		new PartidaDao().insert(p);

		return buildResponse(Status.ACCEPTED);
	}

	@Path("cadastrarParticipante")
	@POST
	@Autenticado
	public Response cadastrarParticipante(Participante participanteJson, @Context HttpServletRequest request) {
		Participante participante = new Participante();
		String ip = request.getRemoteAddr();
		String headerIp = obterIp(request);

		if (headerIp != null) {
			ip = headerIp;
		}
		if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "9.9.9.9";
		}
		
		participante.setNickname(participanteJson.getNickname());
		participante.setPartida(participanteJson.getPartida());
		participante.setDataCriacao(new Date());
		participante.setUsuario(getUsuarioAutenticado());
		participante.setIp(ip);

		String local;
		try {
			local = ServiceUtils.obterLocalizacao(ip);
		} catch (Exception ex) {
			ex.printStackTrace();
			local = "Área 42";
		}

		participante.setLocalizacao(local);
		
		PartidaDao pd = new PartidaDao();
		
		Partida partida = pd.select(participanteJson.getPartida().getId());
		
		if(partida == null) {
			buildResponse(Status.BAD_REQUEST, "Partida inválida");
		}
		try {
			partida.inscreverParticipante(participante);
		} catch (IllegalStateException | IllegalArgumentException ex) {
			return buildResponse(Status.NOT_ACCEPTABLE, ex.getMessage());
		}
		
		pd.update(partida);
		
		return buildResponse(participante);
	}
}
