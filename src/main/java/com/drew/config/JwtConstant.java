package com.drew.config;

import org.springframework.beans.factory.annotation.Value;

public class JwtConstant {
    @Value("${jwt.secret}")
    public static final String JWT_SECRET = "wadwahdbaw12lnf31!?wwaas3iufhebqwdlin!";

     public static final String JWT_HEADER = "Authorization";
}
