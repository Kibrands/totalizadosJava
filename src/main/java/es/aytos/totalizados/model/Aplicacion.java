package es.aytos.totalizados.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aplicacion {
    private String organica;
    private String programa;
    private String economica;
    private String descripcion;
    private Double obligacion;
    private String centroGestor;

    public boolean isTotal() {
        if (!getOrganica().isEmpty()) {
            if (getOrganica().length() < 5) return false;
            return getOrganica().substring(0, 5).equalsIgnoreCase("TOTAL");
        } else if (!getCentroGestor().isEmpty()) {
            if (getCentroGestor().length() > 5) return false;
            return getCentroGestor().substring(0, 5).equalsIgnoreCase("TOTAL");
        }
        return false;
    }
}
