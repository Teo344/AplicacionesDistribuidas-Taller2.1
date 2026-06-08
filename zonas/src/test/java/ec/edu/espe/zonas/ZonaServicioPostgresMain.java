package ec.edu.espe.zonas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspacioRespondeDto;
import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaRespondeDto;
import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.entidades.TipoEspacio;
import ec.edu.espe.zonas.entidades.TipoZona;
import ec.edu.espe.zonas.services.EspacioServicio;
import ec.edu.espe.zonas.services.ZonaServicio;

public class ZonaServicioPostgresMain {

    public static void main(String[] args) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");

        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ZonasApplication.class)
                .web(WebApplicationType.NONE)
                .properties(
                        "spring.main.banner-mode=off",
                        "debug=false",
                        "logging.level.root=ERROR",
                        "logging.level.org.springframework=ERROR",
                        "logging.level.org.hibernate=ERROR",
                        "logging.level.com.zaxxer.hikari=ERROR")
                .run(args)) {

            ZonaServicio zonaServicio = context.getBean(ZonaServicio.class);
            EspacioServicio espacioServicio = context.getBean(EspacioServicio.class);
            String sufijo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            imprimirTitulo();

            imprimirSeccion("1. Crear zona en PostgreSQL");
            ZonaRespondeDto zona = zonaServicio.crearZona(ZonaRequestDto.builder()
                    .nombre("Zona PG " + sufijo)
                    .descripcion("Zona de prueba conectada a PostgreSQL")
                    .tipo(TipoZona.REGULAR)
                    .capacidad(2)
                    .build());
            imprimirZona(zona);

            imprimirSeccion("2. Crear espacios dentro de la zona");
            EspacioRespondeDto espacioUno = espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("PG-" + sufijo + "A")
                    .descripcion("Espacio PostgreSQL A")
                    .tipo(TipoEspacio.AUTO)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build());

            EspacioRespondeDto espacioDos = espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("PG-" + sufijo + "B")
                    .descripcion("Espacio PostgreSQL B")
                    .tipo(TipoEspacio.MOTO)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build());
            imprimirEspacios(List.of(espacioUno, espacioDos));

            imprimirSeccion("3. Validar limite de capacidad");
            ejecutarCasoEsperado("Crear tercer espacio", () -> espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("PG-" + sufijo + "C")
                    .descripcion("Espacio PostgreSQL C")
                    .tipo(TipoEspacio.BUSETA)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build()));

            imprimirSeccion("4. Cambiar estado de espacio a OCUPADO");
            espacioUno = espacioServicio.cambiarEstado(espacioUno.getId(), EspacioEstado.OCUPADO);
            imprimirEspacios(List.of(espacioUno));

            imprimirSeccion("5. Validar bloqueo al desactivar zona con espacio ocupado");
            ejecutarCasoEsperado("Desactivar zona ocupada", () -> zonaServicio.activarZona(zona.getIdZona()));

            imprimirSeccion("6. Liberar espacio y desactivar zona");
            espacioUno = espacioServicio.cambiarEstado(espacioUno.getId(), EspacioEstado.DISPONIBLE);
            zonaServicio.activarZona(zona.getIdZona());
            imprimirResultado("Zona desactivada", "Los espacios asociados quedan con activo=false");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("7. Activar zona nuevamente");
            zonaServicio.activarZona(zona.getIdZona());
            imprimirResultado("Zona activada", "Los espacios vuelven a activo=true y estado DISPONIBLE");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("8. Borrado logico de espacio");
            espacioServicio.eliminarEspacio(espacioDos.getId());
            imprimirResultado("Espacio eliminado", "El registro queda marcado como eliminado y no aparece en listados normales");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("9. Validar espacio eliminado");
            ejecutarCasoEsperado("Cambiar estado de espacio eliminado",
                    () -> espacioServicio.cambiarEstado(espacioDos.getId(), EspacioEstado.MANTENIMIENTO));

            imprimirSeccion("Fin de pruebas PostgreSQL");
            System.out.println("Pruebas ejecutadas contra la base configurada en application.yaml.");
        }
    }

    private static void ejecutarCasoEsperado(String nombreCaso, Runnable accion) {
        try {
            accion.run();
            imprimirResultado(nombreCaso, "ERROR: no fallo como se esperaba");
        } catch (ResponseStatusException ex) {
            imprimirResultado(nombreCaso, "OK: " + ex.getStatusCode() + " - " + ex.getReason());
        }
    }

    private static void imprimirTitulo() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" PRUEBA DE INTEGRACION DE ZONAS Y ESPACIOS");
        System.out.println(" Base de datos: PostgreSQL | Configuracion: application.yaml");
        System.out.println("============================================================");
    }

    private static void imprimirSeccion(String titulo) {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(titulo);
        System.out.println("------------------------------------------------------------");
    }

    private static void imprimirZona(ZonaRespondeDto zona) {
        System.out.printf("%-12s %-20s %-12s %-10s %-10s %-8s%n",
                "ID", "NOMBRE", "CODIGO", "TIPO", "CAPACIDAD", "ESTADO");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-12s %-20s %-12s %-10s %-10d %-8d%n",
                resumirId(zona.getIdZona().toString()),
                zona.getNombre(),
                zona.getCodigo(),
                zona.getTipo(),
                zona.getCapacidad(),
                zona.getEstado());
    }

    private static void imprimirEspacios(List<EspacioRespondeDto> espacios) {
        System.out.printf("%-12s %-12s %-10s %-14s %-8s %-12s%n",
                "ID", "CODIGO", "TIPO", "ESTADO", "ACTIVO", "ZONA");
        System.out.println("--------------------------------------------------------------------------------");

        espacios.forEach(espacio -> System.out.printf("%-12s %-12s %-10s %-14s %-8s %-12s%n",
                resumirId(espacio.getId().toString()),
                espacio.getCodigo(),
                espacio.getTipo(),
                espacio.getEstado(),
                espacio.isActivo(),
                resumirId(espacio.getIdZona().toString())));
    }

    private static void imprimirResultado(String caso, String resultado) {
        System.out.printf("%-42s -> %s%n", caso, resultado);
    }

    private static String resumirId(String id) {
        return id.substring(0, 8);
    }
}
