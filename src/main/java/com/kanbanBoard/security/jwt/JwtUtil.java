package com.kanbanBoard.security.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JwtUtil {

	private static final String SECRET = "someSecretKey";
	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

	private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

	public String generateToken(String username) {
		return JWT.create().withSubject(username).withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(algorithm);
	}

	public String extractUsername(String token) {
		DecodedJWT jwt = verifyToken(token);
		return jwt != null ? jwt.getSubject() : null;
	}

	public boolean validateToken(String token, String username) {
		try {
			DecodedJWT jwt = verifyToken(token);
			return jwt != null && jwt.getSubject().equals(username) && !isTokenExpired(jwt);
		} catch (JWTVerificationException e) {
			return false;
		}
	}

	private DecodedJWT verifyToken(String token) {
		try {
			JWTVerifier verifier = JWT.require(algorithm).build();
			return verifier.verify(token);
		} catch (JWTVerificationException e) {
			return null;
		}
	}

	private boolean isTokenExpired(DecodedJWT jwt) {
		return jwt.getExpiresAt().before(new Date());
	}
}
