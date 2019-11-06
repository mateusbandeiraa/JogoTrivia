package br.uniriotec.bsi.jogotrivia.gameplay;

import java.math.BigDecimal;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonBackReference;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

@Entity
public class Partida {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String nome;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderColumn
	private List<Rodada> rodadas = new ArrayList<Rodada>();
	@OneToOne
	private Rodada rodadaAtual;
	@Column(nullable = false)
	private Date dataInicio;
	@Column(nullable = false, columnDefinition = "ENUM('AGENDADA', 'DISPONIVEL', 'EM_ANDAMENTO', 'ENCERRADA')")
	private EstadoPartida estadoAtual;
	@Column
	private BigDecimal premio = BigDecimal.ZERO;
	@Column
	private BigDecimal entrada = BigDecimal.ZERO;

	@ManyToOne(optional = false)
	private Usuario anfitriao;

	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Participante> participantes = new ArrayList<>();

	public Partida() {
		super();
	}

	public Partida(String nome, Date dataInicio, Usuario anfitriao) {
		this();
		this.nome = nome;
		this.dataInicio = dataInicio;
		this.setAnfitriao(anfitriao);
		if (this.dataInicio.after(new Date())) {
			this.estadoAtual = EstadoPartida.ENCERRADA;
		} else {
			this.estadoAtual = EstadoPartida.AGENDADA;
		}
	}

	public Partida(String nome, Date dataInicio, Usuario anfitriao, BigDecimal premio, BigDecimal entrada) {
		this(nome, dataInicio, anfitriao);
		this.premio = premio;
		this.entrada = entrada;
	}

	@XmlElement
	public int quantidadeRodadas() {
		return this.getRodadas().size();
	}

	@XmlElement
	public int quantidadeParticipantes() {
		return this.getParticipantes().size();
	}

	@XmlElement
	public int numeroRodadaAtual() {
		if (this.getEstadoAtual() != EstadoPartida.EM_ANDAMENTO || rodadaAtual == null) {
			return -1;
		}

		return this.getRodadas().indexOf(this.getRodadaAtual()) + 1;
	}

	public void proximaQuestao() {
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

	public List<Participante> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<Participante> participantes) {
		this.participantes = participantes;
	}

}
