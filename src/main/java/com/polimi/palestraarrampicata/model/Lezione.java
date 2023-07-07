package com.polimi.palestraarrampicata.model;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "lezione")
public class Lezione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "data_lezione")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date data;

    @Column(name = "durata_lezione")
    private Float durata;

    @Column(name = "stato_lezione", nullable = false)
    private Boolean statoLazione;

    @Enumerated(EnumType.ORDINAL)
    private TipologiaLezione tipologiaLezione;

    @ManyToMany(mappedBy = "inviti", fetch = FetchType.LAZY)
    private List<Utente> utentiPartecipanti = null;

    @ManyToMany(mappedBy = "inviti", fetch = FetchType.LAZY)
    private List<Utente>  utentiInvitati = null;

    @ManyToOne
    @JoinColumn(name = "iscritto")
    private Utente iscritto;

}
