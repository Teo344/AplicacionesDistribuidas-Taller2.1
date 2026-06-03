package ec.edu.espe.zonas.utils;

import org.springframework.stereotype.Component;

import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.Zona;

@Component
public class ZonaMapper {

    private final EspacioMapper espacioMapper;

    public ZonaMapper(EspacioMapper espacioMapper) {
        this.espacioMapper = espacioMapper;
    }

    public ZonaRespondeDto toResponse(Zona objZona) {
        return ZonaRespondeDto.builder()
            .idZona(objZona.getId())
            .nombre(objZona.getNombre())
            .codigo(objZona.getCodigo())
            .tipo(objZona.getTipo())
            .descripcion(objZona.getDescripcion())
            .espacios(objZona.getEspacios().stream()
                .map(espacioMapper::toResponseDto)
                .toList())
            .estado(objZona.getEstado())
            .capacidad(objZona.getCapacidad())
            .fechaCreacion(objZona.getFechaCreacion())
            .fechaModificacion(objZona.getFechaModificacion())
            .build();
    }
}
