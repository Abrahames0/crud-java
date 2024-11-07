package com.utng.integradora.repository;

import com.utng.integradora.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long> {

    }
