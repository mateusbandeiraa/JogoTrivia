package br.uniriotec.bsi.jogotrivia.administrativo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.view.ViewTokenAutenticacao;

@Entity
public class TokenAutenticacao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@OneToOne
	@JoinColumn
	@JsonView(ViewTokenAutenticacao.Proprio.class)
	private Usuario usuario;
	
	@Column(nullable = false)
	@JsonView(ViewTokenAutenticacao.Proprio.class)
	private String token;
	
	@Column(nullable = false)
	@JsonView(ViewTokenAutenticacao.Proprio.class)
	private Date dataCriacao;
	
	@Column(nullable = false)
	@JsonView(ViewTokenAutenticacao.Proprio.class)
	private Date dataExpiracao;

	private static final int MS_EM_24_HORAS = 1000 * 60 * 60 * 24;

	public TokenAutenticacao() {

	}

	public TokenAutenticacao(Usuario usuario) {
		this();
		this.usuario = usuario;
		this.token = new BigInteger(130, new SecureRandom()).toString(32);
		this.dataCriacao = new Date();
		this.dataExpiracao = new Date(this.dataCriacao.getTime() + MS_EM_24_HORAS);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

}
