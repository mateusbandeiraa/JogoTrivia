package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Tema {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String nome;
	@ManyToMany
	Set<Questao> questoes;

	public Tema() {
		super();
		this.questoes = new HashSet<>();
	}

	public Tema(String nome) {
		this();
		this.nome = nome;
	}

	public Tema(int id, String nome) {
		this(nome);
		this.id = id;
	}

	protected void adicionarQuestao(Questao questao, boolean cascata) {
		Boolean novoRegistro = this.questoes.add(questao);
		if (cascata && novoRegistro) {
			questao.adicionarTemas(this);
		}
	}

	public void adicionarQuestao(Questao questao) {
		adicionarQuestao(questao, true);
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

	public Set<Questao> getQuestoes() {
		return questoes;
	}

	public void setQuestoes(Set<Questao> questoes) {
		this.questoes = questoes;
	}

}
