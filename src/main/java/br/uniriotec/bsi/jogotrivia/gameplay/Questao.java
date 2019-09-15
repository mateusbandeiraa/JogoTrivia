package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Questao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String textoPergunta;
	private int tempoDisponivel; // em segundos
	private int tempoBonus; // em segundos
	
	@OneToMany(orphanRemoval = true)
	private List<Opcao> opcoes;

	public Questao() {

	}

	public Questao(String textoPergunta, int tempoDisponivel, int tempoBonus, List<Opcao> opcoes) {
		this.textoPergunta = textoPergunta;
		this.tempoDisponivel = tempoDisponivel;
		this.tempoBonus = tempoBonus;
		this.opcoes = opcoes;
	}
	
	public int getQtdOpcoesRemoviveis() {
		int removiveis = 0;
		if(opcoes != null) {
			for(Opcao o : opcoes) {
				if(o.isRemovivel()) {
					removiveis++;
				}
			}
		}
		return removiveis;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTextoPergunta() {
		return textoPergunta;
	}

	public void setTextoPergunta(String textoPergunta) {
		this.textoPergunta = textoPergunta;
	}

	public int getTempoDisponivel() {
		return tempoDisponivel;
	}

	public void setTempoDisponivel(int tempoDisponivel) {
		this.tempoDisponivel = tempoDisponivel;
	}

	public int getTempoBonus() {
		return tempoBonus;
	}

	public void setTempoBonus(int tempoBonus) {
		this.tempoBonus = tempoBonus;
	}

	public List<Opcao> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<Opcao> opcoes) {
		this.opcoes = opcoes;
	}

}