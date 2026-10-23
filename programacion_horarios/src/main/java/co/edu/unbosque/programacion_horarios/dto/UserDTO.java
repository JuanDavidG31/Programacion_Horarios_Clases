package co.edu.unbosque.programacion_horarios.dto;

import co.edu.unbosque.programacion_horarios.model.Role;

public class UserDTO {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String correo;
	private String userName;
	private String password;
	private Role role;
	private String identification;
	private String code;
	
	
	public UserDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public UserDTO(Long id, String firstName, String lastName, String correo, String userName, String password,
			Role role, String identification, String code) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.correo = correo;
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.identification = identification;
		this.code = code;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	

}
