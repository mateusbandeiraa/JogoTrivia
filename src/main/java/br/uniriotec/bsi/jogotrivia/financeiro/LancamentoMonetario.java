package br.uniriotec.bsi.jogotrivia.financeiro;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class LancamentoMonetario extends Lancamento {

	private String codigoAutorizacao;
	private String checkoutCode;
	private boolean estaPago;

	public LancamentoMonetario(BigDecimal valor, DirecaoLancamento direcao, Date data) {
		super(valor, direcao, data);
	}

	public LancamentoMonetario(BigDecimal valor, DirecaoLancamento direcao) {
		super(valor, direcao);
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

			checkoutCode = doc.getElementsByTagName("code").item(0).getTextContent();
			estaPago = doc.getElementsByTagName("status").item(0).getTextContent().equals("3");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return estaPago;
	}
	
	public String getUrlCheckout() {
		return "https://sandbox.pagseguro.uol.com.br/v2/checkout/payment.html?code=" + this.codigoAutorizacao;
	}

	public static void main(String[] args) {
		LancamentoMonetario lm = new LancamentoMonetario(BigDecimal.TEN, DirecaoLancamento.ENTRADA);
		
		lm.setCodigoAutorizacao("36705ACF743646DC945ED72C3FC71973");
		System.out.println(lm.verificarPagamento());
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
