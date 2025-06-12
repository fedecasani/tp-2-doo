/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ubp.edu.ar.ejemplocompletofx.modelo;

import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.ubp.edu.ar.ejemplocompletofx.dao.Dao;
import org.ubp.edu.ar.ejemplocompletofx.dto.ClienteDto;
import org.ubp.edu.ar.ejemplocompletofx.factories.FabricaDao;

/**
 *
 * @author agustin
 */
public class Cliente extends Modelo {

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public ModelMapper getMapper() {
        return mapper;
    }

    public void setMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Dao getDao() {
        return dao;
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    private String nombre;
    private String apellido;
    private String dni;
    private ModelMapper mapper = new ModelMapper();

    public Cliente() {
        this.dao = FabricaDao.fabricar("ClienteDao");
    }

    public List<Cliente> listarTodos() {
        List<ClienteDto> clientesDto = this.dao.listarTodos();
        List<Cliente> clientes = Arrays.asList(this.mapper.map(clientesDto, Cliente[].class));
        return clientes;
    }

    @Override
    public String toString() {
        return nombre.toUpperCase() + " " + apellido.toUpperCase();
    }
}
