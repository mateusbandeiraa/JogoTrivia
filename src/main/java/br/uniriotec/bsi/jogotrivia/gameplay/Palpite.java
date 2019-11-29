package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Palpite {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private Date data;
	@ManyToOne
	private Rodada rodada;
	@ManyToOne
	@JsonBackReference
	private Participante participante;
	@ManyToOne
	private Opcao opcaoEscolhida;

	public Palpite() {
		super();
	}

	public Palpite(Date data, Rodada rodada, Participante participante, Opcao opcaoEscolhida) {
		this();
		this.data = data;
		this.rodada = rodada;
		this.participante = participante;
		this.opcaoEscolhida = opcaoEscolhida;

		if (!rodada.estaAberta()) {
			throw new IllegalStateException("A rodada não está aberta");
		}
		participante.getPalpites().add(this);
		rodada.getPalpites().add(this);
	}

	public boolean estaCorreto() {
		return rodada.getQuestao().getOpcaoCorreta().equals(opcaoEscolhida);
	}

	public Integer pontuacao() {
		if (!estaCorreto()) {
			return 0;
		}
		int segundosPassados = (int) ((rodada.dataLimite().getTime() - this.data.getTime()) / 1000);
		double porcentagemTempoUtilizado = (double) segundosPassados / (double) rodada.tempoTotal();

		return (int) (100 * porcentagemTempoUtilizado);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Rodada getRodada() {
		return rodada;
	}

	public void setRodada(Rodada rodada) {
		this.rodada = rodada;
	}

	public Participante getParticipante() {
		return participante;
	}

	public void setParticipante(Participante participante) {
		this.participante = participante;
	}

	public Opcao getOpcaoEscolhida() {
		return opcaoEscolhida;
	}

	public void setOpcaoEscolhida(Opcao opcaoEscolhida) {
		this.opcaoEscolhida = opcaoEscolhida;
	}

}
