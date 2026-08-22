package com.project.FoodHub.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.exception.ImagenNoValidaException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@Slf4j
public class UploadImageService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final String folderId;
    private final String credentialsPath;

    private Drive driveService;
    private boolean available;

    public UploadImageService(
            @Value("${google.drive.folder.id}") String folderId,
            @Value("${google.credentials.path.storage}") String credentialsPath
    ) {
        this.folderId = folderId;
        this.credentialsPath = credentialsPath;
    }

    @PostConstruct
    public void init() {
        try {
            this.driveService = createDriveService();
            this.available = true;

            log.info("Google Drive integration initialized successfully");

        } catch (Exception e) {
            this.driveService = null;
            this.available = false;

            log.error("Google Drive integration initialization failed: {}", e.getMessage(), e);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {

        GoogleCredentials credentials;

        try(FileInputStream fileInputStream = new FileInputStream(credentialsPath)) {

            credentials = GoogleCredentials
                    .fromStream(fileInputStream)
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));
        }

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("FoodHub")
                .build();
    }

    public String guardarImagen(MultipartFile imagen) throws FotoPerfilException {
        validarEstadoDeServicio();

        String tipoArchivo = Optional.ofNullable(imagen.getContentType()).orElse("");
        List<String> tiposPermitidos = Arrays.asList("image/jpeg", "image/jpg", "image/png");

        if (tiposPermitidos.stream().noneMatch(tipoArchivo::equalsIgnoreCase)) {
            throw new ImagenNoValidaException("El archivo no es una imagen válida");
        }

        try {
            String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
            nombreArchivo = nombreArchivo.replaceAll("[^a-zA-Z0-9._-]", "");

            Path rutaCompleta = Paths.get(System.getProperty("java.io.tmpdir"), nombreArchivo);
            Files.write(rutaCompleta, imagen.getBytes());

            String url = uploadImageToDrive(rutaCompleta.toFile());

            // en caso no existen los directorios temporales, los crea
            Path directorioTemporal = Paths.get(System.getProperty("java.io.tmpdir"));
            if (!Files.exists(directorioTemporal)) {
                Files.createDirectories(directorioTemporal);
            }

            return url;
        } catch (IOException e) {
            throw new FotoPerfilException("Error al guardar la foto", e);
        }
    }

    private void validarEstadoDeServicio() throws FotoPerfilException {
        if (!available || driveService == null) {
            log.warn("Attempted image upload while object storage service is unavailable");
            throw new FotoPerfilException("El servicio de almacenamiento de imágenes no está disponible");
        }
    }

    private String uploadImageToDrive(File file) throws IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("image/jpeg", file);
        com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
    }
}
