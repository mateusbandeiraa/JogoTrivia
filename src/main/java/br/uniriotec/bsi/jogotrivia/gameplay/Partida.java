package br.uniriotec.bsi.jogotrivia.gameplay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.xml.bind.annotation.XmlElement;

@Entity
public class Partida {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String nome;
	@OneToMany
	@OrderColumn
	private List<Rodada> rodadas = new ArrayList<Rodada>();
	@OneToOne
	private Rodada rodadaAtual;
	@Column(nullable = false)
	private Date dataInicio;
	@Column(nullable = false)
	private EstadoPartida estadoAtual;
	@Column
	private BigDecimal premio = BigDecimal.ZERO;
	@Column
	private BigDecimal entrada = BigDecimal.ZERO;

	public Partida() {
		super();
	}

	public Partida(String nome, Date dataInicio) {
		this();
		this.nome = nome;
		this.dataInicio = dataInicio;		
	}

	public Partida(String nome, Date dataInicio, BigDecimal premio, BigDecimal entrada) {
		this(nome, dataInicio);
		this.premio = premio;
		this.entrada = entrada;
	}

	@XmlElement
	public int quantidadeRodadas() {
		return 2; //TODO EDIT
	}
	
	@XmlElement
	public int quantidadeParticipantes() {
		return 0; //TODO EDIT
	}
	
	public void inserirRodada(Questao questao) {
		Rodada rodada = new Rodada(questao);
		this.rodadas.add(rodada);
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

}
