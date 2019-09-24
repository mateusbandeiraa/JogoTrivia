package br.uniriotec.bsi.jogotrivia.suporte;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

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
	private Usuario autor;

	public Mensagem() {

	}

	public Mensagem(String conteudo, Usuario autor, Date dataCriacao) {
		super();
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

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

}
