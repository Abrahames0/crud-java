package com.utng.integradora.email;

import java.io.File;

public interface MailHelper {
    boolean send(String to, String from, String subject, String text, String cco, File... files );

    boolean send (String to, String from, String subject, String text, String cco);

    boolean send (String to, String from, String subject, String text);
}
