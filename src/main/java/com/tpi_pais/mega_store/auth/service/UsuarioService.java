package com.tpi_pais.mega_store.auth.service;

import com.tpi_pais.mega_store.auth.dto.UsuarioDTO;
import com.tpi_pais.mega_store.auth.mapper.UsuarioMapper;
import com.tpi_pais.mega_store.auth.model.Usuario;
import com.tpi_pais.mega_store.auth.repository.UsuarioRepository;
import com.tpi_pais.mega_store.exception.BadRequestException;
import com.tpi_pais.mega_store.exception.NotFoundException;
import com.tpi_pais.mega_store.utils.ExpresionesRegulares;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UsuarioService implements IUsuarioService{

    @Autowired
    private UsuarioRepository modelRepository;

    @Override
    public List<UsuarioDTO> listar() {
        /*
        *  Trae todos los usuarios activos y no eliminados en orden ascendente
        * */
        List<Usuario> categorias = modelRepository.findByFechaEliminacionIsNullAndVerificadoTrueOrderByIdAsc();
        return categorias.stream().map(UsuarioMapper::toDTO).toList();
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        /*
         * Trae el usuario por su id si no lo encuentra lanza una excepcion
         * Si esta eliminado o inactivo lanza una excepcion
         * */
        Optional<Usuario> model = modelRepository.findByFechaEliminacionIsNullAndVerificadoTrueAndId(id);
        if (model.isEmpty()) {
            throw new NotFoundException("El usuario con el id " + id + " no existe o no se encuentra activo.");
        }
        return model.get();
    }

    @Override
    public Usuario buscarEliminadoPorId(Integer id) {
        /*
         * Trae el usuario por su id si no lo encuentra lanza una excepcion
         * Si esta eliminado o no verificado lanza una excepcion
         * */
        Optional<Usuario> model = modelRepository.findById(id);
        if (model.isEmpty()) {
            throw new NotFoundException("El usuario con el id " + id + " no existe.");
        }
        if (!model.get().esEliminado()) {
            throw new NotFoundException("El usuario con el id " + id + " no se encuentra eliminado.");
        }
        return model.get();
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        /*
         * Trae el usuario por su email si no lo encuentra lanza una excepcion
         * Si esta eliminado o no verificado lanza una excepcion
         * */
        Optional<Usuario> model = modelRepository.findByFechaEliminacionIsNullAndVerificadoTrueAndEmail(email);
        if (model.isEmpty()) {
            throw new NotFoundException("El usuario con email " + email + " no existe o no se encuentra activo.");
        }
        return model.get();
    }
    @Override
    public Usuario buscarEliminadoPorEmail(String email) {
        /*
         * Trae el usuario por su email si no lo encuentra lanza una excepcion
         * Si esta eliminado o inactivo lanza una excepcion
         * */
        Optional<Usuario> model = modelRepository.findByEmail(email);
        if (model.isEmpty()) {
            throw new NotFoundException("El usuario con email " + email + " no existe.");
        }
        if (!model.get().esEliminado()) {
            throw new NotFoundException("El usuario con email " + email + " no se encuentra eliminado.");
        }
        return model.get();
    }

    @Override
    public UsuarioDTO guardar(UsuarioDTO modelDTO) {
        /*
         * Guarda un usuario a partir de un DTO
         * */
        Usuario model = UsuarioMapper.toEntity(modelDTO);
        return UsuarioMapper.toDTO(modelRepository.save(model));
    }
    @Override
    public Usuario guardar(Usuario model) {
        /*
         * Guarda un usuario a partir de un modelo
         * */
        return modelRepository.save(model);
    }

    @Override
    public void eliminar(Usuario model) {
        /*
         * Elimina un usuario a partir de un modelo y si ya fue eliminado lanza una excepcion
         * */
        if (model.esEliminado()) {
            throw new BadRequestException("El usuario con id " + model.getId() + " ya fue eliminado.");
        }
        model.eliminar();
        modelRepository.save(model);
    }
    @Override
    public void recuperar(Usuario model) {
        /*
         * Recupera un usuario a partir de un modelo y si ya fue recuperado lanza una excepcion
         * */
        if (!model.esEliminado()) {
            throw new BadRequestException("El usuario con id " + model.getId() + " no se encuentra eliminado.");
        }
        model.recuperar();
        modelRepository.save(model);
    }

    @Override
    public Boolean checkPassword(Usuario model, String password) {
        /*
         * Verifica si la contrase;a enviada coincide con la original
         * Si la contrase;a no coincide lanza una excepcion
         * Si la contrase;a es nula lanza una excepcion
         * */
        if (password == null) {
            throw new BadRequestException("La contrase;a no puede ser nula.");
        }
        if (model.checkPassword(password)) {
            return true;
        } else {
            throw new BadRequestException("La contrase;a es incorrecta.");
        }

    }

    @Override
    public void setPassword(Usuario model, String password) {
        /*
         * Modifica la contrase;a de un usuario
         * si la contrase;a es nula lanza una excepcion
         * Si la contrase;a enviada no coincide con la original lanza una excepcion
         * */
        if (password != null) {
            if (model.checkPassword(password)) {
                model.setPassword(password);
                modelRepository.save(model);
            } else {
                throw new BadRequestException("La contrase;a enviada no coincide con la original.");
            }
        } else {
            throw new BadRequestException("La contrase;a no puede ser nula.");
        }
    }

    @Override
    public boolean verificarEmailFormato(String email) {
        /*
         * Verifica el formato del email
         * Si el formato es incorrecto lanza una excepcion
         * */
        ExpresionesRegulares expReg = new ExpresionesRegulares();
        if (!expReg.verificarEmail(email)) {
            throw new BadRequestException("El formato del email es inválido.");
        }
        return true;
    }

    @Override
    public Usuario verificarAtributos(UsuarioDTO modelDTO) {
        UsuarioMapper mapper = new UsuarioMapper();
        Usuario model = mapper.toEntity(modelDTO);
        return model;
    }

    @Override
    public void verificarNombre(String nombre) {
        ExpresionesRegulares expReg = new ExpresionesRegulares();
        if (!expReg.verificarCaracteres(nombre)){
            throw new BadRequestException("El nombre no puede contener caracteres especiales.");
        }
        nombre = expReg.corregirCadena(nombre);
        if (nombre == ""){
            throw new BadRequestException("El formato del nombre es inválido.");
        }

    }
}
