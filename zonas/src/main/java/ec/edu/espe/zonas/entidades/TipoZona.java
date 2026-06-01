package ec.edu.espe.zonas.entidades;

public enum TipoZona {
    VIP("VIP"),
    REGULAR("REG"),
    INTERNA("INT"),
    EXTERNA("EXT"),
    PREFERENCIAL("PRE");

    private final String abreviatura;

    TipoZona(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getAbreviatura() {
        return abreviatura;
    }
}
