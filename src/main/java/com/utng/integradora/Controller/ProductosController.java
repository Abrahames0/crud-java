package com.utng.integradora.Controller;

import com.utng.integradora.entity.Productos;
import com.utng.integradora.exception.ProductoDuplicadoException;
import com.utng.integradora.model.ProductosDTO;
import com.utng.integradora.service.ProductosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/API/v1/CORREO/productosController")
@Slf4j
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // CONSULTAR
    @PostMapping(value = "consultarProductos")
    public ResponseEntity<?> consultarProductos(@RequestBody ProductosDTO productosDTO) {
        if (productosDTO.getNombre() == null || productosDTO.getNombre().isEmpty()) {
            return new ResponseEntity<>("El nombre del producto no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        List<ProductosDTO> productosDTOList = new ArrayList<>();
        List<Productos> listaTienda = productosService.listaTienda(productosDTO.getNombre());

        for (Productos tiendita : listaTienda) {
            ProductosDTO productito = new ProductosDTO();
            BeanUtils.copyProperties(tiendita, productito);
            productosDTOList.add(productito);
        }

        return new ResponseEntity<>(productosDTOList, HttpStatus.OK);
    }

    // CREAR PRODUCTO
    @PostMapping(value = "crearProductos")
    public ResponseEntity<?> agregarProductos(@RequestParam(value = "nombre") String nombre,
                                              @RequestParam(value = "precio") String precioStr,
                                              @RequestParam(value = "existencia") String existenciaStr) {

        if (nombre == null || nombre.isEmpty()) {
            return new ResponseEntity<>("El nombre del producto no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        if (productosService.existeProductoPorNombre(nombre)) {
            throw new ProductoDuplicadoException("El producto ya existe con el nombre " + nombre);
        }

        if (!isNumeric(precioStr)) {
            return new ResponseEntity<>("El precio debe ser un número válido", HttpStatus.BAD_REQUEST);
        }
        Double precio = Double.valueOf(precioStr);

        if (!isNumeric(existenciaStr)) {
            return new ResponseEntity<>("La existencia debe ser un número válido", HttpStatus.BAD_REQUEST);
        }
        Integer existencia = Integer.valueOf(existenciaStr);

        ProductosDTO productosDTO1 = productosService.crearProducto(nombre, precio, existencia);
        return new ResponseEntity<>(productosDTO1, HttpStatus.OK);
    }

    // ELIMINAR
    @DeleteMapping(value = "eliminarProductos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>("El ID debe ser un número positivo válido", HttpStatus.BAD_REQUEST);
        }

        try {
            productosService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error eliminando el producto: {}", e.getMessage());
            return new ResponseEntity<>("Producto no encontrado con el ID especificado", HttpStatus.NOT_FOUND);
        }
    }

    // ACTUALIZAR POR ID
    @PutMapping(value = "actualizarProducto/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductosDTO productosDTO) {

        log.info("ProductosDTO recibido: {}", productosDTO);

        if (id == null || id <= 0) {
            return new ResponseEntity<>("El ID debe ser un número positivo válido", HttpStatus.BAD_REQUEST);
        }

        if (productosDTO.getNombre() == null || productosDTO.getNombre().isEmpty()) {
            return new ResponseEntity<>("El nombre del producto no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        if (productosDTO.getPrecio() == null || productosDTO.getPrecio() <= 0) {
            return new ResponseEntity<>("El precio debe ser un valor positivo", HttpStatus.BAD_REQUEST);
        }

        if (productosDTO.getExistencia() == null || productosDTO.getExistencia() < 0) {
            return new ResponseEntity<>("La existencia debe ser un número no negativo", HttpStatus.BAD_REQUEST);
        }

        try {
            ProductosDTO updatedProducto = productosService.actualizarProducto(id, productosDTO);
            return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
        } catch (ProductoDuplicadoException e) {
            log.error("Error actualizando el producto: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            log.error("Error actualizando el producto: {}", e.getMessage());
            return new ResponseEntity<>("Producto no encontrado con el ID especificado", HttpStatus.NOT_FOUND);
        }
    }

    // ACTUALIZAR POR NOMBRE
    @PutMapping(value = "actualizarProductoPorNombre/{nombre}")
    public ResponseEntity<?> actualizarProductoPorNombre(
            @PathVariable String nombre,
            @RequestBody ProductosDTO productosDTO) {

        log.info("ProductosDTO recibido para actualización por nombre: {}", productosDTO);

        if (productosDTO.getPrecio() == null || productosDTO.getPrecio() <= 0) {
            return new ResponseEntity<>("El precio debe ser un valor positivo", HttpStatus.BAD_REQUEST);
        }

        if (productosDTO.getExistencia() == null || productosDTO.getExistencia() < 0) {
            return new ResponseEntity<>("La existencia debe ser un número no negativo", HttpStatus.BAD_REQUEST);
        }

        try {
            ProductosDTO updatedProducto = productosService.actualizarProductoPorNombre(nombre, productosDTO);
            return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
        } catch (ProductoDuplicadoException e) {
            log.error("Error actualizando el producto por nombre: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            log.error("Error actualizando el producto por nombre: {}", e.getMessage());
            return new ResponseEntity<>("Producto no encontrado con el nombre especificado", HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<String> handleProductoDuplicadoException(ProductoDuplicadoException ex) {
        log.error("Error de producto duplicado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}