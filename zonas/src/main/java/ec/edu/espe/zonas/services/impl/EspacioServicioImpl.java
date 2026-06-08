package ec.edu.espe.zonas.services.impl;

import java.time.LocalDateTime;
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
        return repositorio.findByEliminadoFalse().stream()
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

        if (objZona.getEstado() == 0) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede crear un espacio en una zona inactiva");
        }

        long espaciosRegistrados = repositorio.countByZonaIdAndEliminadoFalse(dto.getIdZona());
        if (espaciosRegistrados >= objZona.getCapacidad()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La zona ya alcanzo su capacidad maxima de espacios");
        }
        
        Espacio nuevEspacio = mapper.toEntity(dto);
        nuevEspacio.setZona(objZona);
        nuevEspacio.setActivo(true);
        nuevEspacio.setEliminado(false);
        nuevEspacio.setFechaCreacion(LocalDateTime.now());

        if (nuevEspacio.getEstado() == null) {
            nuevEspacio.setEstado(EspacioEstado.DISPONIBLE);
        }

        Espacio espacioSaved = repositorio.save(nuevEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    @Transactional
    public EspacioRespondeDto actualizarEspacio(UUID idEspacio, EspacioRequestDto dto) {
        Espacio objEspacio = repositorio.findById(idEspacio)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Espacio no encontrado con id: " + idEspacio));

        validarEspacioNoEliminado(objEspacio);

        if (repositorio.existsByCodigoAndIdNot(dto.getCodigo(), idEspacio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "YA EXISTE EL CODIGO DEL ESPACIO");
        }

        Zona objZona = repositorioZona.findById(dto.getIdZona())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Zona no encontrada con id: " + dto.getIdZona()));

        if (!objEspacio.getZona().getId().equals(dto.getIdZona())) {
            long espaciosRegistrados = repositorio.countByZonaIdAndEliminadoFalse(dto.getIdZona());
            if (espaciosRegistrados >= objZona.getCapacidad()) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La zona destino ya alcanzo su capacidad maxima de espacios");
            }
        }

        objEspacio.setCodigo(dto.getCodigo());
        objEspacio.setDescripcion(dto.getDescripcion());
        objEspacio.setTipo(dto.getTipo());
        objEspacio.setZona(objZona);

        if (dto.getEstado() != null) {
            objEspacio.setEstado(dto.getEstado());
        }

        objEspacio.setFechaModificacion(LocalDateTime.now());

        Espacio espacioSaved = repositorio.save(objEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    @Transactional
    public void eliminarEspacio(UUID idEspacio) {
        Espacio objEspacio = repositorio.findById(idEspacio)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Espacio no encontrado con id: " + idEspacio));

        if (objEspacio.isEliminado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El espacio ya se encuentra eliminado");
        }

        if (objEspacio.getEstado() == EspacioEstado.OCUPADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar un espacio ocupado");
        }

        objEspacio.setEliminado(true);
        objEspacio.setActivo(false);
        objEspacio.setFechaModificacion(LocalDateTime.now());
        repositorio.save(objEspacio);
    }

    @Override
    @Transactional
    public EspacioRespondeDto cambiarEstado(UUID idEspacio, EspacioEstado estado) {
        Espacio objEspacio = repositorio.findById(idEspacio)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Espacio no encontrado con id: " + idEspacio));

        validarEspacioNoEliminado(objEspacio);

        if (!objEspacio.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El espacio se encuentra inactivo");
        }

        if (objEspacio.getEstado() == estado) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El espacio ya se encuentra en estado: " + estado);
        }

        objEspacio.setEstado(estado);
        objEspacio.setFechaModificacion(LocalDateTime.now());

        Espacio espacioSaved = repositorio.save(objEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacioRespondeDto> obtenerEspacioPorEstado(EspacioEstado estado) {
        return repositorio.findByEstadoAndEliminadoFalse(estado).stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacioRespondeDto> obtenerEspacioPorZona(UUID idZona, EspacioEstado estado) {
        if (!repositorioZona.existsById(idZona)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Zona no encontrada con id: " + idZona);
        }

        List<Espacio> espacios = estado == null
            ? repositorio.findByZonaIdAndEliminadoFalse(idZona)
            : repositorio.findByZonaIdAndEstadoAndEliminadoFalse(idZona, estado);

        return espacios.stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    private void validarEspacioNoEliminado(Espacio objEspacio) {
        if (objEspacio.isEliminado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El espacio se encuentra eliminado");
        }
    }
    
}
