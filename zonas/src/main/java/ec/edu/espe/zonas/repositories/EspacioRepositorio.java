package ec.edu.espe.zonas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.zonas.entidades.Espacio;

public interface EspacioRepositorio extends JpaRepository<Espacio,UUID> {

    boolean existsByCodigo(String codigo);

    List<Espacio> findByZona(UUID idZona);

    List<Espacio> findByZonaAndEstado(UUID idZona, int estado);
    
    List<Espacio> findByEstado(boolean estado);

    
}
