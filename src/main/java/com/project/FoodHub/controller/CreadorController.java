package com.project.FoodHub.controller;

import com.project.FoodHub.dto.CreadorDTO;
import com.project.FoodHub.dto.MessageResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.service.ICreadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/creador")
@RequiredArgsConstructor
public class CreadorController {

    private final ICreadorService creadorService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/cantidadRecetas")
    public ResponseEntity<Integer> obtenerCantidadRecetasCreadas() {
        Integer cantidadRecetas = creadorService.obtenerCantidadDeRecetasCreadas();
        return ResponseEntity.ok(cantidadRecetas);
    }

    @GetMapping("/perfil")
    public ResponseEntity<CreadorDTO> verPerfil() {
        CreadorDTO perfil = creadorService.verPerfil();
        return ResponseEntity.ok(perfil);
    }

    @PostMapping("/actualizarFotoPerfil")
    public ResponseEntity<MessageResponse> actualizarFotoPerfil(@RequestPart("fotoPerfil") MultipartFile fotoPerfil) throws IOException, FotoPerfilException, ExecutionException, InterruptedException {
        MessageResponse response = creadorService.actualizarFotoPerfil(fotoPerfil);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/FotoPerfil")
    public ResponseEntity<Resource> obtenerFotoPerfil() {
        Long idCreador = creadorService.obtenerIdCreadorAutenticado();
        Creador creador = creadorService.obtenerCreadorPorId(idCreador);

        String googleDriveUrl = creador.getFotoPerfil();

        if (googleDriveUrl == null || googleDriveUrl.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        byte[] imageBytes = restTemplate.getForObject(googleDriveUrl, byte[].class);

        ByteArrayResource resource = new ByteArrayResource(imageBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + idCreador + ".jpg\"")
                .body(resource);
    }
}
