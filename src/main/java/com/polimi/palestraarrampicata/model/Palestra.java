package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
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

    @OneToMany(mappedBy = "iscrittiPalestra", fetch = FetchType.LAZY  )
    private List<Utente> iscrittiPalestra = null;

    @OneToMany(mappedBy = "attrezziPalestra", fetch = FetchType.LAZY )
    private List<Attrezzatura> attrezatura = null;


}
