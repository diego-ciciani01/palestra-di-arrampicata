package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "attrezzatura")
public class Attrezzatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "quantita")
    private Integer quantita;

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

    @Column(name = "taglia")
    private String taglia;

    @ManyToOne
    @JoinColumn(name="noleggiatore")
    private Utente noleggiatore;

    @ManyToOne()
    @JoinColumn(name ="attrezzi_palestra")
    private Palestra attrezziPalestra;
}
