package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaAberta;

@Entity
public class Rodada {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private int id;

	@ManyToOne(optional = false)
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private Questao questao;

	@Column(nullable = false)
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private int tempoExtra; // Em segundos

	@Column
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private Date dataInicio;

	@OneToMany(fetch = FetchType.EAGER)
	private List<Palpite> palpites = new ArrayList<>();

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
	public int quantidadeRespostas() {
		return getPalpites().size();
	}

	@XmlElement
	@JsonView(ViewPublico.class)
	public int tempoTotal() {
		if (this.getQuestao() == null) {
			return 0;
		}
		return this.getTempoExtra() + this.getQuestao().getTempoDisponivel();
	}

	@XmlElement
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	public Date dataLimite() {
		if (getDataInicio() == null) {
			return new Date(0);
		}
		return new Date(getDataInicio().getTime() + tempoTotal() * 1000);
	}

	@XmlElement
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	public boolean estaAberta() {
		Date dataLimite = this.dataLimite();
		return new Date().before(dataLimite);
	}

	public List<Palpite> getPalpites() {
		return palpites;
	}

	public void setPalpites(List<Palpite> palpites) {
		this.palpites = palpites;
	}

}
