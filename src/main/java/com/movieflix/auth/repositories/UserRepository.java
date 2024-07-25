package com.movieflix.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.movieflix.auth.entities.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByEmail(String username);

	// 2 and 1 are parameters used in method
	@Modifying
	@Transactional
	@Query("update User u set u.password = ?2 where u.email ?1")
	void updatePassword(String email, String password);
	
}
