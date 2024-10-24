package com.tpi_pais.mega_store.auth.service;

import com.tpi_pais.mega_store.auth.dto.UsuarioDTO;
import com.tpi_pais.mega_store.auth.model.Usuario;
import com.tpi_pais.mega_store.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    public List<UsuarioDTO> listar();

    public Usuario buscarPorId(Integer id);

    public Usuario buscarPorEmail (String email);

    public UsuarioDTO guardar(UsuarioDTO model);

    public Usuario guardar(Usuario model);

    public void eliminar(Usuario model);

    public void recuperar(Usuario model);

    public Boolean checkPassword (Usuario model, String password);

    public void setPassword(Usuario model, String password);

    public boolean verificarEmailFormato (String email);

    public Usuario buscarEliminadoPorId (Integer id);

    public Usuario buscarEliminadoPorEmail (String email);

    public Usuario verificarAtributos (UsuarioDTO modelDTO);

    public void verificarNombre (String nombre);
}
