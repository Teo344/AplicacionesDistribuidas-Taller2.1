package ec.edu.espe.zonas.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.entidades.Zona;
import ec.edu.espe.zonas.repositories.EspacioRepositorio;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.services.ZonaServicio;
import ec.edu.espe.zonas.utils.ZonaMapper;



@Service
public class ZonaServicioImpl implements ZonaServicio {

    private final ZonaRepositorio repositorioZona;
    private final EspacioRepositorio repositorioEspacio;
    private final ZonaCodigoGenerador zonaCodigoGenerador;
    private final ZonaMapper zonaMapper;

    public ZonaServicioImpl(
            ZonaRepositorio repositorioZona,
            EspacioRepositorio repositorioEspacio,
            ZonaCodigoGenerador zonaCodigoGenerador,
            ZonaMapper zonaMapper) {
        this.repositorioZona = repositorioZona;
        this.repositorioEspacio = repositorioEspacio;
        this.zonaCodigoGenerador = zonaCodigoGenerador;
        this.zonaMapper = zonaMapper;
    }

    @Override
    @Transactional()
    public List<ZonaRespondeDto> listarZonas() {
       return repositorioZona.findAll().stream()
            .map(zonaMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ZonaRespondeDto crearZona(ZonaRequestDto request) {

        validarCapacidad(request.getCapacidad());

        if(repositorioZona.existsByNombre(request.getNombre())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"YA EXISTE EL NOMBRE");
        }


        Zona objZona = new Zona();
        objZona.setNombre(request.getNombre());
        objZona.setCodigo(zonaCodigoGenerador.generar(request.getTipo()));
        objZona.setDescripcion(request.getDescripcion());
        objZona.setTipo(request.getTipo());
        objZona.setCapacidad(request.getCapacidad());

        repositorioZona.save(objZona);

        return zonaMapper.toResponse(objZona);
    }

    @Override
    @Transactional
    public ZonaRespondeDto actualizarZona(UUID idZona, ZonaRequestDto request) {
        validarCapacidad(request.getCapacidad());

        Zona objZona = repositorioZona.findById(idZona)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Zona no encontrada con id: " + idZona));

        long espaciosRegistrados = repositorioEspacio.countByZonaId(idZona);
        if (request.getCapacidad() < espaciosRegistrados) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La capacidad no puede ser menor a los espacios registrados: " + espaciosRegistrados);
        }

        if (repositorioZona.existsByNombreAndIdNot(request.getNombre(), idZona)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "YA EXISTE EL NOMBRE");
        }

        if (objZona.getTipo() != request.getTipo()) {
            objZona.setCodigo(zonaCodigoGenerador.generar(request.getTipo()));
        }

        objZona.setNombre(request.getNombre());
        objZona.setDescripcion(request.getDescripcion());
        objZona.setTipo(request.getTipo());
        objZona.setCapacidad(request.getCapacidad());
        objZona.setFechaModificacion(LocalDateTime.now());

        Zona zonaSaved = repositorioZona.save(objZona);

        return zonaMapper.toResponse(zonaSaved);
    }

    @Override
    @Transactional
    public void activarZona(UUID idZona) {
        Zona objZona = repositorioZona.findById(idZona)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Zona no encontrada con id: " + idZona));

        if (objZona.getEstado() == 1) {
            desactivarZona(objZona);
        } else {
            activarZonaConEspacios(objZona);
        }

        objZona.setFechaModificacion(LocalDateTime.now());

        repositorioZona.save(objZona);
    }

    private void activarZonaConEspacios(Zona objZona) {
        List<Espacio> espacios = repositorioEspacio.findByZonaId(objZona.getId());

        espacios.forEach(espacio -> {
            espacio.setActivo(true);
            espacio.setEstado(EspacioEstado.DISPONIBLE);
            espacio.setFechaModificacion(LocalDateTime.now());
        });

        repositorioEspacio.saveAll(espacios);
        objZona.setEstado(1);
    }

    private void desactivarZona(Zona objZona) {
        boolean existenEspaciosOcupados = repositorioEspacio.existsByZonaIdAndEstado(
            objZona.getId(),
            EspacioEstado.OCUPADO);

        if (existenEspaciosOcupados) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede desactivar la zona porque tiene espacios ocupados");
        }

        List<Espacio> espacios = repositorioEspacio.findByZonaId(objZona.getId());

        espacios.forEach(espacio -> {
            espacio.setActivo(false);
            espacio.setFechaModificacion(LocalDateTime.now());
        });

        repositorioEspacio.saveAll(espacios);
        objZona.setEstado(0);
    }

    private void validarCapacidad(int capacidad) {
        if (capacidad < 1 || capacidad > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La capacidad debe estar entre 1 y 100 espacios");
        }
    }
    
}
