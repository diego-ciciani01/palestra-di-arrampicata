package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "escursione")
public class Escursione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "data_pubblicazione")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date data;

    @Column(name = "posti-disponibili")
    private Integer postiDisponibili;

    @Column(name = "nome-escurione")
    private  String nomeEscursione;

    @Column(name = "descrizione")
    private  String descrizione;

    @ManyToOne
    @JoinColumn(name = "organizzatore")
    private Utente organizzatore;

    @Column(name = "stato", nullable = false)
    private Boolean statoEscursione;

    @ManyToMany(mappedBy = "escursioniPartecipate", fetch = FetchType.LAZY)
    private List<Utente> utentiPartecipanti = null;

}
