package es.aytos.totalizados.service.impl;

import es.aytos.totalizados.mapper.AplicacionesMapper;
import es.aytos.totalizados.model.*;
import es.aytos.totalizados.service.TotalizadosService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class TotalizadosServiceImpl implements TotalizadosService {

    private final AplicacionesMapper aplicacionesMapper;

    public TotalizadosServiceImpl(AplicacionesMapper aplicacionesMapper) {
        this.aplicacionesMapper = aplicacionesMapper;
    }

    @Override
    public List<Map.Entry<Aplicacion, BigDecimal>> filtrarAplicacion(Organica organica, Programa programa, Economica economica, CentroGestor centroGestor) {
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();

        List<Aplicacion> aplicacionList = new ArrayList<>(this.aplicacionesMapper.getAplicaciones());

        if (!Organica.EMPTY.equals(organica) && Programa.EMPTY.equals(programa) && Economica.EMPTY.equals(economica)
                && CentroGestor.EMPTY.equals(centroGestor)) { // Organica Only
            mapList = generateMapListByOrganica(aplicacionList, organica);
        } else if (!Organica.EMPTY.equals(organica) && !Programa.EMPTY.equals(programa) && Economica.EMPTY.equals(economica)
                && CentroGestor.EMPTY.equals(centroGestor)) { // Organica & Programa
            mapList = generateMapListByOrganicaAndPrograma(aplicacionList, organica, programa);
        } else if (!Organica.EMPTY.equals(organica) && !Programa.EMPTY.equals(programa) && !Economica.EMPTY.equals(economica)
                && CentroGestor.EMPTY.equals(centroGestor)) { // Org & Prog & Econ
            mapList = generateMapListByOrganicaAndProgramaAndEconomica(aplicacionList, organica, programa, economica);
        } else if (Organica.EMPTY.equals(organica) && !Programa.EMPTY.equals(programa) && Economica.EMPTY.equals(economica)
                && CentroGestor.EMPTY.equals(centroGestor)) { // Programa Only
            mapList = generateMapListByPrograma(aplicacionList, programa);
        } else if (Organica.EMPTY.equals(organica) && !Programa.EMPTY.equals(programa) && Economica.EMPTY.equals(economica)
                && !CentroGestor.EMPTY.equals(centroGestor)) { // CentroGestor & Programa
            mapList = generateMapListByCentroGestorAndPrograma(centroGestor, programa);
        }
        return mapList;
    }

    private List<Map.Entry<Aplicacion, BigDecimal>> generateMapListByOrganica(List<Aplicacion> aplicacionList, Organica organica) {
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();
        Map<Object, List<Aplicacion>> organicaMap = aplicacionList.stream().collect(Collectors.groupingBy(
                a -> {
                    return a.getOrganica().substring(0, organica.getIndex());
                },
                TreeMap::new,
                Collectors.toList()
        ));
        int i = 1;
        for (Map.Entry<Object, List<Aplicacion>> entry : organicaMap.entrySet()) {
            BigDecimal total = new BigDecimal(0);
            Aplicacion aplicacion = new Aplicacion();
            aplicacion.setOrganica((String) entry.getKey());
            for (Aplicacion a : entry.getValue()) {
                total = total.add(BigDecimal.valueOf(a.getObligacion()));
            }
            aplicacion.setDescripcion("Descrip " + (i++));
            mapList.add(new AplicacionEntry<Aplicacion, BigDecimal>(aplicacion, total));
        }
        return mapList;
    }

    private List<Map.Entry<Aplicacion, BigDecimal>> generateMapListByOrganicaAndPrograma(List<Aplicacion> aplicacionList
            , Organica organica, Programa programa) {
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();

        Map<Object, Map<Object, List<Aplicacion>>> programaMap = aplicacionList.stream().collect(Collectors.groupingBy(
                a -> {
                    return a.getOrganica().substring(0, organica.getIndex());
                }, TreeMap::new,
                Collectors.groupingBy(a -> {
                    return a.getPrograma().substring(0, programa.getIndex());
                }, TreeMap::new, Collectors.toList())
        ));
        programaMap.forEach((ok, ov) -> {
            Aplicacion app = new Aplicacion();
            app.setOrganica("Total " + ok);
            BigDecimal totalizado = new BigDecimal(0);
            Map.Entry<Aplicacion, BigDecimal> myMap = new AplicacionEntry<Aplicacion, BigDecimal>(app, totalizado);
            ov.forEach((pk, pv) -> {
                BigDecimal total = new BigDecimal(0);
                Aplicacion aplicacion = new Aplicacion();
                aplicacion.setOrganica((String) ok);
                aplicacion.setPrograma((String) pk);
                for (Aplicacion a : pv) {
                    total = total.add(BigDecimal.valueOf(a.getObligacion()));
                }
                aplicacion.setDescripcion("Descripción de " + programa.toString());
                mapList.add(new AplicacionEntry<Aplicacion, BigDecimal>(aplicacion, total));
            });
            mapList.add(myMap);
        });

        return mapList;
    }

    private List<Map.Entry<Aplicacion, BigDecimal>> generateMapListByOrganicaAndProgramaAndEconomica(
            List<Aplicacion> aplicacionList, Organica organica, Programa programa, Economica economica) {
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();
        AtomicReference<BigDecimal> totalGeneral = new AtomicReference<>(new BigDecimal(0));
        Aplicacion appTotalGeneral = new Aplicacion();
        appTotalGeneral.setOrganica("TOTAL");

        Map<Object, Map<Object, Map<Object, List<Aplicacion>>>> economicaMap =
                aplicacionList.stream().collect(Collectors.groupingBy(
                        a -> {
                            return a.getOrganica().substring(0, organica.getIndex());
                        }, TreeMap::new,
                        Collectors.groupingBy(a -> {
                                    return a.getPrograma().substring(0, programa.getIndex());
                                }, TreeMap::new,
                                Collectors.groupingBy(a -> {
                                    return a.getEconomica().substring(0, economica.getIndex());
                                }, TreeMap::new, Collectors.toList()))
                ));
        economicaMap.forEach((ok, ov) -> {
            Aplicacion appTotalOrganica = new Aplicacion();
            appTotalOrganica.setOrganica("Total " + ok);
            appTotalOrganica.setDescripcion("Descripción de la orgánica");
            AtomicReference<BigDecimal> totalOrganica = new AtomicReference<>(new BigDecimal(0));
            ov.forEach((pk, pv) -> {
                Aplicacion appTotalPrograma = new Aplicacion();
                appTotalPrograma.setOrganica("Total " + ok);
                appTotalPrograma.setDescripcion("Descripción de " + programa.toString());
                AtomicReference<BigDecimal> totalPrograma = new AtomicReference<>(new BigDecimal(0));
                pv.forEach((ek, ev) -> {
                    BigDecimal total = new BigDecimal(0);
                    Aplicacion aplicacion = new Aplicacion();
                    aplicacion.setOrganica((String) ok);
                    aplicacion.setPrograma((String) pk);
                    aplicacion.setEconomica((String) ek);
                    aplicacion.setDescripcion("Descripción de " + economica.toString());
                    for (Aplicacion a : ev) {
                        total = total.add(BigDecimal.valueOf(a.getObligacion()));
                    }
                    totalPrograma.accumulateAndGet(total, BigDecimal::add);
                    mapList.add(new AplicacionEntry<Aplicacion, BigDecimal>(aplicacion, total));
                });
                Map.Entry<Aplicacion, BigDecimal> pMap = new AplicacionEntry<Aplicacion, BigDecimal>(appTotalPrograma, totalPrograma.get());
                totalOrganica.accumulateAndGet(totalPrograma.get(), BigDecimal::add);
                mapList.add(pMap);
            });
            Map.Entry<Aplicacion, BigDecimal> oMap = new AplicacionEntry<Aplicacion, BigDecimal>(appTotalOrganica, totalOrganica.get());
            mapList.add(oMap);
            totalGeneral.accumulateAndGet(totalOrganica.get(), BigDecimal::add);
        });
        Map.Entry<Aplicacion, BigDecimal> totalMap = new AplicacionEntry<Aplicacion, BigDecimal>(appTotalGeneral, totalGeneral.get());
        mapList.add(totalMap);
        return mapList;
    }

    private List<Map.Entry<Aplicacion, BigDecimal>> generateMapListByPrograma(List<Aplicacion> aplicacionList, Programa programa) {
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();
        Map<Object, List<Aplicacion>> programaMap = aplicacionList.stream().collect(Collectors.groupingBy(
                a -> {
                    return a.getPrograma().substring(0, programa.getIndex());
                }
        ));
        for (Map.Entry<Object, List<Aplicacion>> entry : programaMap.entrySet()) {
            BigDecimal total = new BigDecimal(0);
            Aplicacion aplicacion = new Aplicacion();
            aplicacion.setPrograma((String) entry.getKey());
            for (Aplicacion a : entry.getValue()) {
                total = total.add(BigDecimal.valueOf(a.getObligacion()));
            }
            aplicacion.setDescripcion(programa.toString());
            mapList.add(new AplicacionEntry<Aplicacion, BigDecimal>(aplicacion, total));
        }
        return mapList;
    }

    private List<Map.Entry<Aplicacion, BigDecimal>> generateMapListByCentroGestorAndPrograma(CentroGestor centroGestor, Programa programa) {
        List<Aplicacion> aplicaciones = this.aplicacionesMapper.getAplicacionesGroupByCentroGestorAndPrograma();
        List<Map.Entry<Aplicacion, BigDecimal>> mapList = new ArrayList<>();
        AtomicReference<BigDecimal> totalGeneral = new AtomicReference<>(new BigDecimal(0));
        aplicaciones.forEach(a -> {
            a.setOrganica("");
            BigDecimal total = new BigDecimal(0);
            total = total.add(BigDecimal.valueOf(a.getObligacion()));
            mapList.add(new AplicacionEntry<>(a, total));
            totalGeneral.accumulateAndGet(total, BigDecimal::add);
        });
        Aplicacion aplicacion = new Aplicacion();
        aplicacion.setOrganica("");
        aplicacion.setCentroGestor("TOTAL");
        mapList.add(new AplicacionEntry<>(aplicacion, totalGeneral.get()));
        return mapList;
    }

}
