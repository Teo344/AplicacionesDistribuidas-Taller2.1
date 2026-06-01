package ec.edu.espe.zonas.services;

import java.util.List;
import java.util.UUID;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaRespondeDto;

public interface ZonaServicio {

    List<ZonaRespondeDto> listarZonas();

    ZonaRespondeDto crearZona(ZonaRequestDto request);

    ZonaRespondeDto actualizarZona(UUID idZona, ZonaRequestDto request);

    void activarZona(UUID idZona);

}
