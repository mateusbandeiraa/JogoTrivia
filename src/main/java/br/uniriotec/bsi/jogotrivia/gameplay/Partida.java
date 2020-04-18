package br.uniriotec.bsi.jogotrivia.gameplay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.service.Views;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaAberta;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaEncerrada;

@Entity
public class Partida {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(Views.ViewPublico.class)
	private int id;

	@Column(nullable = false)
	@JsonView(Views.ViewPublico.class)
	private String nome;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonView(ViewAnfitriao.class)
	private List<Rodada> rodadas = new ArrayList<Rodada>();

	@OneToOne
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private Rodada rodadaAtual;

	@Column(nullable = false)
	@JsonView(Views.ViewPublico.class)
	private Date dataInicio;

	@Column(nullable = false, columnDefinition = "ENUM('AGENDADA', 'DISPONIVEL', 'EM_ANDAMENTO', 'ENCERRADA') DEFAULT 'DISPONIVEL'")
	@Enumerated(EnumType.STRING)
	@JsonView(Views.ViewPublico.class)
	private EstadoPartida estadoAtual = EstadoPartida.DISPONIVEL;

	@Column
	@JsonView(ViewPublico.class)
	private BigDecimal premio = BigDecimal.ZERO;

	@Column
	@JsonView(ViewPublico.class)
	private BigDecimal entrada = BigDecimal.ZERO;

	@ManyToOne(optional = false)
	@JsonView(ViewPublico.class)
	private Usuario anfitriao;

	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonView(ViewPublico.class)
	@JsonBackReference
	private Set<Participante> participantes = new HashSet<>();

	public Partida() {
		super();
	}

	public Partida(String nome, Date dataInicio, Usuario anfitriao) {
		this();
		this.nome = nome;
		this.dataInicio = dataInicio;
		this.setAnfitriao(anfitriao);
	}

	public Partida(String nome, Date dataInicio, Usuario anfitriao, BigDecimal premio, BigDecimal entrada) {
		this(nome, dataInicio, anfitriao);
		this.premio = premio;
		this.entrada = entrada;
	}

	// @XmlElement
	@JsonView(Views.ViewPublico.class)
	public int quantidadeRodadas() {
		return this.getRodadas().size();
	}

	// @XmlElement
	@JsonView(Views.ViewPublico.class)
	public int quantidadeParticipantes() {
		return this.getParticipantes().size();
	}

	// @XmlElement
	@JsonView(Views.ViewPublico.class)
	public int numeroRodadaAtual() {
		if (this.getEstadoAtual() != EstadoPartida.EM_ANDAMENTO || rodadaAtual == null) {
			return -1;
		}

		return this.getRodadas().indexOf(this.getRodadaAtual()) + 1;
	}

	public void proximaQuestao() {
		if (this.getEstadoAtual() == EstadoPartida.DISPONIVEL) {
			this.setEstadoAtual(EstadoPartida.EM_ANDAMENTO);
		}
		int indiceRodadaAtual = this.getRodadas().indexOf(this.getRodadaAtual());

		if (indiceRodadaAtual + 1 == this.quantidadeRodadas()) {
			throw new IllegalStateException("A partida não tem mais rodadas.");
		}
		this.setRodadaAtual(this.getRodadas().get(indiceRodadaAtual + 1));
	}

	public void inserirRodada(Questao questao) {
		Rodada rodada = new Rodada(questao);
		this.rodadas.add(rodada);
	}

	public void inscreverParticipante(Participante participante)
			throws IllegalStateException, IllegalArgumentException {
		if (this.estadoAtual != EstadoPartida.DISPONIVEL) {
			throw new IllegalStateException("A partida não está disponível.");
		}

		for (Participante participanteInscrito : this.getParticipantes()) {
			if (participanteInscrito.getUsuario().equals(participante.getUsuario())) {
				throw new IllegalArgumentException("Este usuário já está inscrito nesta partida.");
			}
			if (participanteInscrito.getNickname().equalsIgnoreCase(participante.getNickname())) {
				throw new IllegalArgumentException("Já existe um usuário com este nickname nesta partida.");
			}
		}

		participante.setPartida(this);
		this.getParticipantes().add(participante);
	}

	public boolean usuarioEstaInscrito(Usuario usuario) {
		for (Participante participante : getParticipantes()) {
			if (participante.getUsuario().equals(usuario)) {
				return true;
			}
		}
		return false;
	}

	// @XmlElement
	@JsonView({ ViewAnfitriao.class, ViewRodadaEncerrada.class })
	public List<Participante> ranking() {
		List<Participante> lista = new ArrayList<>(this.getParticipantes());
		Collections.sort(lista, new Comparator<Participante>() {

			@Override
			public int compare(Participante o1, Participante o2) {
				return o1.pontuacaoTotal() - o2.pontuacaoTotal();
			}
		});

		List<Participante> top10 = lista.subList(0, Math.min(10, lista.size()));
		return top10;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Rodada> getRodadas() {
		return rodadas;
	}

	public void setRodadas(List<Rodada> rodadas) {
		this.rodadas = rodadas;
	}

	public Rodada getRodadaAtual() {
		return rodadaAtual;
	}

	public void setRodadaAtual(Rodada rodadaAtual) {
		rodadaAtual.setDataInicio(new Date());
		this.rodadaAtual = rodadaAtual;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public EstadoPartida getEstadoAtual() {
		return estadoAtual;
	}

	public void setEstadoAtual(EstadoPartida estadoAtual) {
		this.estadoAtual = estadoAtual;
	}

	public BigDecimal getPremio() {
		return premio;
	}

	public void setPremio(BigDecimal premio) {
		this.premio = premio;
	}

	public BigDecimal getEntrada() {
		return entrada;
	}

	public void setEntrada(BigDecimal entrada) {
		this.entrada = entrada;
	}

	public Usuario getAnfitriao() {
		return anfitriao;
	}

	public void setAnfitriao(Usuario anfitriao) {
		if (!(anfitriao.getPrivilegio() == Privilegio.ANFITRIAO || anfitriao.getPrivilegio() == Privilegio.MODERADOR)) {
			throw new IllegalArgumentException("O anfitrião não tem os privilégios necessários.");
		}
		this.anfitriao = anfitriao;
	}

	public Set<Participante> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Set<Participante> participantes) {
		this.participantes = participantes;
	}

}
