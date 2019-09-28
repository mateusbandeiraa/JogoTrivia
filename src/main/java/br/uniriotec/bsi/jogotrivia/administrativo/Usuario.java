package br.uniriotec.bsi.jogotrivia.administrativo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.mindrot.jbcrypt.BCrypt;

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
	private int id;
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String hashSenha;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false, columnDefinition = "datetime default current_timestamp")
	private Date dataCadastro;
	@Column(nullable = false, columnDefinition = "boolean default true")
	private boolean ativo;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('USUARIO', 'MODERADOR') DEFAULT 'USUARIO'")
	private Privilegio privilegio = Privilegio.USUARIO;

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

	public TokenAutenticacao autenticar(String senha) throws IllegalArgumentException {
		if (BCrypt.checkpw(senha, hashSenha)) {
			return new TokenAutenticacao(this);
		} else {
			throw new IllegalArgumentException("Senha incorreta");
		}
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

}
