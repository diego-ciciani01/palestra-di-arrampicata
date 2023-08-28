package com.polimi.palestraarrampicata.model;

import com.polimi.palestraarrampicata.utils.Utils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "palestra")
public class Palestra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false) // updatable = false: non permette di modificare l'id
    private Integer id;

    @NotNull
    @NotEmpty
    @Column(name = "nome")
    private String nome;

    @NotNull
    @NotEmpty
    @Column(name = "indirizzo")
    private String indirizzo;

    @NotNull
    @NotEmpty
    @Column(name = "cap")
    @Size(min = 5, max = 5, message = Utils.ERROR_CAP)
    private String cap;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    @Column(name = "email")
    private String emailPalestra;

    @NotNull
    @NotEmpty
    @Column(name = "citta")
    private String citta;

    @NotNull
    @NotEmpty
    @Column(name = "telefono")
    private String telefono;

    @OneToMany(mappedBy = "iscrittiPalestra", fetch = FetchType.LAZY  )
    private List<Utente> iscrittiPalestra;

    @OneToMany(mappedBy = "attrezziPalestra", fetch = FetchType.LAZY )
    private List<Attrezzatura> attrezatura;


    public Palestra(String cap, String citta, String telefono, String indirizzo, String nomePalestra, String emailPalestra) {
        this.cap = cap;
        this.citta = citta;
        this.telefono=telefono;
        this.indirizzo = indirizzo;
        this.nome = nomePalestra;
        this.emailPalestra = emailPalestra;
    }
}
