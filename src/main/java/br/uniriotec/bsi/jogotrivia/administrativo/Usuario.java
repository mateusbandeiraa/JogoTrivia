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

import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;

/**
 * 
 * @author Mateus Bandeira
 *         Criado em 13/09/19
 *
 */
@Entity
public class Usuario {
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
	
	public static void main(String[] args) {
		UsuarioDao ud = new UsuarioDao();
		ud.insert(new Usuario("Mateus", BCrypt.hashpw("1234", BCrypt.gensalt()), "mateusbandeiraa@gmail.com", new Date(), true));
	}

	public Usuario(String nome, String hashSenha, String email, Date dataCadastro, boolean ativo) {
		super();
		this.nome = nome;
		this.hashSenha = hashSenha;
		this.email = email;
		this.dataCadastro = dataCadastro;
		this.ativo = ativo;
	}

	public Usuario() {

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

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getHashSenha() {
		return hashSenha;
	}

	public void setHashSenha(String hashSenha) {
		this.hashSenha = hashSenha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
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
