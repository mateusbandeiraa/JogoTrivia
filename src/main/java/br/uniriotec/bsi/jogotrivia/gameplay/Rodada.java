package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;

@Entity
public class Rodada {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewAnfitriao.class)
	private int id;
	
	@ManyToOne(optional = false)
	@JsonView(ViewAnfitriao.class)
	private Questao questao;
	
	@Column(nullable = false)
	@JsonView(ViewAnfitriao.class)
	private int tempoExtra; // Em segundos
	
	@Column
	@JsonView(ViewAnfitriao.class)
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
	
	@XmlElement
	@JsonView(ViewPublico.class)
	public int quantidadeRespostas(){
		return 0; //TODO edit this
	}
	
	@XmlElement
	@JsonView(ViewPublico.class)
	public int tempoTotal() {
		if(this.getQuestao() == null) {
			return 0;
		}
		return this.getTempoExtra() + this.getQuestao().getTempoDisponivel();
	}

}
