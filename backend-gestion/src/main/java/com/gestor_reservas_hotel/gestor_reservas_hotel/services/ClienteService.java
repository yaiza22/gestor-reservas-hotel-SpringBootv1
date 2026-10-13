package com.gestor_reservas_hotel.gestor_reservas_hotel.services;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.ClienteUpdateRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.ConflictoNegocioException;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.RecursoNoEncontradoException;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Cliente;
import com.gestor_reservas_hotel.gestor_reservas_hotel.repository.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio del módulo de clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private static final String MENSAJE_NO_EXISTE = "No existe un cliente con ese ID.";

    private final ClienteRepository repository;

    /**
     * Crea un cliente nuevo.
     *
     * @param request datos del cliente
     * @return el cliente guardado, con su id
     * @throws ConflictoNegocioException si ya existe un cliente con ese tipo y número de documento (RN-11)
     */
    public Cliente crear(ClienteRequest request) {
        // RN-11: el documento (tipo + número) es único dentro del módulo de clientes
        if (repository.existsByTipoDocIdentidadAndNumDocIdentidad(request.tipoDocIdentidad(),
                request.numDocIdentidad())) {
            throw new ConflictoNegocioException("Ya existe un cliente con ese documento.");
        }
        Cliente cliente = new Cliente();
        cliente.setTipoDocIdentidad(request.tipoDocIdentidad());
        cliente.setNumDocIdentidad(request.numDocIdentidad());
        cliente.setNombre(request.nombre());
        cliente.setTelefono(request.telefono());
        cliente.setCorreo(request.correo());
        // RN-13: por ahora se guarda tal cual (BCrypt es un extra) y nunca se devuelve ni se registra
        cliente.setPassword(request.password());
        cliente.setNacionalidad(request.nacionalidad());
        cliente.setPaisResidencia(request.paisResidencia());
        Cliente guardado = repository.save(cliente);
        log.info("Cliente creado id={}", guardado.getId());
        return guardado;
    }

    /**
     * Lista todos los clientes ordenados por id.
     *
     * @return la lista de clientes (vacía si no hay ninguno)
     */
    public List<Cliente> listar() {
        return repository.findAll();
    }

    /**
     * Busca un cliente por su id.
     *
     * @param id identificador del cliente
     * @return el cliente encontrado
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    public Cliente obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_NO_EXISTE));
    }

    /**
     * Indica si existe un cliente con ese id (lo usará el módulo de reservas).
     *
     * @param id identificador del cliente
     * @return {@code true} si existe
     */
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    /**
     * Actualiza los datos editables de un cliente; el documento y la contraseña no cambian (RN-11).
     *
     * @param id      identificador del cliente
     * @param request nuevos datos
     * @return el cliente actualizado
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    public Cliente actualizar(Long id, ClienteUpdateRequest request) {
        Cliente cliente = obtenerPorId(id);
        cliente.setNombre(request.nombre());
        cliente.setTelefono(request.telefono());
        cliente.setCorreo(request.correo());
        cliente.setNacionalidad(request.nacionalidad());
        cliente.setPaisResidencia(request.paisResidencia());
        Cliente guardado = repository.save(cliente);
        log.info("Cliente actualizado id={}", guardado.getId());
        return guardado;
    }

    /**
     * Elimina un cliente. No revisa si tiene reservas (limitación conocida de v1, RN-12).
     *
     * @param id identificador del cliente
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.deleteById(id);
        log.info("Cliente eliminado id={}", id);
    }
}
