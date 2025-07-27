package com.hackathon.junglegym.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}
