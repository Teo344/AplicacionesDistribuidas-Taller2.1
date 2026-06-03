package ec.edu.espe.zonas.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.entidades.TipoEspacio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioRespondeDto {
    private UUID id;
    private String codigo;
    private String descripcion;
    private TipoEspacio tipo;
    private EspacioEstado estado;
    private boolean activo;

    private String nombreZona;
    private UUID idZona;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
