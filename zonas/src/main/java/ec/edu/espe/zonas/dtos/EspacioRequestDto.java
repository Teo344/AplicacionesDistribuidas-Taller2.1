package ec.edu.espe.zonas.dtos;

import java.util.UUID;

import ec.edu.espe.zonas.entidades.TipoEspacio;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "El id de la zona no puede ester vacio")
    private UUID idZona;

    
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "El tipo de espacio es obligatorio")
    private TipoEspacio tipo;
}
