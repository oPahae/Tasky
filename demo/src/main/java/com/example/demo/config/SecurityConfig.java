// package com.example.demo.config;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.example.demo.jwt.JwtFilter;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private JwtFilter jwtFilter;// bach naccediw lfilter dyal jwt

//     // hadi method li katconfiguriw biha security dyal application
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       
//         //crsf kankhedmo biha f session 
//         //on desactive csrf hit hna kanst3mlo jwt 
//         http.csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/auth/register", "/auth/login").permitAll()// hadi routes li autorisées bla ma ykoun token (register u login)
//                 .anyRequest().authenticated()
//             )
//             .sessionManagement(sess -> sess
//                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             );
        
//         //appel de jwtFilter 
//         http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


//         return http.build();
//     }
// }

// // hadi class li katconfiguriw biha security dyal application
// //les routes li autorisées bla ma ykoun token (register u login)