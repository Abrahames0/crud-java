package com.utng.integradora.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import com.utng.integradora.model.CorreoRespuestaDTO;
import com.utng.integradora.service.EnviaCorreoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping(value = "/API/v1/CORREO/CorreoController")
@Slf4j
public class CorreoController {
    @Autowired
    private EnviaCorreoService enviaCorreoService;
    @PostMapping(value="enviarCorreo")
    public ResponseEntity<CorreoRespuestaDTO> correoEnviado(@RequestBody CorreoRespuestaDTO correoRespuestaDTO){
        CorreoRespuestaDTO correoRespuestaDTO1 = null;

        boolean isCorreoEnviado = enviaCorreoService.enviaCorreo(correoRespuestaDTO.getCorreoEmisor());

        if(isCorreoEnviado){
            correoRespuestaDTO1.setCorreoEmisor("Esta es una prueba de correo");
            return new ResponseEntity<>(correoRespuestaDTO1, HttpStatus.OK);
        }
        return new ResponseEntity<>(correoRespuestaDTO1,HttpStatus.OK);
    }
}
