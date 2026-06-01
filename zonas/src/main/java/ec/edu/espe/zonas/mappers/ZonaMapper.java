package ec.edu.espe.zonas.mappers;

import org.springframework.stereotype.Component;

import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.Zona;

@Component
public class ZonaMapper {

    public ZonaRespondeDto toResponse(Zona objZona) {
        return ZonaRespondeDto.builder()
            .idZona(objZona.getId())
            .nombre(objZona.getNombre())
            .codigo(objZona.getCodigo())
            .tipo(objZona.getTipo())
            .descripcion(objZona.getDescripcion())
            .espacios(objZona.getEspacios())
            .estado(objZona.getEstado())
            .fechaCreacion(objZona.getFechaCreacion())
            .fechaModificacion(objZona.getFechaModificacion())
            .build();
    }
}
