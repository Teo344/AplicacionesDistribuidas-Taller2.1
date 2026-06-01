package ec.edu.espe.zonas.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.Zona;
import ec.edu.espe.zonas.mappers.ZonaMapper;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.services.ZonaServicio;



@Service
public class ZonaServicioImpl implements ZonaServicio {

    private final ZonaRepositorio repositorioZona;
    private final ZonaCodigoGenerador zonaCodigoGenerador;
    private final ZonaMapper zonaMapper;

    public ZonaServicioImpl(
            ZonaRepositorio repositorioZona,
            ZonaCodigoGenerador zonaCodigoGenerador,
            ZonaMapper zonaMapper) {
        this.repositorioZona = repositorioZona;
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


        if(repositorioZona.existsByNombre(request.getNombre())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"YA EXISTE EL NOMBRE");
        }


        Zona objZona = new Zona();
        objZona.setNombre(request.getNombre());
        objZona.setCodigo(zonaCodigoGenerador.generar(request.getTipo()));
        objZona.setDescripcion(request.getDescripcion());
        objZona.setTipo(request.getTipo());

        repositorioZona.save(objZona);

        return zonaMapper.toResponse(objZona);
    }

    @Override
    public ZonaRespondeDto actualizarZona(UUID idZona, ZonaRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizarZona'");
    }

    @Override
    public void activarZona(UUID idZona) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'activarZona'");
    }
    
}
