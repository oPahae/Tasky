package com.example.demo.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private final String SECRET = "SECRET123456789";

    //creer token mnin kaydir user login et ce token kitsifet lfrontend 
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    //kat9ra token u katextracti email mnou
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
}
// hadi class li kat9ra u katcreer  JWT (JSON Web Token) li hiya wahed lmethod li katst3ml f authentication u authorization
// hadi  token li fih username dyal user u kat3tiha wahed lexpiration time dyal 24 hours
// hadi katst3ml f project bach n3tiw token luser mlli kaydir login u mn b3d n9raw hadak token bach n3rfou achno howa user li kaydir requests
