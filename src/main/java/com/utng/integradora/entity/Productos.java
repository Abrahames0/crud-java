package com.utng.integradora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "productos")
public class Productos {
    @Id
    @SequenceGenerator(name = "sec_numproductos", sequenceName = "sec_numproductos", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_numproductos")
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "existencia")
    private Integer existencia;
}
