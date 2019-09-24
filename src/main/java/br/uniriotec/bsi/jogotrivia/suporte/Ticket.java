package br.uniriotec.bsi.jogotrivia.suporte;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import br.uniriotec.bsi.jogotrivia.administrativo.Privilegio;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

@Entity
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String assunto;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('ABERTO', 'RESOLVIDO', 'AGUARDANDO_SOLICITANTE', 'AGUARDANDO_SUPORTE') DEFAULT 'ABERTO'")
	private EstadoTicket estadoAtual = EstadoTicket.ABERTO;
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderColumn
	private List<Mensagem> mensagens;
	@ManyToOne(optional = false)
	private Usuario solicitante;
	@ManyToOne(optional = true)
	private Usuario solucionador;

	public Ticket() {

	}

	public Ticket(String assunto, Usuario solicitante) {
		super();
		this.assunto = assunto;
		this.solicitante = solicitante;
	}

	public void adicionarMensagem(Mensagem mensagem) {
		if (mensagens == null) {
			mensagens = new ArrayList<>();
		}

		mensagens.add(mensagem);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public EstadoTicket getEstadoAtual() {
		return estadoAtual;
	}

	public void setEstadoAtual(EstadoTicket estadoAtual) {
		this.estadoAtual = estadoAtual;
	}

	public List<Mensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}

	public Usuario getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Usuario solicitante) {
		this.solicitante = solicitante;
	}

	public Usuario getSolucionador() {
		return solucionador;
	}

	public void setSolucionador(Usuario solucionador) {
		if (!solucionador.getPrivilegio().equals(Privilegio.MODERADOR)) {
			throw new IllegalArgumentException("O solucionador de um Ticket deve ter privilégios de MODERADOR");
		}
		this.solucionador = solucionador;
	}

}
