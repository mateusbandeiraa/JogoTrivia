package br.uniriotec.bsi.jogotrivia.administrativo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.collection.internal.PersistentBag;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.financeiro.Lancamento;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewPublico;
import br.uniriotec.bsi.jogotrivia.suporte.Mensagem;
import br.uniriotec.bsi.jogotrivia.suporte.Ticket;

/**
 * 
 * @author Mateus Bandeira
 *         Criado em 13/09/19
 *
 */
@Entity
public class Usuario {

	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewPublico.class)
	private int id;

	@Column(nullable = false)
	@JsonView(ViewPublico.class)
	private String nome;

	private transient String senha;

	@Column(nullable = false)
	private String hashSenha;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false, columnDefinition = "datetime default current_timestamp")
	private Date dataCadastro;

	@Column(nullable = false, columnDefinition = " Jboolean default true")
	private boolean ativo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('USUARIO', 'MODERADOR', 'ANFITRIAO') DEFAULT 'USUARIO'")
	@JsonView(ViewPublico.class)
	private Privilegio privilegio = Privilegio.USUARIO;

	@OneToMany(orphanRemoval = true, targetEntity = Lancamento.class)
	private List<Lancamento> lancamentos;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	@JsonView(ViewAutenticado.class)
	private int quantidadeAjudasBomba;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	@JsonView(ViewAutenticado.class)
	private int quantidadeAjudasPopular;

	@Column(nullable = false, columnDefinition = "INT DEFAULT 0")
	@JsonView(ViewAutenticado.class)
	private int quantidadeAjudasBonus;

	public Usuario(String nome, String hashSenha, String email, Date dataCadastro, boolean ativo) {
		this(nome, email, dataCadastro, ativo);
		setHashSenha(hashSenha);
	}

	public Usuario(String nome, String email, Date dataCadastro, boolean ativo) {
		this();
		setNome(nome);
		setEmail(email);
		setDataCadastro(dataCadastro);
		setAtivo(ativo);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Usuario) {
			Usuario usuario = (Usuario) obj;
			if (usuario.getId() != 0 && this.getId() != 0) {
				return usuario.getId() == this.getId();
			}
		}
		return super.equals(obj);
	}

	public TokenAutenticacao autenticar(String senha) throws IllegalArgumentException {
		if (BCrypt.checkpw(senha, hashSenha)) {
			return new TokenAutenticacao(this);
		} else {
			throw new IllegalArgumentException("Senha incorreta");
		}
	}

	public Ticket abrirTicket(String assunto, String textoMensagem) {
		Ticket t = new Ticket(assunto, this);
		Mensagem m = new Mensagem(textoMensagem, this);
		t.adicionarMensagem(m);
		return t;
	}

	@XmlElement
	@JsonView(ViewAutenticado.class)
	public BigDecimal getSaldo() {
		BigDecimal saldo = BigDecimal.ZERO;

		for (Lancamento lancamento : this.getLancamentos()) {
			saldo = saldo.add(lancamento.getValorEfetivo());
		}

		return saldo.setScale(2);
	}

	public Usuario() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) throws IllegalArgumentException {
		if (nome == null || nome.isEmpty()) {
			throw new IllegalArgumentException("Nome inválido");
		}
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public String getHashSenha() {
		return hashSenha;
	}

	public void setHashSenha(String hashSenha) {
		this.hashSenha = hashSenha;
	}

	public void setSenha(String senha) throws IllegalArgumentException {
		if (senha == null || senha.isEmpty()) {
			throw new IllegalArgumentException("Senha inválida");
		}
		this.setHashSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) throws IllegalArgumentException {
		if (!email.matches(EMAIL_REGEX)) {
			throw new IllegalArgumentException("E-mail inválido");
		}
		this.email = email;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Privilegio getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Privilegio privilegio) {
		this.privilegio = privilegio;
	}

	public List<Lancamento> getLancamentos() {
		if (lancamentos == null || lancamentos instanceof PersistentBag) {
			this.setLancamentos(new UsuarioDao().selectLancamentos(this).getLancamentos());
		}
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	public int getQuantidadeAjudasBomba() {
		return quantidadeAjudasBomba;
	}

	public void setQuantidadeAjudasBomba(int quantidadeAjudasBomba) {
		this.quantidadeAjudasBomba = quantidadeAjudasBomba;
	}

	public void incrementarQuantidadeAjudasBomba(int incremento) {
		this.setQuantidadeAjudasBomba(this.quantidadeAjudasBomba + incremento);
	}

	public int getQuantidadeAjudasPopular() {
		return quantidadeAjudasPopular;
	}

	public void setQuantidadeAjudasPopular(int quantidadeAjudasPopular) {
		this.quantidadeAjudasPopular = quantidadeAjudasPopular;
	}

	public void incrementarQuantidadeAjudasPopular(int incremento) {
		this.setQuantidadeAjudasPopular(this.quantidadeAjudasPopular + incremento);
	}

	public int getQuantidadeAjudasBonus() {
		return quantidadeAjudasBonus;
	}

	public void setQuantidadeAjudasBonus(int quantidadeAjudasBonus) {
		this.quantidadeAjudasBonus = quantidadeAjudasBonus;
	}

	public void incrementarQuantidadeAjudasBonus(int incremento) {
		this.setQuantidadeAjudasBonus(this.quantidadeAjudasBonus + incremento);
	}

}
