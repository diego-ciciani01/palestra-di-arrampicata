package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="noleggio")
public class Noleggio {

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

    @ManyToOne
    @JoinColumn(name="attrezzo_noleggiato")
    private Attrezzatura attrezzoNoleggiato;

    @ManyToOne
    @JoinColumn(name="noleggiatore")
    private Utente noleggiatore;

    public Noleggio(LocalDateTime inizioNoleggio, LocalDateTime fineNoleggio, Utente utenteLoggato) {
        this.dataNoleggio = inizioNoleggio;
        this.dataFineNoleggio = fineNoleggio;
        this.noleggiatore = utenteLoggato;
    }
}
