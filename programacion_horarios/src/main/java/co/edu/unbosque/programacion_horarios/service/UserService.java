package co.edu.unbosque.programacion_horarios.service;

import java.util.List;

import co.edu.unbosque.programacion_horarios.dto.UserDTO;
import co.edu.unbosque.programacion_horarios.model.User;

public class UserService implements CRUDOperation<UserDTO, User> {

	@Override
	public UserDTO create(UserDTO data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserDTO> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserDTO updateById(Long id, UserDTO newData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exist(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

}
