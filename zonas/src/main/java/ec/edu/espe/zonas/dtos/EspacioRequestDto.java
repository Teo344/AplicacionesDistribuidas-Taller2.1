package ec.edu.espe.zonas.dtos;

import java.util.UUID;

import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.entidades.TipoEspacio;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioRequestDto {
    

    @NotNull(message = "El id de la zona es obligatorio")
    private UUID idZona;

    @NotBlank(message = "El codigo del espacio es obligatorio")
    @Size(max = 12, message = "El codigo del espacio no debe superar 12 caracteres")
    private String codigo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de espacio es obligatorio")
    private TipoEspacio tipo;

    @Enumerated(EnumType.STRING)
    private EspacioEstado estado;
}
