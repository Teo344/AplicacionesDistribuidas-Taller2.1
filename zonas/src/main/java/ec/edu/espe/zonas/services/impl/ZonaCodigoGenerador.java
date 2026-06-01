package ec.edu.espe.zonas.services.impl;

import org.springframework.stereotype.Component;

import ec.edu.espe.zonas.entidades.TipoZona;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;

@Component
public class ZonaCodigoGenerador {

    private static final String PREFIJO_ZONA = "ZON";

    private final ZonaRepositorio repositorioZona;

    public ZonaCodigoGenerador(ZonaRepositorio repositorioZona) {
        this.repositorioZona = repositorioZona;
    }

    public String generar(TipoZona tipoZona) {
        long secuencial = repositorioZona.countByTipo(tipoZona) + 1;
        String codigo = construirCodigo(tipoZona, secuencial);

        while (repositorioZona.existsByCodigo(codigo)) {
            secuencial++;
            codigo = construirCodigo(tipoZona, secuencial);
        }

        return codigo;
    }

    private String construirCodigo(TipoZona tipoZona, long secuencial) {
        return String.format("%s-%s-%02d", PREFIJO_ZONA, tipoZona.getAbreviatura(), secuencial);
    }
}
