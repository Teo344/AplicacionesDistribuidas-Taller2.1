package ec.edu.espe.zonas.services;

import java.util.List;
import java.util.UUID;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspacioRespondeDto;
import ec.edu.espe.zonas.entidades.EspacioEstado;

public interface EspacioServicio {

    List<EspacioRespondeDto> obtenerEspacio();

    EspacioRespondeDto crearEspacio(EspacioRequestDto dto);
    
    EspacioRespondeDto actualizarEspacio(UUID idEspacio, EspacioRespondeDto dto);

    void eliminarEspacio(UUID idEspacio);

    EspacioRespondeDto cambiarEstado(UUID idEspacio, EspacioEstado estado);

    List<EspacioRespondeDto> obtenerEspacioPorEstado(EspacioEstado estado);

    List<EspacioRespondeDto> obtenerEspacioPorZona(UUID idZona, EspacioEstado estado);
}
