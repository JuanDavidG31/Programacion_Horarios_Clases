package co.edu.unbosque.programacion_horarios.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.programacion_horarios.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUserName(String userName);

    public void deleteByUserName(String userName);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByName(String name);

	public Optional<User> findById(Integer userId);
	



}
