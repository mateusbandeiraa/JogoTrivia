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

@Entity
public abstract class Lancamento {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private BigDecimal valor = BigDecimal.ZERO;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('ENTRADA', 'SAIDA')")
	private DirecaoLancamento direcao = DirecaoLancamento.ENTRADA;
	private Date data;

	public Lancamento() {
		super();
	}

	public Lancamento(BigDecimal valor, DirecaoLancamento direcao, Date data) {
		this();
		this.valor = valor;
		this.direcao = direcao;
		this.data = data;
	}

	public Lancamento(BigDecimal valor, DirecaoLancamento direcao) {
		this(valor, direcao, new Date());
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

}
