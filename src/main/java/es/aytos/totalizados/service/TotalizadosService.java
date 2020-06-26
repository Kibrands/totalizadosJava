package es.aytos.totalizados.service;

import es.aytos.totalizados.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TotalizadosService {

    List<Map.Entry<Aplicacion, BigDecimal>> filtrarAplicacion(Organica organica, Programa programa, Economica economica, CentroGestor centroGestor);
}
