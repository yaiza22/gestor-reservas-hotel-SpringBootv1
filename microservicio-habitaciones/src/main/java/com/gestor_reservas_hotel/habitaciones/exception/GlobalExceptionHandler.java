package com.gestor_reservas_hotel.habitaciones.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Aplicamos el formato que queremos a cada excepción posible.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String CODIGO_VALIDACION = "VALIDACION";

    /**
     * Construimos el detalle de cada problema con sus propios parámetros
     */
    private ResponseEntity<ProblemDetail> construir(HttpStatus estado, String titulo, String detalle, String codigo, List<ErrorCampo> errores) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        problema.setProperty("codigo", codigo);
        if (errores != null) {
            problema.setProperty("errores", errores);
        }
        problema.setProperty("timestamp", Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return ResponseEntity.status(estado).body(problema);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> manejarNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return construir(HttpStatus.NOT_FOUND, "No encontrado", e.getMessage(), "NO_ENCONTRADO", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> manejarRutaInexistente(NoResourceFoundException e) {
        log.warn("Ruta inexistente: {}", e.getResourcePath());
        return construir(HttpStatus.NOT_FOUND, "No encontrado", "La ruta solicitada no existe", "NO_ENCONTRADO", null);
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ProblemDetail> manejarConflicto(ConflictoNegocioException e) {
        log.warn("Conflicto de negocio: {}", e.getMessage());
        return construir(HttpStatus.CONFLICT, "Conflicto", e.getMessage(), "CONFLICTO", null);
    }

    @ExceptionHandler(ReferenciaInvalidaException.class)
    public ResponseEntity<ProblemDetail> manejarReferenciaInvalida(ReferenciaInvalidaException e) {
        log.warn("Referencia inexistente: {}", e.getMessage());
        List<ErrorCampo> errores = List.of(new ErrorCampo(e.getCampo(), e.getMessage()));
        return construir(HttpStatus.BAD_REQUEST, "Referencia inexistente", e.getMessage(), "REFERENCIA_INEXISTENTE", errores);
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ProblemDetail> manejarServicioNoDisponible(ServicioNoDisponibleException e) {
        log.warn("Servicio no disponible: {}", e.getMessage());
        return construir(HttpStatus.SERVICE_UNAVAILABLE, "Servicio no disponible", e.getMessage(), "SERVICIO_NO_DISPONIBLE", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> manejarValidacion(MethodArgumentNotValidException e) {
        List<ErrorCampo> errores = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorCampo(error.getField(), error.getDefaultMessage()))
                .toList();
        log.warn("Validación fallida en {} campo(s)", errores.size());
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida", "Hay campos con errores", CODIGO_VALIDACION, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> manejarJsonIlegible(HttpMessageNotReadableException e) {
        log.warn("JSON ilegible o valor no válido en el cuerpo");
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida", "El cuerpo de la solicitud está mal formado o tiene un valor no válido.", CODIGO_VALIDACION, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> manejarParametroInvalido(MethodArgumentTypeMismatchException e) {
        log.warn("Valor inválido en el parámetro {}", e.getName());
        String detalle = "El valor del parámetro '" + e.getName() + "' no es válido.";
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida", detalle, CODIGO_VALIDACION, List.of(new ErrorCampo(e.getName(), detalle)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> manejarParametroAusente(MissingServletRequestParameterException e) {
        log.warn("Falta el parámetro {}", e.getParameterName());
        String detalle = "El parámetro '" + e.getParameterName() + "' es obligatorio.";
        return construir(HttpStatus.BAD_REQUEST, "Solicitud inválida", detalle, CODIGO_VALIDACION, List.of(new ErrorCampo(e.getParameterName(), detalle)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> manejarInesperado(Exception ex) {
        log.error("Error inesperado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ocurrió un error inesperado. Intenta de nuevo más tarde.", "ERROR_INTERNO", null);
    }
}