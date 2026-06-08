package ec.edu.espe.zonas;

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
import ec.edu.espe.zonas.services.impl.EspacioServicioImpl;
import ec.edu.espe.zonas.services.impl.ZonaServicioImpl;

public class ZonaServicioPruebaMain {

    public static void main(String[] args) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");

        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ZonasApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("local")
                .properties(
                        "spring.main.banner-mode=off",
                        "debug=false",
                        "logging.level.root=ERROR",
                        "logging.level.org.springframework=ERROR",
                        "logging.level.org.hibernate=ERROR",
                        "logging.level.com.zaxxer.hikari=ERROR")
                .run(args)) {

            ZonaServicioImpl zonaServicio = context.getBean(ZonaServicioImpl.class);
            EspacioServicioImpl espacioServicio = context.getBean(EspacioServicioImpl.class);

            imprimirTitulo();

            imprimirSeccion("1. Crear zona con capacidad limitada");
            ZonaRequestDto request = ZonaRequestDto.builder()
                    .nombre("Zona Norte Demo")
                    .descripcion("Zona regulada del bloque norte")
                    .tipo(TipoZona.VIP)
                    .capacidad(2)
                    .build();

            ZonaRespondeDto zona = zonaServicio.crearZona(request);
            imprimirZona(zona);

            imprimirSeccion("2. Crear espacios dentro de la capacidad");
            EspacioRespondeDto espacioUno = espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("ESP-001")
                    .descripcion("Primer espacio")
                    .tipo(TipoEspacio.AUTO)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build());

            EspacioRespondeDto espacioDos = espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("ESP-002")
                    .descripcion("Segundo espacio")
                    .tipo(TipoEspacio.MOTO)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build());

            imprimirEspacios(List.of(espacioUno, espacioDos));

            imprimirSeccion("3. Activar zona y espacios asociados");
            zonaServicio.activarZona(zona.getIdZona());
            imprimirResultado("Zona activada", "Los espacios quedan activos y DISPONIBLES");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("4. Validar capacidad maxima");
            ejecutarCasoEsperado("Crear espacio superando capacidad", () -> espacioServicio.crearEspacio(EspacioRequestDto.builder()
                    .idZona(zona.getIdZona())
                    .codigo("ESP-003")
                    .descripcion("Tercer espacio")
                    .tipo(TipoEspacio.AUTO)
                    .estado(EspacioEstado.DISPONIBLE)
                    .build()));

            imprimirSeccion("5. Cambiar un espacio a OCUPADO");
            espacioUno = espacioServicio.cambiarEstado(espacioUno.getId(), EspacioEstado.OCUPADO);
            imprimirEspacios(List.of(espacioUno));

            imprimirSeccion("6. Intentar desactivar zona con espacio ocupado");
            ejecutarCasoEsperado("Desactivar zona con espacio ocupado", () -> zonaServicio.activarZona(zona.getIdZona()));

            imprimirSeccion("7. Liberar espacio ocupado");
            espacioUno = espacioServicio.cambiarEstado(espacioUno.getId(), EspacioEstado.DISPONIBLE);
            imprimirEspacios(List.of(espacioUno));

            imprimirSeccion("8. Desactivar zona sin espacios ocupados");
            zonaServicio.activarZona(zona.getIdZona());
            imprimirResultado("Zona desactivada", "Todos los espacios quedan con activo=false");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("9. Activar zona nuevamente");
            zonaServicio.activarZona(zona.getIdZona());
            imprimirResultado("Zona activada", "Todos los espacios quedan activos y DISPONIBLES");
            imprimirEspacios(espacioServicio.obtenerEspacioPorZona(zona.getIdZona(), null));

            imprimirSeccion("Fin de pruebas");
            System.out.println("Todos los escenarios principales fueron ejecutados.");
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
        System.out.println(" PRUEBA LOCAL DE ZONAS Y ESPACIOS");
        System.out.println(" Base de datos: H2 en memoria | Perfil: local");
        System.out.println("============================================================");
    }

    private static void imprimirSeccion(String titulo) {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(titulo);
        System.out.println("------------------------------------------------------------");
    }

    private static void imprimirZona(ZonaRespondeDto zona) {
        System.out.printf("%-12s %-20s %-12s %-10s %-10s%n", "ID", "NOMBRE", "CODIGO", "TIPO", "CAPACIDAD");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-12s %-20s %-12s %-10s %-10d%n",
                resumirId(zona.getIdZona().toString()),
                zona.getNombre(),
                zona.getCodigo(),
                zona.getTipo(),
                zona.getCapacidad());
    }

    private static void imprimirEspacios(List<EspacioRespondeDto> espacios) {
        System.out.printf("%-12s %-10s %-10s %-14s %-8s %-12s%n",
                "ID", "CODIGO", "TIPO", "ESTADO", "ACTIVO", "ZONA");
        System.out.println("--------------------------------------------------------------------------------");

        espacios.forEach(espacio -> System.out.printf("%-12s %-10s %-10s %-14s %-8s %-12s%n",
                resumirId(espacio.getId().toString()),
                espacio.getCodigo(),
                espacio.getTipo(),
                espacio.getEstado(),
                espacio.isActivo(),
                resumirId(espacio.getIdZona().toString())));
    }

    private static void imprimirResultado(String caso, String resultado) {
        System.out.printf("%-40s -> %s%n", caso, resultado);
    }

    private static String resumirId(String id) {
        return id.substring(0, 8);
    }
}
