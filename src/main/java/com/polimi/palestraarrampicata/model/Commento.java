package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "commento")
public class Commento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private  Integer id;

    @Column(name = "data_pubblicazione")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataInserimento;

    @Column(name = "testo")
    private  String testo;

    @ManyToOne
    private Commento commentoPadre;

    @OneToMany(mappedBy = "commentoPadre", fetch = FetchType.LAZY)
    private List<Commento> commentiFigli = null;

    @ManyToOne
    @JoinColumn(name = "istruttoreCommentato")
    private Utente istruttoreCommentato;

    @ManyToOne
    @JoinColumn(name = "commentatore")
    private Utente commentatore;

    @Override
    public String toString() {
        return "Commento{" +
                "id=" + id +
                ", dataInserimento=" + dataInserimento +
                ", testo='" + testo + '\'' +
                ", commentatore=" + commentatore +
                ", commentoPadre=" + commentoPadre +
                ", commentiFigli=" + commentiFigli +
                '}';
    }
}
 