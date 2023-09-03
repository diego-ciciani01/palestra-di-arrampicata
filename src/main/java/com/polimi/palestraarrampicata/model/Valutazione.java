package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@Table(name = "valutazione")
@ToString
public class Valutazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "valore", nullable = false, length = 1)
    @Min(1)
    @Max(5)
    private Integer valore;

    @ManyToOne
    @JoinColumn(name = "valutatore")
    private Utente valutatore;

    @ManyToOne
    @JoinColumn(name = "valutato")
    private Utente valutato;

}
