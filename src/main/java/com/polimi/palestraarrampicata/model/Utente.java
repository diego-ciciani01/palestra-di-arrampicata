package com.polimi.palestraarrampicata.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="utente")
public class Utente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "foto_profilo")
    @Lob
    private byte[] fotoProfilo;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "data_nascita")
    private LocalDate dataDiNascita;

    @Enumerated(EnumType.ORDINAL)
    private Ruolo ruolo;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "commentatore")
    private List<Commento> commenti;

    @OneToMany(mappedBy = "valutatore")
    private List<Valutazione> valutazioniInserite;

    @OneToMany(mappedBy = "valutato")
    private List<Valutazione> valutazioniRicevute;

    @OneToMany(mappedBy = "organizzatore")
    private List<Escursione> escursioniOrganizzate;

    @ManyToMany
    @JoinTable(name = "partecipazione", joinColumns =
    @JoinColumn( name = "utente") , inverseJoinColumns =
    @JoinColumn(name = "escursione"))
    private List<Escursione> escursioniPartecipate;

    @Column
    private boolean accountExpired;

    @JsonBackReference
    @ManyToMany
    @JoinTable(name = "invito", joinColumns =
    @JoinColumn( name = "utente") , inverseJoinColumns =
    @JoinColumn(name = "lezione"))
    private List<Lezione> inviti;

    @OneToMany(mappedBy = "istruttore")
    private List<Lezione> lezioniIscritte;

    @OneToMany(mappedBy = "noleggiatore")
    private List<Attrezzatura> attrezzatureNoleggiate ;

    @ManyToOne()
    @JoinColumn(name = "iscritti_palestra")
    private Palestra iscrittiPalestra;

    @OneToMany(mappedBy = "istruttoreCorso")
    private List<Corso> corsiTenuti;

    @Column
    private boolean enable;

    @Column
    private boolean locked;

    @ManyToMany
    @JoinTable(name="iscrizione", joinColumns =
    @JoinColumn(name = "utente"), inverseJoinColumns =
    @JoinColumn(name ="corso"))
    private List<Corso> corsiIscritto;

    @OneToMany(mappedBy = "istruttoreCommentato")
    private List<Commento> commentiIstruttore;

    public Utente(String email, Ruolo ruolo, String password, boolean enable) {
        this.email = email;
        this.ruolo = ruolo;
        this.password = password;
        this.enable = enable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(ruolo.name()));
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
