package br.uniriotec.bsi.jogotrivia.financeiro;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.administrativo.User;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;

@Entity
public class LancamentoMonetario extends Lancamento {
	@JsonView(ViewAutenticado.class)
	private String codigoAutorizacao;
	@JsonView(ViewAutenticado.class)
	private String checkoutCode;
	@JsonView(ViewAutenticado.class)
	private boolean estaPago;

	public LancamentoMonetario() {
		super();
	}

	public LancamentoMonetario(BigDecimal valor, DirecaoLancamento direcao, User usuario, Date data) {
		super(valor, direcao, usuario, data);
	}

	public LancamentoMonetario(BigDecimal valor, User usuario, DirecaoLancamento direcao) {
		super(valor, direcao, usuario);
	}

	public String solicitarAutorizacao() {
		try {
			URL url = new URL(
					"https://ws.sandbox.pagseguro.uol.com.br/v2/checkout?email=mateus.albertin@uniriotec.br&token=DF0D84C820ED417AAA9105AB2931835C");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");

			con.setDoOutput(true);

			DataOutputStream dos = new DataOutputStream(con.getOutputStream());

			dos.writeBytes(
					"currency=BRL&itemId1=0&itemDescription1=" + this.getValor().setScale(2) + "+creditos&itemAmount1="
							+ this.getValor().setScale(2) + "&itemQuantity1=1&reference=" + this.getId());

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setNamespaceAware(false);

			DocumentBuilder docBuilder = dbf.newDocumentBuilder();

			Document doc = docBuilder.parse(con.getInputStream());

			con.disconnect();

			codigoAutorizacao = doc.getElementsByTagName("code").item(0).getTextContent();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return codigoAutorizacao;
	}

	public boolean verificarPagamento() {
		try {
			URL url = new URL("https://ws.sandbox.pagseguro.uol.com.br/v3/transactions/?reference=" + this.getId()
					+ "&email=mateus.albertin@uniriotec.br&token=DF0D84C820ED417AAA9105AB2931835C");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setNamespaceAware(false);

			DocumentBuilder docBuilder = dbf.newDocumentBuilder();

			Document doc = docBuilder.parse(con.getInputStream());

			con.disconnect();

			try {
				checkoutCode = doc.getElementsByTagName("code").item(0).getTextContent();
				estaPago = doc.getElementsByTagName("status").item(0).getTextContent().equals("3");
			} catch (NullPointerException ex) {
				// nada
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return estaPago;
	}

//	@XmlElement
	@JsonView(ViewAutenticado.class)
	@JsonProperty("urlCheckout")
	public String getUrlCheckout() {
		return "https://sandbox.pagseguro.uol.com.br/v2/checkout/payment.html?code=" + this.codigoAutorizacao;
	}
	
	public BigDecimal getValorEfetivo() {
		if(!estaPago) {
			return BigDecimal.ZERO;
		}
		return this.getDirecao().equals(DirecaoLancamento.ENTRADA) ? this.getValor() : this.getValor().negate();
	}

	public String getCodigoAutorizacao() {
		return codigoAutorizacao;
	}

	public void setCodigoAutorizacao(String codigoAutorizacao) {
		this.codigoAutorizacao = codigoAutorizacao;
	}

	public String getCheckoutCode() {
		return checkoutCode;
	}

	public void setCheckoutCode(String checkoutCode) {
		this.checkoutCode = checkoutCode;
	}

	public boolean isEstaPago() {
		return estaPago;
	}

	public void setEstaPago(boolean estaPago) {
		this.estaPago = estaPago;
	}

}
