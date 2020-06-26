package es.aytos.totalizados.web;

import es.aytos.totalizados.model.*;
import es.aytos.totalizados.service.TotalizadosService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@Data
public class IndexBean implements Serializable {

    private String[] selectedCentroGestor;
    private Byte centroGestorPosition;

    private String[] selectedOrganicas;
    private Byte organicaPosition;

    private String[] selectedProgramas;
    private Byte programaPosition;

    private String[] selectedEconomicas;
    private Byte economicaPosition;

    private Boolean isOrganicaEmpty = true;
    private Boolean isProgramaEmpty = true;
    private Boolean isEconomicaEmpty = true;
    private Boolean isCentroGestorEmpty = true;

    private List<Map.Entry<Aplicacion, BigDecimal>> aplicacionesFiltradas;

    @ManagedProperty("#{totalizadosServiceImpl}")
    private TotalizadosService totalizadosService;

    @PostConstruct
    public void init() {
        centroGestorPosition = 1;
        organicaPosition = 2;
        programaPosition = 3;
        economicaPosition = 4;
    }

    public void filtrar() {
        aplicacionesFiltradas = new ArrayList<Map.Entry<Aplicacion, BigDecimal>>();

        Organica organica = generateOrganica();
        Programa programa = generatePrograma();
        Economica economica = generateEconomica();
        CentroGestor centroGestor = generateCentroGestor();

        aplicacionesFiltradas = this.totalizadosService.filtrarAplicacion(organica, programa, economica, centroGestor);
    }

    private Organica generateOrganica() {
        if (selectedOrganicas.length != 0) {
            this.isOrganicaEmpty = false;
            return Organica.valueOf(selectedOrganicas[selectedOrganicas.length - 1].toUpperCase().replace(" ", "_"));
        } else {
            this.isOrganicaEmpty = true;
            return Organica.EMPTY;
        }
    }

    private Programa generatePrograma() {
        if (selectedProgramas.length != 0) {
            this.isProgramaEmpty = false;
            return Programa.valueOf(StringUtils.stripAccents(selectedProgramas[selectedProgramas.length - 1]
                    .toUpperCase().replace(" ", "_")));
        } else {
            this.isProgramaEmpty = true;
            return Programa.EMPTY;
        }
    }

    private Economica generateEconomica() {
        if (selectedEconomicas.length != 0) {
            this.isEconomicaEmpty = false;
            return Economica.valueOf(StringUtils.stripAccents(selectedEconomicas[selectedEconomicas.length - 1].toUpperCase()));
        } else {
            this.isEconomicaEmpty = true;
            return Economica.EMPTY;
        }
    }

    private CentroGestor generateCentroGestor() {
        if (selectedCentroGestor.length != 0) {
            this.isCentroGestorEmpty = false;
            return CentroGestor.CENTRO_GESTOR;
        } else {
            this.isCentroGestorEmpty = true;
            return CentroGestor.EMPTY;
        }
    }
}