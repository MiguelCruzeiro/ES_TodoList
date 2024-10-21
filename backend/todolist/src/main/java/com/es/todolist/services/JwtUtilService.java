package com.es.todolist.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Service
public class JwtUtilService {

    @Value("${cognito.jwks.url}")
    private String jwksUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JwtUtilService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public Claims verifyToken(String token) throws Exception {
        String kid = extractKidFromToken(token);
        System.out.println("Kid: " + kid);
        RSAPublicKey publicKey = getPublicKeyFromJwks(kid);
        System.out.println("Public key: " + publicKey);
        return Jwts.parser().setSigningKey(publicKey).build().parseSignedClaims(token).getPayload();
    }

    public String extractKidFromToken(String token) throws Exception {
        // Split the token by '.' to get the header (first part)
        String[] parts = token.split("\\.");
        
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }
    
        // Decode the Base64 URL-encoded header
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
    
        // Parse the header JSON to extract the "kid"
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> headerMap = objectMapper.readValue(headerJson, Map.class);
        
        // Return the "kid" value
        return headerMap.get("kid").toString();
    }
    

    private RSAPublicKey getPublicKeyFromJwks(String kid) throws Exception {
        // Fetch the JWKS from the provided URL
        JsonNode jwks = restTemplate.getForObject(jwksUrl, JsonNode.class);
        for (JsonNode key : jwks.get("keys")) {
            if (key.get("kid").asText().equals(kid)) {
                String modulus = key.get("n").asText();
                String exponent = key.get("e").asText();
                return createPublicKey(modulus, exponent);
            }
        }
        

        throw new RuntimeException("Unable to find matching key in JWKS");
    }

    private RSAPublicKey createPublicKey(String modulus, String exponent) throws Exception {
        // Decode the Base64 URL-encoded values
        byte[] decodedModulus = Base64.getUrlDecoder().decode(modulus);
        byte[] decodedExponent = Base64.getUrlDecoder().decode(exponent);

        BigInteger modBigInt = new BigInteger(1, decodedModulus);
        BigInteger expBigInt = new BigInteger(1, decodedExponent);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modBigInt, expBigInt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    public boolean validateToken(String token) throws Exception {
        // Extract the username from the token
        String username = extractUsername(token);
        // Extract the expiration date from the token
        Date expiration = extractExpiration(token);
        // Check if the token has expired
        if (isTokenExpired(token)) {
            return false;
        }
        return true;
    }

    // Extract the username (subject) from the token
    public String extractUsername(String token) throws Exception {
        Claims claims = verifyToken(token);
        return claims.getSubject();
    }

    // Extract the role from the token (assuming "role" is a claim)
    public List<String> extractRoles(String token) throws Exception {
        System.out.println("Extracting roles");
        Claims claims = verifyToken(token);
    
        // Extract the list of roles from "cognito:groups"
        return claims.get("cognito:groups", List.class);
    }

    // Extract the token expiration date
    public Date extractExpiration(String token) throws Exception {
        Claims claims = verifyToken(token);
        return claims.getExpiration();
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) throws Exception {
        return extractExpiration(token).before(new Date());
    }
    
}
