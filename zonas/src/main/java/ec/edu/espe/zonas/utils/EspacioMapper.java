package ec.edu.espe.zonas.utils;

import org.springframework.stereotype.Component;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspacioRespondeDto;
import ec.edu.espe.zonas.entidades.Espacio;

@Component
public class EspacioMapper {

    public EspacioRespondeDto toResponseDto(Espacio objEspacio) {
        if (objEspacio == null) {
            return null;
        }

        return EspacioRespondeDto.builder()
            .id(objEspacio.getId())
            .codigo(objEspacio.getCodigo())
            .descripcion(objEspacio.getDescripcion())
            .tipo(objEspacio.getTipo())
            .estado(objEspacio.getEstado())
            .activo(objEspacio.isActivo())
            .idZona(objEspacio.getZona().getId())
            .nombreZona(objEspacio.getZona().getNombre())
            .fechaCreacion(objEspacio.getFechaCreacion())
            .fechaModificacion(objEspacio.getFechaModificacion())
            .build();
    }

    public Espacio toEntity(EspacioRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return Espacio.builder()
            .codigo(requestDto.getCodigo())
            .descripcion(requestDto.getDescripcion())
            .tipo(requestDto.getTipo())
            .estado(requestDto.getEstado())
            .build();
    }
}
