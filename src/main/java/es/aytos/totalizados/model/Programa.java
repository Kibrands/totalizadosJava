package es.aytos.totalizados.model;

import lombok.Getter;

@Getter
public enum Programa {
    AREA_DE_GASTO(1), POLITICA(2), GRUPO_PROG(3), PROGRAMA(4), SUBPROGRAMA(5), EMPTY(0);

    private final int index;

    private Programa(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        switch (this) {
            case EMPTY:
                return "";
            case POLITICA:
                return "Política";
            case PROGRAMA:
                return "Programa";
            case AREA_DE_GASTO:
                return "Área de Gasto";
            case GRUPO_PROG:
                return "Grupo Prog";
            case SUBPROGRAMA:
                return "Subprograma";
            default:
                throw new IllegalArgumentException();
        }
    }
}