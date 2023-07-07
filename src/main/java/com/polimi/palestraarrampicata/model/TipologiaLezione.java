package com.polimi.palestraarrampicata.model;

public enum TipologiaLezione {
    Tecnica ("Tecnica"),
    Resistenza("Resistenza"),
    Uscita("Usciata in falesia"),
    Introduzione("Lezione di introduzione");

    private final String nome;

    TipologiaLezione(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static TipologiaLezione fromString(String nome) {
        for (TipologiaLezione t : TipologiaLezione.values()) {
            if (t.nome.equalsIgnoreCase(nome)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo di lezione non disponibile");
    }

    @Override
    public String toString() {
        return this.nome;
    }

}
