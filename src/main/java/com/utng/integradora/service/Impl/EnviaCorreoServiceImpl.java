package com.utng.integradora.service.Impl;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.utng.integradora.service.EnviaCorreoService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.utng.integradora.email.MailHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnviaCorreoServiceImpl implements EnviaCorreoService {
    @Autowired
    private MailHelper mailHelper;
    private static final String ALERTA_FECHA = "ALERTA_FECHA";
    @Override
    public boolean enviaCorreo(String fromCorreo){
        try(InputStream correoTemplate = new ClassPathResource("templateEmailAlerts/plantilla.html").getInputStream()){
            String template = IOUtils.toString(correoTemplate,StandardCharsets.UTF_8);
            template = template.replace(ALERTA_FECHA, (new Date()).toString());
            template = template.replace("ERRORES_SERVICIO", String.valueOf("prueba"));
            template = template.replace("Mensaje de Error", "prueba");
            mailHelper.send(fromCorreo ,fromCorreo, "Alerta Error en el servicio PASE Empresario", template);
        }catch (IOException e){
            System.out.println("errormdemkibana:{}"+e.getMessage());
            throw new RuntimeException(e);
        }
        return false;
    }
}
