package es.aytos.totalizados.model;

import lombok.Getter;

@Getter
public enum Organica {
    NIVEL_1(1), NIVEL_2(2), NIVEL_3(3), NIVEL4(3), NIVEL_5(3), EMPTY(0);

    private final int index;

    private Organica(int index) {
        this.index = index;
    }

}