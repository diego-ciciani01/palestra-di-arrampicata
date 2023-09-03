package com.polimi.palestraarrampicata.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "lezione")
public class Lezione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modiicare l'id
    private Integer id;

    @Column(name = "data_lezione")
    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime data;

    @Column(name = "durata_lezione")
    private Float durata;

    @Column(name = "stato_lezione", nullable = false)
    private Boolean statoLezione;

    @Enumerated(EnumType.ORDINAL)
    private TipologiaLezione tipologiaLezione;
    /*
    @ManyToMany(mappedBy = "inviti", fetch = FetchType.LAZY)
    private List<Utente> utentiPartecipanti;
    */
    @ManyToMany(mappedBy = "inviti", fetch = FetchType.LAZY)
    private List<Utente>  utenteMittente;

    @ManyToOne
    @JoinColumn(name = "istruttore")
    private Utente istruttore;

    @Column(name = "commento_istruttore")
    private String commento;

    public Lezione(LocalDateTime StartLesson, Float durata, TipologiaLezione tipologiaLezione){
        this.data = StartLesson;
        this.durata = durata;
        this.tipologiaLezione = tipologiaLezione;
    }

}
