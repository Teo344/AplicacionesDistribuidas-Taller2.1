package ec.edu.espe.zonas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.EspacioEstado;

public interface EspacioRepositorio extends JpaRepository<Espacio,UUID> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    boolean existsByZonaIdAndEstado(UUID idZona, EspacioEstado estado);

    boolean existsByZonaIdAndEstadoAndEliminadoFalse(UUID idZona, EspacioEstado estado);

    long countByZonaId(UUID idZona);

    long countByZonaIdAndEliminadoFalse(UUID idZona);

    List<Espacio> findByEliminadoFalse();

    List<Espacio> findByZonaId(UUID idZona);

    List<Espacio> findByZonaIdAndEliminadoFalse(UUID idZona);

    List<Espacio> findByZonaIdAndEstado(UUID idZona, EspacioEstado estado);

    List<Espacio> findByZonaIdAndEstadoAndEliminadoFalse(UUID idZona, EspacioEstado estado);
    
    List<Espacio> findByEstado(EspacioEstado estado);

    List<Espacio> findByEstadoAndEliminadoFalse(EspacioEstado estado);

    
}
