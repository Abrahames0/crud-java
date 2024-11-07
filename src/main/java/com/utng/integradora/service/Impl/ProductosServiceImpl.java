package com.utng.integradora.service.Impl;

import com.utng.integradora.entity.Productos;
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
    public ProductosDTO actualizarProducto(Long id, ProductosDTO productosDTO) {
        Optional<Productos> optionalProducto = productosApiRepository.findById(id);
        if (optionalProducto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con id " + id);
        }

        Productos productoExistente = optionalProducto.get();

        if (productosDTO.getNombre() == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }

        BeanUtils.copyProperties(productosDTO, productoExistente, "id");
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