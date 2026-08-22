package com.project.FoodHub.service.impl;

import com.project.FoodHub.dto.ConfirmacionResponse;
import com.project.FoodHub.dto.RecetaDTOResponse;
import com.project.FoodHub.dto.RecetaRequest;
import com.project.FoodHub.dto.RecetasCategoriaResponse;
import com.project.FoodHub.entity.Creador;
import com.project.FoodHub.entity.Ingrediente;
import com.project.FoodHub.entity.Instruccion;
import com.project.FoodHub.entity.Receta;
import com.project.FoodHub.enumeration.Categoria;
import com.project.FoodHub.exception.CreadorNoEncontradoException;
import com.project.FoodHub.exception.FotoPerfilException;
import com.project.FoodHub.exception.RecetaNoEncontradaException;
import com.project.FoodHub.exception.UsuarioNoAutenticadoException;
import com.project.FoodHub.repository.CreadorRepository;
import com.project.FoodHub.repository.IngredienteRepository;
import com.project.FoodHub.repository.InstruccionRepository;
import com.project.FoodHub.repository.RecetaRepository;
import com.project.FoodHub.service.ICreadorService;
import com.project.FoodHub.service.IRecetaService;
import com.project.FoodHub.service.UploadImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements IRecetaService {

    private final RecetaRepository recetaRepository;
    private final CreadorRepository creadorRepository;
    private final IngredienteRepository ingredienteRepository;
    private final InstruccionRepository instruccionRepository;
    private final ICreadorService creadorService;
    private final UploadImageService uploadImageService;

    @Override
    @Transactional
    public ConfirmacionResponse crearReceta(RecetaRequest recetaRequest, MultipartFile imagen) throws FotoPerfilException {
        Long idCreador = obtenerIdCreadorAutenticado();

        Creador creador = creadorRepository.findById(idCreador)
                .orElseThrow(() -> new CreadorNoEncontradoException("Creador no encontrado con ID: " + idCreador));

        String nombreImagen =  uploadImageService.guardarImagen(imagen);

        Receta receta = Receta.builder()
                .titulo(recetaRequest.getTitulo())
                .descripcion(recetaRequest.getDescripcion())
                .tiempoCoccion(recetaRequest.getTiempoCoccion())
                .porciones(recetaRequest.getPorciones())
                .calorias(recetaRequest.getCalorias())
                .imagen(nombreImagen)
                .categoria(recetaRequest.getCategoria())
                .ingredientes(new ArrayList<>())
                .instrucciones(new ArrayList<>())
                .creador(creador)
                .build();

        recetaRepository.save(receta);

        agregarIngrediente(receta, recetaRequest.getIngredientes());

        agregarInstruccion(receta, recetaRequest.getInstrucciones());

        return new ConfirmacionResponse("Receta creada de forma exitosa", "success");
    }

    @Transactional
    public void agregarIngrediente(Receta receta, List<Ingrediente> ingredientes) {
        ingredientes.forEach(ingrediente -> ingrediente.setReceta(receta));
        ingredienteRepository.saveAll(ingredientes);
    }

    @Transactional
    public void agregarInstruccion(Receta receta, List<Instruccion> instrucciones) {
        instrucciones.forEach(instruccion -> instruccion.setReceta(receta));
        instruccionRepository.saveAll(instrucciones); // Save all instructions in bulk
    }

    @Override
    @Transactional
    public List<RecetasCategoriaResponse> mostrarRecetasPorCategoria(Categoria categoria, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        var recetasPage = recetaRepository.findByCategoria(categoria, pageRequest);

        List<RecetasCategoriaResponse> recetasResponse = new ArrayList<>();

        recetasPage.forEach(receta -> {
            RecetasCategoriaResponse recetaResponse = RecetasCategoriaResponse.builder()
                    .id(receta.getId())
                    .titulo(receta.getTitulo())
                    .descripcion(receta.getDescripcion())
                    .imagenReceta(receta.getImagenReceta())
                    .build();
            recetasResponse.add(recetaResponse);
        });

        return recetasResponse;
    }

    @Override
    public RecetaDTOResponse verReceta(Long idReceta) {
        Receta receta = recetaRepository.findById(idReceta)
                .orElseThrow(() -> new RecetaNoEncontradaException("Receta no encontrada"));

        String nombreCompleto = receta.getCreador().getNombre() + " " + receta.getCreador().getApellidoPaterno() + " " + receta.getCreador().getApellidoMaterno();

        return new RecetaDTOResponse(
                receta.getTitulo(),
                receta.getDescripcion(),
                receta.getTiempoCoccion(),
                receta.getPorciones(),
                receta.getCalorias(),
                receta.getImagen(),
                receta.getCategoria(),
                receta.getIngredientes(),
                receta.getInstrucciones(),
                receta.getCreador().getFotoPerfil(),
                nombreCompleto
        );
    }

    @Override
    public String obtenerUrlImagen(Long idReceta) {
        Receta receta = recetaRepository.findById(idReceta)
                .orElseThrow(() -> new RecetaNoEncontradaException("Receta no encontrada"));
        return receta.getImagen();
    }

    @Override
    public String obtenerImagenAutor(Long idReceta) {
        Creador creador = creadorRepository.findCreadorByRecetaId(idReceta)
                .orElseThrow(() -> new CreadorNoEncontradoException("Receta no encontrada"));

        return creador.getFotoPerfil();
    }

    private Long obtenerIdCreadorAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validarAutenticacion(authentication);
        String email = authentication.getName();
        return creadorService.obtenerCreadorPorEmail(email).getIdCreador();
    }

    private void validarAutenticacion(Authentication authentication) {
        Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new UsuarioNoAutenticadoException("Usuario no autenticado"));
    }

}
