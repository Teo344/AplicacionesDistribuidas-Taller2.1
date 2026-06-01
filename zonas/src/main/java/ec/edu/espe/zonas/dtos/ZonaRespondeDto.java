package ec.edu.espe.zonas.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.TipoZona;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRespondeDto {

    private UUID idZona;

    private String nombre;

    private String descripcion;

    private String codigo;

    private int estado; //1: activo ; 0: inactivo

    private TipoZona tipo;

    private List<Espacio> espacios;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaModificacion;
}
