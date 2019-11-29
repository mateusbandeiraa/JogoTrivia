package br.uniriotec.bsi.jogotrivia.financeiro;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;

@Entity
public abstract class Lancamento {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewAutenticado.class)
	private int id;

	@JsonView(ViewAutenticado.class)
	private BigDecimal valor = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('ENTRADA', 'SAIDA')")
	@JsonView(ViewAutenticado.class)
	private DirecaoLancamento direcao = DirecaoLancamento.ENTRADA;

	@JsonView(ViewAutenticado.class)
	private Date data;

	@ManyToOne(optional = false)
	@JsonBackReference
	private Usuario usuario;

	public Lancamento() {
		super();
	}

	public Lancamento(BigDecimal valor, DirecaoLancamento direcao, Usuario usuario, Date data) {
		this();
		this.valor = valor;
		this.direcao = direcao;
		this.usuario = usuario;
		this.data = data;
	}

	public Lancamento(BigDecimal valor, DirecaoLancamento direcao, Usuario usuario) {
		this(valor, direcao, usuario, new Date());
	}

	public BigDecimal getValorEfetivo() {
		return direcao.equals(DirecaoLancamento.ENTRADA) ? this.getValor() : this.getValor().negate();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor.abs().setScale(2);
	}

	public DirecaoLancamento getDirecao() {
		return direcao;
	}

	public void setDirecao(DirecaoLancamento direcao) {
		this.direcao = direcao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
