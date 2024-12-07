package com.tp_integrador_p2.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class emailService {

    private static final String EMAIL = "medicarutnfrgp@gmail.com";
    private static final String PASSWORD = "lwrd wwjr gycb lzrl";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void enviarCorreo(String destinatario, String asunto, String mensaje, Callback callback) {
        executorService.execute(() -> {
            try {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL, PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject(asunto);
                message.setText(mensaje);

                Transport.send(message);

                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (MessagingException e) {
                e.printStackTrace();

                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public interface Callback {
        void onSuccess();
        void onError(String error);
        void onResult(Object o);
    }
}
