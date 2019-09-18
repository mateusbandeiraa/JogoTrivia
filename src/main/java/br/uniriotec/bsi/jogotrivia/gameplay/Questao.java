package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

@Entity
public class Questao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String textoPergunta;
	private int tempoDisponivel; // em segundos

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// @JoinTable()
	@OrderColumn
	private List<Opcao> opcoes;

	public Questao() {

	}

	public Questao(String textoPergunta, int tempoDisponivel) {
		this();
		this.textoPergunta = textoPergunta;
		this.tempoDisponivel = tempoDisponivel;
	}

	public Questao(String textoPergunta, int tempoDisponivel, List<Opcao> opcoes) {
		this(textoPergunta, tempoDisponivel);
		this.opcoes = opcoes;
	}

	public int getQtdOpcoesRemoviveis() {
		int removiveis = 0;
		if (opcoes != null) {
			for (Opcao o : opcoes) {
				if (o.isRemovivel()) {
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

	public List<Opcao> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<Opcao> opcoes, Opcao correta) {
		if (!opcoes.contains(correta)) {
			throw new IllegalArgumentException("A lista de opçoes passadas não contém a opçao correta");
		}
		this.opcoes = opcoes;
		this.setOpcaoCorreta(correta);
	}

	@Transient
	public Opcao getOpcaoCorreta() {
		for (Opcao o : opcoes) {
			if (o.isCorreta()) {
				return o;
			}
		}
		return null;
	}

	private void setOpcaoCorreta(Opcao opcaoCorreta) {
		for (int i = 0; i < opcoes.size(); i++) {
			Opcao opcao = opcoes.get(i);
			if (opcao.equals(opcaoCorreta)) {
				opcao.setCorreta(true);
			} else {
				opcao.setCorreta(false);
			}
		}
	}
}