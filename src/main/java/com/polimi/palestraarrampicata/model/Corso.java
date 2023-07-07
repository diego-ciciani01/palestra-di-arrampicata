package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="corso")
public class Corso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name="testo")
    private String nome;

    @Column(name = "numero_settimane")
    private Integer settimaneDiCorso;

    @Enumerated(EnumType.ORDINAL)
    private Difficolta difficolta;

    @Column(name="costo")
    private Float costo;

    @ManyToOne()
    @JoinColumn(name="istruttoreCorso")
    private Utente istruttoreCorso;

    @ManyToMany(mappedBy = "corsiIscritto", fetch = FetchType.LAZY)
    private List<Utente> iscritti;

}
