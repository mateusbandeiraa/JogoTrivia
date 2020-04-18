package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaAberta;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaEncerrada;

@Entity
public class Questao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewAnfitriao.class)
	private int id;

	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private String textoPergunta;

	@JsonView(ViewAnfitriao.class)
	private int tempoDisponivel; // em segundos

	@ManyToOne(optional = false)
	@JsonView(ViewAnfitriao.class)
	private Usuario autor;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private List<Opcao> opcoes;

	public Questao() {

	}

	public Questao(String textoPergunta, int tempoDisponivel, Usuario autor) {
		this();
		this.textoPergunta = textoPergunta;
		this.tempoDisponivel = tempoDisponivel;
		this.autor = autor;
	}

	public Questao(String textoPergunta, int tempoDisponivel, List<Opcao> opcoes, Usuario autor) {
		this(textoPergunta, tempoDisponivel, autor);
		this.opcoes = opcoes;
	}

//	@XmlElement
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
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

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

//	@XmlElement
	@JsonView({ ViewAnfitriao.class, ViewRodadaEncerrada.class })
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