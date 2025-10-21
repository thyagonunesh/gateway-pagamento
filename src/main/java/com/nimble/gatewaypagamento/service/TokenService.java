package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiracao}")
    private long expiracao;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getCpf())
                .claim("email", usuario.getEmail())
                .claim("papel", usuario.getFuncao().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(SignatureAlgorithm.HS256, chaveSecreta)
                .compact();
    }

}