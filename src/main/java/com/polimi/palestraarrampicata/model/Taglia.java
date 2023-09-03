package com.polimi.palestraarrampicata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="taglia")
public class Taglia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @Column(name ="tagliaAttrezzo")
    private String tagliaAttrezzo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "attrezzo")
    private Attrezzatura attrezzo;
    /*
    @OneToMany(mappedBy = "attrezzi")
    private List<Attrezzatura> attrezzi;
    */

    @Column(name = "quantita")
    private Integer quantita;


    public  Taglia(String taglia, Attrezzatura attrezzo, Integer quantita){
        this.tagliaAttrezzo = taglia;
        this.attrezzo = attrezzo;
        this.quantita = quantita;
    }

}
