package com.hackathon.junglegym.domain.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // 항상 api뒤에 복수로 써야함!!
@Tag(name = "User", description = "User 관리 API")
public class UserController {}
