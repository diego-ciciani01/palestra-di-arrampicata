package com.polimi.palestraarrampicata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "attrezzatura")
public class Attrezzatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "data_noleggio")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataNoleggio;

    @Column(name = "data_fine_noleggio")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataFineNoleggio;

    @Column(name = "disponibilita", nullable = false)
    private Boolean disponibilita;

    @Column(name= "nome")
    private String nomeAttrezzatura;


    @OneToMany(mappedBy = "attrezzo")
    private List<Taglia> nomeTaglia;

/*
    @ManyToOne
    @JoinColumn(name = "attrezzo")
    private Taglia taglia;
*/
    @ManyToOne
    @JoinColumn(name="noleggiatore")
    private Utente noleggiatore;

    @ManyToOne()
    @JoinColumn(name ="attrezzi_palestra")
    private Palestra attrezziPalestra;
}
