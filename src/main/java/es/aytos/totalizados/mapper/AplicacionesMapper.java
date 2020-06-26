package es.aytos.totalizados.mapper;

import es.aytos.totalizados.model.Aplicacion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AplicacionesMapper {

    @Select("SELECT * FROM A_APL")
    List<Aplicacion> getAplicaciones();

    @Select("select cg.DESCRIPCION as CENTROGESTOR, PROGRAMA, apl.DESCRIPCION , OBLIGACION from dbo.A_APL APL, dbo.A_CG CG where " +
            "apl.CENTROGESTOR = cg.CODIGO group by cg.DESCRIPCION, PROGRAMA, apl.DESCRIPCION, OBLIGACION")
    List<Aplicacion> getAplicacionesGroupByCentroGestorAndPrograma();
}
