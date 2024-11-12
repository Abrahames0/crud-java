package com.utng.integradora;

public class exception {
    public static class ProductoDuplicadoException extends RuntimeException {
        public ProductoDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }
}
