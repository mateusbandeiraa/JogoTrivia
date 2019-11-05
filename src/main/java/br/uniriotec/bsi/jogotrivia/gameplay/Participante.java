package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

@Entity
public class Participante {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String nickname;
	@Column(nullable = false)
	private Date dataCriacao;
	@Column(nullable = false)
	private String ip;
	@Column
	private String localizacao;
	@ManyToOne(optional = false)
	private Usuario usuario;
	@ManyToOne(optional = false)
	@JsonBackReference
	private Partida partida;

	public Participante() {
		super();
	}

	public Participante(String nickname, Date dataCriacao, String ip, String localizacao, Usuario usuario,
			Partida partida) {
		this();
		this.nickname = nickname;
		this.dataCriacao = dataCriacao;
		this.ip = ip;
		this.localizacao = localizacao;
		this.usuario = usuario;
		this.partida = partida;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

}
