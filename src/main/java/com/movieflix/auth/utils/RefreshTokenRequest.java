package com.movieflix.auth.utils;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Data
@Getter
@Builder
public class RefreshTokenRequest {

	private String refreshToken;

}
