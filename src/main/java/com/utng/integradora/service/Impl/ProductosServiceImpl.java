package com.utng.integradora.service.Impl;

import com.utng.integradora.entity.Productos;
import com.utng.integradora.exception;
import com.utng.integradora.model.ProductosDTO;
import com.utng.integradora.repository.ProductosRepository;
import com.utng.integradora.service.ProductosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductosServiceImpl implements ProductosService {

    @Autowired(required = true)
    private ProductosRepository productosApiRepository;

    @Override
    public List<Productos> listaTienda(String nombre) {
        return productosApiRepository.findAll();
    }

    @Override
    public List<Productos> listaTodaTienda() {
        return productosApiRepository.findAll();
    }

    @Override
    public ProductosDTO crearProducto(String nombre, Double precio, Integer existencia) {
        try {
            ProductosDTO productosConsultaDTO1 = new ProductosDTO();
            Productos productos = new Productos();
            productos.setNombre(nombre);
            productos.setPrecio(precio);
            productos.setExistencia(existencia);
            productosApiRepository.save(productos);
            BeanUtils.copyProperties(productos, productosConsultaDTO1);
            return productosConsultaDTO1;
        } catch (Exception e) {
            log.error("Ha ocurrido un error al guardar la información, a causa de: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existeProductoPorNombre(String nombre) {
        return productosApiRepository.findByNombre(nombre).isPresent();
    }

    @Override
    public ProductosDTO actualizarProducto(Long id, ProductosDTO productosDTO) {
        Optional<Productos> optionalProducto = productosApiRepository.findById(id);
        if (optionalProducto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con id " + id);
        }

        Productos productoExistente = optionalProducto.get();

        if (productosDTO.getNombre() != null && !productosDTO.getNombre().equals(productoExistente.getNombre())) {
            Optional<Productos> productoConMismoNombre = productosApiRepository.findByNombre(productosDTO.getNombre());
            if (productoConMismoNombre.isPresent()) {
                throw new exception.ProductoDuplicadoException("Ya existe un producto con el nombre " + productosDTO.getNombre());
            }
        }

        BeanUtils.copyProperties(productosDTO, productoExistente, "id");
        Productos productoActualizado = productosApiRepository.save(productoExistente);

        ProductosDTO updatedDTO = new ProductosDTO();
        BeanUtils.copyProperties(productoActualizado, updatedDTO);

        return updatedDTO;
    }

    @Override
    public ProductosDTO actualizarProductoPorNombre(String nombre, ProductosDTO productosDTO) {
        Optional<Productos> optionalProducto = productosApiRepository.findByNombre(nombre);
        if (optionalProducto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con nombre " + nombre);
        }

        Productos productoExistente = optionalProducto.get();

        // Verificación de nombre duplicado
        if (productosDTO.getNombre() != null && !productosDTO.getNombre().equals(nombre)) {
            Optional<Productos> productoConMismoNombre = productosApiRepository.findByNombre(productosDTO.getNombre());
            if (productoConMismoNombre.isPresent()) {
                throw new RuntimeException("Ya existe un producto con el nombre " + productosDTO.getNombre());
            }
        }

        BeanUtils.copyProperties(productosDTO, productoExistente, "id", "nombre");
        Productos productoActualizado = productosApiRepository.save(productoExistente);

        ProductosDTO updatedDTO = new ProductosDTO();
        BeanUtils.copyProperties(productoActualizado, updatedDTO);

        return updatedDTO;
    }

    @Override
    public void eliminarProducto(Long id) {
        try {
            if (!productosApiRepository.existsById(id)) {
                throw new RuntimeException("Producto no encontrado");
            }
            productosApiRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ha ocurrido un error al eliminar el producto, a causa de: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar el producto");
        }
    }
}
