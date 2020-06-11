package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.User;
import br.uniriotec.bsi.jogotrivia.persistence.PalpiteDao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewHistorico;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaEncerrada;

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
	@JsonView({ ViewAutenticado.class, ViewHistorico.class })
	private Date dataCriacao;

	@Column(nullable = false)
	private String ip;

	@Column
	@JsonView({ ViewAutenticado.class, ViewRodadaEncerrada.class, ViewAnfitriao.class })
	private String localizacao;

	@ManyToOne(optional = false)
	@JsonView(ViewAutenticado.class)
	private User usuario;

	@ManyToOne(optional = false)
	@JsonBackReference
	@JsonView({ ViewAutenticado.class, ViewHistorico.class })
	private Partida partida;

	@OneToMany
	private List<Palpite> palpites = new ArrayList<>();

	public Participante() {
		super();
	}

	public Participante(String handle, Date dataCriacao, String ip, String localizacao, User usuario,
			Partida partida) {
		this();
		this.handle = handle;
		this.dataCriacao = dataCriacao;
		this.ip = ip;
		this.localizacao = localizacao;
		this.usuario = usuario;
		this.partida = partida;
	}

	@JsonView({ ViewAutenticado.class, ViewHistorico.class })
	@JsonProperty("nickname")
	public String getNickname() {
		if (this.getHandle() != null) {
			return this.getHandle();
		}
		return this.getUsuario().getName();
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

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public List<Palpite> getPalpites() {
		return new PalpiteDao().selectByParticipante(this.getId());
	}

	public void setPalpites(List<Palpite> palpites) {
		this.palpites = palpites;
	}

//	@XmlElement
	@JsonView({ ViewAutenticado.class, ViewRodadaEncerrada.class, ViewAnfitriao.class, ViewHistorico.class })
	public int pontuacaoTotal() {
		int pontuacao = 0;
		for (Palpite palpite : this.getPalpites()) {
			pontuacao += palpite.pontuacao();
		}
		return pontuacao;
	}

}
