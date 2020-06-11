package br.uniriotec.bsi.jogotrivia.suporte;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.uniriotec.bsi.jogotrivia.administrativo.User;

@Entity
public class Mensagem {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String conteudo;
	@Column(nullable = false)
	private Date dataCriacao;
	@ManyToOne
	private User autor;

	public Mensagem() {
		super();
	}
	
	public Mensagem(String conteudo, User autor) {
		this();
		this.conteudo = conteudo;
		this.dataCriacao = new Date();
		this.autor = autor;
	}

	public Mensagem(String conteudo, User autor, Date dataCriacao) {
		this.conteudo = conteudo;
		this.dataCriacao = dataCriacao;
		this.autor = autor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public User getAutor() {
		return autor;
	}

	public void setAutor(User autor) {
		this.autor = autor;
	}

}
