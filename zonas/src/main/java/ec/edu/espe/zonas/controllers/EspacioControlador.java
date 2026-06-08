package ec.edu.espe.zonas.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspacioRespondeDto;
import ec.edu.espe.zonas.entidades.EspacioEstado;
import ec.edu.espe.zonas.services.EspacioServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/espacios")
@RequiredArgsConstructor
public class EspacioControlador {

    private final EspacioServicio espacioServicio;

    @GetMapping("/")
    public ResponseEntity<List<EspacioRespondeDto>> listarEspacios() {
        return ResponseEntity.ok(espacioServicio.obtenerEspacio());
    }

    @PostMapping("/")
    public ResponseEntity<EspacioRespondeDto> crearEspacio(
            @Valid @RequestBody EspacioRequestDto request) {
        return new ResponseEntity<>(espacioServicio.crearEspacio(request), HttpStatus.CREATED);
    }

    @PutMapping("/{idEspacio}")
    public ResponseEntity<EspacioRespondeDto> actualizarEspacio(
            @PathVariable UUID idEspacio,
            @Valid @RequestBody EspacioRequestDto request) {
        return ResponseEntity.ok(espacioServicio.actualizarEspacio(idEspacio, request));
    }

    @PatchMapping("/{idEspacio}/estado/{estado}")
    public ResponseEntity<EspacioRespondeDto> cambiarEstado(
            @PathVariable UUID idEspacio,
            @PathVariable EspacioEstado estado) {
        return ResponseEntity.ok(espacioServicio.cambiarEstado(idEspacio, estado));
    }

    @DeleteMapping("/{idEspacio}")
    public ResponseEntity<Void> eliminarEspacio(@PathVariable UUID idEspacio) {
        espacioServicio.eliminarEspacio(idEspacio);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EspacioRespondeDto>> listarPorEstado(
            @PathVariable EspacioEstado estado) {
        return ResponseEntity.ok(espacioServicio.obtenerEspacioPorEstado(estado));
    }

    @GetMapping("/zona/{idZona}")
    public ResponseEntity<List<EspacioRespondeDto>> listarPorZona(
            @PathVariable UUID idZona,
            @RequestParam(required = false) EspacioEstado estado) {
        return ResponseEntity.ok(espacioServicio.obtenerEspacioPorZona(idZona, estado));
    }
}
