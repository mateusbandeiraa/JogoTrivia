package br.uniriotec.bsi.administrativo;

import java.util.Date;

/**
 * 
 * @author Mateus Bandeira
 * Criado em 13/09/19
 *
 */
public class Usuario {
	private String nome;
	private String hashSenha;
	private String email;
	private Date dataCadastro;
	private Boolean ativo;
	
	public Usuario(String nome, String hashSenha, String email, Date dataCadastro, Boolean ativo) {
		super();
		this.nome = nome;
		this.hashSenha = hashSenha;
		this.email = email;
		this.dataCadastro = dataCadastro;
		this.ativo = ativo;
	}
	
	public Usuario() {
		
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getHashSenha() {
		return hashSenha;
	}

	public void setHashSenha(String hashSenha) {
		this.hashSenha = hashSenha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	
	
	
}
