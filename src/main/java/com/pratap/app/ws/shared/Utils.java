package com.pratap.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import com.pratap.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public interface Utils {

	final Random RANDOM = new SecureRandom();
	final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String generateUserId(int length) {
		return generateRandomString(length);
	}

	public static String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private static String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}
	
	public static boolean hasTokenExpired(String token) {
		boolean returnValue = false;

		try {
			Claims claims = Jwts.parser()
								.setSigningKey(SecurityConstants.getTokenSecret())
								.parseClaimsJws(token)
								.getBody();

			Date tokenExpirationDate = claims.getExpiration();
			Date todayDate = new Date();

			returnValue = tokenExpirationDate.before(todayDate);
		} catch (ExpiredJwtException ex) {
			returnValue = true;
		}

		return returnValue;
	}
	
	public static String generatedEmailVerificationToken(String userId) {
		
		String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
		
	}
	
	public static String generatePasswordResetToken(String userId)
    {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }
}
