package com.tccseed.tcc_seed.controller.auth.dto;

public record CsrfTokenResponse(String token, String headerName) {
}
