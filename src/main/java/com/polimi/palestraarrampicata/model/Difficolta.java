package com.polimi.palestraarrampicata.model;

public enum Difficolta {
    FACILE("Facile"),
    MEDIA("Media"),
    DIFFICILE("Difficile"),
    MOLTO_DIFFICILE("Molto difficile");

    private final String nome;
    Difficolta(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static Difficolta fromString(String nome) {
        for (Difficolta d : Difficolta.values()) {
            if (d.nome.equalsIgnoreCase(nome)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Difficoltà inserita non valida");
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
