package com.project.FoodHub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreadorRequest {

    @NotBlank(message = "Por favor agrega un nombre")
    @Size(max = 20, message = "El nombre debe tener máximo 20 caracteres")
    private String nombre;

    @NotBlank(message = "Por favor agrega el apellido paterno")
    @Size(max = 20, message = "El apellido paterno debe tener máximo 20 caracteres")
    private String apellidoPaterno;

    @NotBlank(message = "Por favor agrega el apellido materno")
    @Size(max = 20, message = "El apellido materno debe tener máximo 20 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "Por favor agrega un correo")
    @Size(max = 30, message = "El correo debe tener máximo 30 caracteres")
    @Email(message = "Por favor agrega un correo válido")
    private String correoElectronico;

    @NotBlank(message = "Por favor agrega una contraseña")
    @Size(max = 15, message = "La contraseña debe tener máximo 15 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])[^\\s]+$", message = "La contraseña debe contener al menos un número y una letra mayúscula")
    private String contrasenia;

    @NotBlank(message = "Por favor ingresa un código de colegiatura")
    @Size(min = 6, max = 6, message = "El código de colegiatura debe tener exactamente 6 caracteres")
    @Pattern(regexp = "\\d+", message = "El código de colegiatura solo debe contener números")
    private String codigoColegiatura;
}
