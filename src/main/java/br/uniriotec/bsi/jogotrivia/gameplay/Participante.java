package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;

@Entity
public class Participante {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewAutenticado.class)
	private int id;
	
	@Column(nullable = true)
	@JsonView(ViewAutenticado.class)
	private String handle;
	
	@Column(nullable = false)
	private Date dataCriacao;
	
	@Column(nullable = false)
	private String ip;
	
	@Column
	@JsonView(ViewAutenticado.class)
	private String localizacao;
	
	@ManyToOne(optional = false)
	@JsonView(ViewAutenticado.class)
	private Usuario usuario;
	
	@ManyToOne(optional = false)
	@JsonBackReference
	private Partida partida;

	public Participante() {
		super();
	}

	public Participante(String handle, Date dataCriacao, String ip, String localizacao, Usuario usuario,
			Partida partida) {
		this();
		this.handle = handle;
		this.dataCriacao = dataCriacao;
		this.ip = ip;
		this.localizacao = localizacao;
		this.usuario = usuario;
		this.partida = partida;
	}

	@JsonView(ViewPublico.class)
	@JsonProperty("nickname")
	public String getNickname() {
		if (this.getHandle() != null) {
			return this.getHandle();
		}
		return this.getUsuario().getNome();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
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
