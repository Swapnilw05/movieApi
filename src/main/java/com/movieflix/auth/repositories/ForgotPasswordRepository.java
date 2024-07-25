package com.movieflix.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.movieflix.auth.entities.ForgotPassword;
import com.movieflix.auth.entities.User;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer>{

	@Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.otp = ?2")
	Optional<ForgotPassword> findByOtpAndUser(int otp, User user);
}
