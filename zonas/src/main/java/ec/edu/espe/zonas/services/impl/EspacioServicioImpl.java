package ec.edu.espe.zonas.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspacioRespondeDto;
import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.entidades.Zona;
import ec.edu.espe.zonas.repositories.EspacioRepositorio;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.services.EspacioServicio;
import ec.edu.espe.zonas.utils.EspacioMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspacioServicioImpl implements EspacioServicio {

    private final EspacioRepositorio repositorio;
    private final ZonaRepositorio repositorioZona;
    private final EspacioMapper mapper;



    @Override
    @Transactional(readOnly = true)
    public List<EspacioRespondeDto> obtenerEspacio() {
        return repositorio.findAll().stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EspacioRespondeDto crearEspacio(EspacioRequestDto dto) {
        if (repositorio.existsByCodigo(dto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "YA EXISTE EL CODIGO DEL ESPACIO");
        }

        Zona objZona = repositorioZona.findById(dto.getIdZona())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Zona no encontrada con id: " + dto.getIdZona()));
        
        Espacio nuevEspacio = mapper.toEntity(dto);
        nuevEspacio.setZona(objZona);
        nuevEspacio.setActivo(true);

        if (nuevEspacio.getEstado() == null) {
            nuevEspacio.setEstado(EspacioEstado.DISPONIBLE);
        }

        Espacio espacioSaved = repositorio.save(nuevEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    public EspacioRespondeDto actualizarEspacio(UUID idEspacio, EspacioRespondeDto dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizarEspacio'");
    }

    @Override
    public void eliminarEspacio(UUID idEspacio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarEspacio'");
    }

    @Override
    @Transactional
    public EspacioRespondeDto cambiarEstado(UUID idEspacio, EspacioEstado estado) {
        Espacio objEspacio = repositorio.findById(idEspacio)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Espacio no encontrado con id: " + idEspacio));

        if (objEspacio.getEstado() == estado) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El espacio ya se encuentra en estado: " + estado);
        }

        objEspacio.setEstado(estado);

        Espacio espacioSaved = repositorio.save(objEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    public List<EspacioRespondeDto> obtenerEspacioPorEstado(EspacioEstado estado) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerEspacioPorEstado'");
    }

    @Override
    public List<EspacioRespondeDto> obtenerEspacioPorZona(UUID idZona, EspacioEstado estado) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerEspacioPorZona'");
    }
    
}
