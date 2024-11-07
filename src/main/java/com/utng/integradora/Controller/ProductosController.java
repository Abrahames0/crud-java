package com.utng.integradora.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utng.integradora.entity.Productos;
import com.utng.integradora.model.ProductosDTO;
import com.utng.integradora.service.ProductosService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/API/v1/CORREO/productosController")
@Slf4j
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    // CONSULTAR
    @PostMapping(value = "consultarProductos")
    public ResponseEntity<List<ProductosDTO>> consultarProductos(@RequestBody ProductosDTO productosDTO) {
        List<ProductosDTO> productosDTO1 = new ArrayList<>();
        List<Productos> listaTienda = productosService.listaTienda(productosDTO.getNombre());

        for (Productos tiendita : listaTienda) {
            ProductosDTO productito = new ProductosDTO();
            BeanUtils.copyProperties(tiendita, productito);
            productosDTO1.add(productito);
        }

        return new ResponseEntity<>(productosDTO1, HttpStatus.OK);
    }

    // CREAR O AGREGAR
    @PostMapping(value = "crearProductos")
    public ResponseEntity<ProductosDTO> agregarProductos(@RequestParam(value = "nombre") String nombre,
                                                         @RequestParam Double precio,
                                                         @RequestParam Integer existencia) {
        ProductosDTO productosDTO1 = productosService.crearProducto(nombre, precio, existencia);
        return new ResponseEntity<>(productosDTO1, HttpStatus.OK);
    }

    // ELIMINAR
    @DeleteMapping(value = "eliminarProductos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productosService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error eliminando el producto: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ACTUALIZAR
    @PutMapping(value = "actualizarProducto/{id}")
    public ResponseEntity<ProductosDTO> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductosDTO productosDTO) {

        log.info("ProductosDTO recibido: {}", productosDTO);

        try {
            ProductosDTO updatedProducto = productosService.actualizarProducto(id, productosDTO);
            return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error actualizando el producto: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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