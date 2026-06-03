package ec.edu.espe.zonas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.EspacioEstado;

public interface EspacioRepositorio extends JpaRepository<Espacio,UUID> {

    boolean existsByCodigo(String codigo);

    List<Espacio> findByZonaId(UUID idZona);

    List<Espacio> findByZonaIdAndEstado(UUID idZona, EspacioEstado estado);
    
    List<Espacio> findByEstado(EspacioEstado estado);

    
}
