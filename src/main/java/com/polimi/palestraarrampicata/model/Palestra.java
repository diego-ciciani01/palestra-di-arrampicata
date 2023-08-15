package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "palestra")
public class Palestra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "cap")
    private String cap;

    @Column(name = "citta")
    private String citta;

    @Column(name = "telefono")
    private String telefono;

    @OneToMany(mappedBy = "iscrittiPalestra", fetch = FetchType.LAZY  )
    private List<Utente> iscrittiPalestra = null;

    @OneToMany(mappedBy = "attrezziPalestra", fetch = FetchType.LAZY )
    private List<Attrezzatura> attrezatura = null;


    public Palestra(String cap, String citta, String telefono, String indirizzo, String nomePalestra) {
        this.cap = cap;
        this.citta = citta;
        this.telefono=telefono;
        this.indirizzo = indirizzo;
        this.nome = nomePalestra;
    }
}
