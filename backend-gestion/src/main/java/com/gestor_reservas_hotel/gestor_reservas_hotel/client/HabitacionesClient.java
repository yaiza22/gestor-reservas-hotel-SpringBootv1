package com.gestor_reservas_hotel.gestor_reservas_hotel.client;

import com.gestor_reservas_hotel.gestor_reservas_hotel.client.dto.*;
import com.gestor_reservas_hotel.gestor_reservas_hotel.config.PropiedadesHabitaciones;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.ReferenciaInvalidaException;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.ServicioNoDisponibleException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * Cliente http hacia el microservicio habitaciones
 */
@Component
@EnableConfigurationProperties(PropiedadesHabitaciones.class)
@Slf4j
public class HabitacionesClient {
    private static final String RUTA_HABITACIONES = "/api/habitaciones";
    private static final String CAMPO_HABITACIONES = "habitacionesId";
    private static final String MENSAJE_SERVICIO_CAIDO =
            "El servicio de habitaciones no responde. Intenta de nuevo en unos minutos.";
    private static final long NANOSEGUNDOS_POR_MILISEGUNDO = 1_000_000L;

    private final RestClient restClient;

    public HabitacionesClient(RestClient.Builder builder, PropiedadesHabitaciones propiedades) {
        // Se usa el cliente HTTP del JDK porque soporta PATCH
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(propiedades.timeoutConexionMs())).build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpClient);
        fabrica.setReadTimeout(Duration.ofMillis(propiedades.timeoutLecturaMs()));
        this.restClient = builder.baseUrl(propiedades.urlBase()).requestFactory(fabrica).build();
    }

    private long milisegundosDesde(long inicioEnNanosegundos) {
        return (System.nanoTime() - inicioEnNanosegundos) / NANOSEGUNDOS_POR_MILISEGUNDO;
    }

    /**
     * Se ejecuta una llamada, registra el log [habitaciones] y traduce los errores a excepciones de dominio.
     */
    private <T> T ejecutar(String metodo, String ruta, Long id, Supplier<ResponseEntity<T>> llamada) {
        long inicio = System.nanoTime();
        try {
            ResponseEntity<T> respuesta = llamada.get();
            log.info("[habitaciones] {} {} -> {} ({} ms)", metodo, ruta,
                    respuesta.getStatusCode().value(), milisegundosDesde(inicio));
            return respuesta.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[habitaciones] {} {} -> 404 ({} ms)", metodo, ruta, milisegundosDesde(inicio));
            throw new ReferenciaInvalidaException(CAMPO_HABITACIONES,
                    "No existe una habitación con el ID: " + id + ".");
        } catch (RestClientResponseException e) {
            // Cuando recibe otra respuesta de error (500, 400...) el problema es del servicio
            log.warn("[habitaciones] {} {} -> {} ({} ms)", metodo, ruta,
                    e.getStatusCode().value(), milisegundosDesde(inicio));
            throw new ServicioNoDisponibleException(MENSAJE_SERVICIO_CAIDO);
        } catch (RestClientException e) {
            // Cuando no hay respuesta: servicio apagado, conexión rechazada o timeout
            log.warn("[habitaciones] {} {} -> sin respuesta ({} ms): {}", metodo, ruta,
                    milisegundosDesde(inicio), e.getMessage());
            throw new ServicioNoDisponibleException(MENSAJE_SERVICIO_CAIDO);
        }
    }

    /**
     * Consulta de habitación desde el microservicio.
     */
    public HabitacionRemota obtenerPorId(Long id) {
        String ruta = RUTA_HABITACIONES + "/" + id;
        return ejecutar("GET", ruta, id, () -> restClient.get().uri(ruta).retrieve()
                .toEntity(HabitacionRemota.class));
    }

    /**
     * Pide el precio por noche de una habitación
     */
    public PrecioRemoto obtenerPrecio(Long id, String temporada) {
        String ruta = RUTA_HABITACIONES + "/" + id + "/precio";
        return ejecutar("GET", ruta + "?temporada=" + temporada, id, () -> restClient.get()
                .uri(uriBuilder -> uriBuilder.path(ruta).queryParam("temporada", temporada).build())
                .retrieve().toEntity(PrecioRemoto.class));
    }

    /**
     * Cambio de estado de la habitación: check-in -> OCUPADA y check-out -> LIMPIEZA).
     */
    public HabitacionRemota cambiarEstado(Long id, EstadoHabitacionRemoto estado) {
        String ruta = RUTA_HABITACIONES + "/" + id + "/estado";
        return ejecutar("PATCH", ruta, id, () -> restClient.patch().uri(ruta).contentType(MediaType.APPLICATION_JSON)
                .body(new CambioEstadoRemoto(estado)).retrieve().toEntity(HabitacionRemota.class));
    }
}
