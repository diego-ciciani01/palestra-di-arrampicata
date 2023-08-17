package com.polimi.palestraarrampicata.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;

import java.util.List;

@Data
@Entity
@Table(name="taglia")
public class Taglia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name ="taglia_attrezzo")
    private String taglia;

    @ManyToOne
    @JoinColumn(name = "attrezzo")
    private Attrezzatura attrezzo;
    /*
    @OneToMany(mappedBy = "attrezzi")
    private List<Attrezzatura> attrezzi;
    */

    public  Taglia(String taglia, Attrezzatura attrezzo){
        this.taglia = taglia;
        this.attrezzo = attrezzo;
    }

}
