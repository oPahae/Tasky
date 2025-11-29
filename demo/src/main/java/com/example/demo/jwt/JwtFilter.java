// package com.example.demo.jwt;
// import java.io.IOException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.example.demo.service.CustomUserDetailsService;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class JwtFilter extends OncePerRequestFilter { 

//     @Autowired
//     private JwtUtil jwtUtil; // bach naccediw lmethods dyal jwtUtil

//     @Autowired
//     private CustomUserDetailsService userDetailsService;// bach naccediw ldetails dyal user

//     // hadi method li katintercepti kol request u katchecki wach fih token
//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain)
//             throws ServletException, IOException {

//         String header = request.getHeader("Authorization");// kanjibo lheader dyal Authorization mn request

//         //ila kan token kan9rawou u kanextractiw username mnou
//         //sinon kankheliwh iduz
//         if (header != null && header.startsWith("Bearer ")) {
//             String token = header.substring(7);// kanextractiw token mn header
//             String username = jwtUtil.extractUsername(token);// kanextractiw username mn token
            
//             //kanjibo details dyal user mn database
//             UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//             UsernamePasswordAuthenticationToken auth =
//                     new UsernamePasswordAuthenticationToken(
//                             userDetails, null, userDetails.getAuthorities());

//             SecurityContextHolder.getContext().setAuthentication(auth);
//         }

//         //nkheliw passer request vers controller
//         filterChain.doFilter(request, response);
//     }
// }

// //cette classe JwtFilter est un filtre de sécurité qui intercepte chaque requête HTTP entrante. 
// //verifie si le client a envoyé un token JWT 
// //si oui, elle extrait le nom d'utilisateur du token, charge les détails de l'utilisateur correspondant,