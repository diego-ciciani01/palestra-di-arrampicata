package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="corso")
public class Corso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name="nome_corso")
    private String nome;

    @Column(name = "numero_settimane")
    private Integer settimaneDiCorso;

    @Enumerated(EnumType.ORDINAL)
    private Difficolta difficolta;

    @Column(name="costo")
    private Float costo;

    @Column(name="data_inizio")
    private LocalDate dataInizio;

    @ManyToOne()
    @JoinColumn(name="istruttoreCorso")
    private Utente istruttoreCorso;

    @ManyToMany(mappedBy = "corsiIscritto", fetch = FetchType.LAZY)
    private List<Utente> iscritti;

    @ManyToOne()
    @JoinColumn(name="corsoPalestra")
    private Palestra corsoPalestra;
}
