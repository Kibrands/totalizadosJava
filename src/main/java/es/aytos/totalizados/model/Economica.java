package es.aytos.totalizados.model;

import lombok.Getter;

@Getter
public enum Economica {
    CAPITULO(1), ARTICULO(2), CONCEPTO(3), PROGRAMA(4), PARTIDA(5), EMPTY(0);

    private final int index;

    private Economica(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        switch (this) {
            case EMPTY:
                return "";
            case CAPITULO:
                return "Capítulo";
            case PROGRAMA:
                return "Programa";
            case ARTICULO:
                return "Artículo";
            case CONCEPTO:
                return "Concepto";
            case PARTIDA:
                return "Partida";
            default:
                throw new IllegalArgumentException();
        }
    }
}
