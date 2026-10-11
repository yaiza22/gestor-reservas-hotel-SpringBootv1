package com.gestor_reservas_hotel.gestor_reservas_hotel.services;

import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.dto.UsuarioUpdateRequest;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.ConflictoNegocioException;
import com.gestor_reservas_hotel.gestor_reservas_hotel.exception.RecursoNoEncontradoException;
import com.gestor_reservas_hotel.gestor_reservas_hotel.model.Usuario;
import com.gestor_reservas_hotel.gestor_reservas_hotel.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio del módulo de usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private static final String MENSAJE_NO_EXISTE = "No existe un usuario con ese ID.";

    private final UsuarioRepository repository;

    /**
     * Crea un usuario nuevo.
     *
     * @param request datos del usuario
     * @return el usuario guardado, con su id
     * @throws ConflictoNegocioException si ya existe un usuario con ese tipo y número de documento (RN-11)
     */
    public Usuario crear(UsuarioRequest request) {
        // RN-11: el documento (tipo + número) es único dentro del módulo de usuarios
        if (repository.existsByTipoDocIdentidadAndNumDocIdentidad(request.tipoDocIdentidad(),
                request.numDocIdentidad())) {
            throw new ConflictoNegocioException("Ya existe un usuario con ese documento.");
        }
        Usuario usuario = new Usuario();
        usuario.setTipoDocIdentidad(request.tipoDocIdentidad());
        usuario.setNumDocIdentidad(request.numDocIdentidad());
        usuario.setNombre(request.nombre());
        usuario.setTelefono(request.telefono());
        usuario.setCorreo(request.correo());
        // RN-13: por ahora se guarda tal cual (BCrypt es un extra) y nunca se devuelve ni se registra
        usuario.setPassword(request.password());
        usuario.setRol(request.rol());
        Usuario guardado = repository.save(usuario);
        log.info("Usuario creado id={}", guardado.getId());
        return guardado;
    }

    /**
     * Lista todos los usuarios ordenados por id.
     *
     * @return la lista de usuarios (vacía si no hay ninguno)
     */
    public List<Usuario> listar() {
        return repository.findAll();
    }

    /**
     * Busca un usuario por su id.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws RecursoNoEncontradoException si no existe un usuario con ese id
     */
    public Usuario obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_NO_EXISTE));
    }

    /**
     * Indica si existe un usuario con ese id.
     *
     * @param id identificador del usuario
     * @return {@code true} si existe
     */
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    /**
     * Actualiza los datos editables de un usuario; el documento y la contraseña no cambian (RN-11).
     *
     * @param id      identificador del usuario
     * @param request nuevos datos
     * @return el usuario actualizado
     * @throws RecursoNoEncontradoException si no existe un usuario con ese id
     */
    public Usuario actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(request.nombre());
        usuario.setTelefono(request.telefono());
        usuario.setCorreo(request.correo());
        usuario.setRol(request.rol());
        Usuario guardado = repository.save(usuario);
        log.info("Usuario actualizado id={}", guardado.getId());
        return guardado;
    }

    /**
     * Elimina un usuario.
     *
     * @param id identificador del usuario
     * @throws RecursoNoEncontradoException si no existe un usuario con ese id
     */
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.deleteById(id);
        log.info("Usuario eliminado id={}", id);
    }
}
