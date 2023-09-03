package com.polimi.palestraarrampicata.security;

import com.polimi.palestraarrampicata.model.Ruolo;
import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

/**
 * Questa classe configura la sicurezza dell'applicazione web utilizzando Spring Security.
 * Definisce un filtro di autenticazione JWT personalizzato e un provider di autenticazione personalizzato.
 * Inoltre, definisce le regole di autorizzazione per le richieste HTTP in base al percorso della richiesta.
 * La classe utilizza anche un filtro personalizzato per la gestione delle eccezioni JWT.
 * Infine, il metodo `createAdmin` viene eseguito al momento dell'inizializzazione dell'applicazione e crea un utente amministratore predefinito se non esiste già nel database.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final UtenteRepo userRepository;
    private final PasswordEncoder passwordEncoder;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/profilo/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Il metodo viene chiamato quando si avvia l'appalicazione e controlla l'esistenza dell'utente admin, nel caso in cui
     * non sia presente lo crea
     */
    @PostConstruct
    public void createAdmin() {
        Optional<Utente> admin = userRepository.findUserByEmail("admin@palestra.com");
        if(admin.isEmpty()) {
            userRepository.save(new Utente(
                    "admin@palestra.com",
                            Ruolo.ADMIN,
                            passwordEncoder.encode("admin"),
                    true
                    )
            );
        }
    }
}