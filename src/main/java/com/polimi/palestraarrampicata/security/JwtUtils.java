package com.polimi.palestraarrampicata.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
/**
 * Questa classe fornisce metodi per generare e verificare JSON Web Token (JWT).
 * Utilizza la libreria JJWT per la creazione e la decodifica dei token.
 * La chiave segreta utilizzata per la firma dei token è specificata come costante all'interno della classe.
 * Inoltre, la classe fornisce metodi per estrarre informazioni dal token, come l'username dell'utente autenticato.
 */
@Component
public class JwtUtils {

    /**
     * Chiave per la decodifica da 256byte
     */
    private final String SECRET_KEY = "ubxRpXlzAHHvdRmUH/wInqgFBkIJn4Y6Al5jP2rNMEFPT+ghIOLU7rD7KXeEudZ/";
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    /**
     * Estrae l'email dal token, l'email è contenuta nel claims come soggetto
     * @param token
     * @return
     */
    public String exctractUsername(String token) {
        return extractClaim(token, Claims::getSubject) ; //Claim::getSubject mi da la mail che il sobject del claim
    }

    /**
     * Estrazione di un solo Claim
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera il token con la data di scadenza
     * @param userDetails
     * @return
     */
/*    public String generateToken(
            UserDetails userDetails,
            Date expireToken
    ) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expireToken)
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }*/

    public String generateToken(
            UserDetails userDetails,
            Map<String, Object> claims
    ){
    return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(SignatureAlgorithm.HS256, getSignKey())
            .compact();

    }


            /**
             * prende i claims inseriti nel campo extraClaims del jwt
             * @param token
             * @return
             */
    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Segna il token con una chiave e l'algoritmo di decodifica utilizzato
     * @return Key
     */
    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * controlla se il token appartiene all'utente considerato e che non sia ancora scaduto
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = exctractUsername(token);
        return(username.equals(userDetails.getUsername()) && !isTokenExpired(token) && userDetails.isEnabled());
    }

    /**
     *Vede se il token ha una data ancora valida
     */
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Estrae l'email dell'utente dal SecurityContext
     * @param servletRequest
     * @return
     */
    public String findEmailUtenteByHttpServletRequest(HttpServletRequest servletRequest) {
        //Prendo l'email dal token presente nella ServletRequest e da questo ricavo l'utente che sta effettuando la prenotazione
        String authHeader = servletRequest.getHeader("Authorization");
        //Nel header il token si trova nella posizione dopo la 7
        String jwt = authHeader.substring(7);
        return exctractUsername(jwt);
    }
}