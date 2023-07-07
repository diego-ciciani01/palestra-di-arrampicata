package com.polimi.palestraarrampicata.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.*;
import lombok.Cleanup;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Table(name ="utente")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modiicare l'id
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "username")
    private String username;

    @Column(name = "foto_profilo")
    @Lob
    private byte[] fotoProfilo;

    @Column(name = "email")
    private String email;

    @Column(name = "data_nascita")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataDiNascita;

    @Enumerated(EnumType.ORDINAL)
    private Ruolo ruolo;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "commentatore", fetch = FetchType.LAZY)
    private List<Commento> commenti = null;

    @OneToMany(mappedBy = "valutatore", fetch = FetchType.LAZY)
    private List<Valutazione> valutazioniInserite = null;

    @OneToMany(mappedBy = "valutato", fetch = FetchType.LAZY)
    private List<Valutazione> valutazioniRicevute = null;

    @OneToMany(mappedBy = "organizzatore", fetch = FetchType.LAZY )
    private List<Escursione> escursioniOrganizzate = null;

    @ManyToMany
    @JoinTable(name = "partecipazione", joinColumns =
    @JoinColumn( name = "utente") , inverseJoinColumns =
    @JoinColumn(name = "escursione"))
    private List<Escursione> escursioniPartecipate = null;

    @ManyToMany
    @JoinTable(name = "invito", joinColumns =
    @JoinColumn( name = "utente") , inverseJoinColumns =
    @JoinColumn(name = "lezione"))
    private List<Lezione> inviti = null;

    @OneToMany(mappedBy = "iscritto", fetch = FetchType.LAZY)
    private List<Lezione> lezioniIscritte = null;

    @OneToMany(mappedBy = "noleggiatore", fetch = FetchType.LAZY)
    private List<Attrezzatura> attrezzatureNoleggiate = null;

    @ManyToOne()
    @JoinColumn(name = "iscritti_palestra")
    private Palestra iscrittiPalestra;

    @OneToMany(mappedBy = "istruttoreCorso", fetch = FetchType.LAZY)
    private List<Corso> corsiTenuti = null;

    @ManyToMany
    @JoinTable(name="iscrizione", joinColumns =
    @JoinColumn(name = "utente"), inverseJoinColumns =
    @JoinColumn(name ="corso"))
    private List<Corso> corsiIscritto = null;

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", fotoProfilo=" + Arrays.toString(fotoProfilo) +
                ", email='" + email + '\'' +
                ", dataDiNascita=" + dataDiNascita +
                ", ruolo=" + ruolo +
                ", password='" + password + '\'' +
                ", commenti=" + commenti +
                '}';
    }
}
