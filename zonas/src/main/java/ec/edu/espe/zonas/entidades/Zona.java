package ec.edu.espe.zonas.entidades;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zonas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Zona {

    /*
        Ticket-1A-23-20052026

    */


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true , nullable = false, length = 32)
    private String nombre;

    @Column
    private String descripcion;

    @Column(unique = true , nullable = false , length = 11)
    private String codigo;

    @Column
    private int estado; //1: activo ; 0: inactivo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoZona tipo;


    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Espacio> espacios = new ArrayList<>();

    @Column
    private int capacidad;

    @Column
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaModificacion;

}