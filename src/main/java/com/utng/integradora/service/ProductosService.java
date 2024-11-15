package com.utng.integradora.service;

import com.utng.integradora.entity.Productos;
import com.utng.integradora.model.ProductosDTO;

import java.util.List;

public interface ProductosService {
    List<Productos> listaTienda(String nombre);
    List<Productos> listaTodaTienda();
    ProductosDTO crearProducto(String nombre, Double precio, Integer existencia);
    ProductosDTO actualizarProducto(Long id, ProductosDTO productosDTO);
    ProductosDTO actualizarProductoPorNombre(String nombre, ProductosDTO productosDTO);
    void eliminarProducto(Long id);
    boolean existeProductoPorNombre(String nombre);
}

