package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Rodada {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne(optional = false)
	private Questao questao;
	@Column(nullable = false)
	private int tempoExtra; // Em segundos
	@Column
	private Date dataInicio;
	// List<Palpite>

	public int getId() {
		return id;
	}

	public Rodada() {
		super();
	}

	public Rodada(Questao questao) {
		this();
		this.questao = questao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Questao getQuestao() {
		return questao;
	}

	public void setQuestao(Questao questao) {
		this.questao = questao;
	}

	public int getTempoExtra() {
		return tempoExtra;
	}

	public void setTempoExtra(int tempoExtra) {
		this.tempoExtra = tempoExtra;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

}
