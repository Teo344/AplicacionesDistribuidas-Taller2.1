package ec.edu.espe.zonas;

import java.util.UUID;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.TipoZona;
import ec.edu.espe.zonas.services.impl.ZonaServicioImpl;

public class ZonaServicioPruebaMain {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ZonasApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {

            ZonaServicioImpl servicio = context.getBean(ZonaServicioImpl.class);

            ZonaRequestDto request = ZonaRequestDto.builder()
                    .nombre("Zona Norte")
                    .descripcion("Zona regulada del bloque norte")
                    .tipo(TipoZona.VIP)
                    .capacidad(50)
                    .build();

            ZonaRespondeDto respuesta = servicio.crearZona(request);

            System.out.println("Zona creada correctamente");
            System.out.println("ID: " + respuesta.getIdZona());
            System.out.println("Nombre: " + respuesta.getNombre());
            System.out.println("Codigo: " + respuesta.getCodigo());
            System.out.println("Tipo: " + respuesta.getTipo());
            System.out.println("Capacidad: " + respuesta.getCapacidad());
        }
    }
}
