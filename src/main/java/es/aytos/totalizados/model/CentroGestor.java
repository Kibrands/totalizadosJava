package es.aytos.totalizados.model;

import lombok.Getter;

@Getter
public enum CentroGestor {
    CENTRO_GESTOR, EMPTY;

    @Override
    public String toString() {
        if (this == CentroGestor.EMPTY) {
            return "";
        }
        return super.toString();
    }
}
