package br.edu.utfpr.pb.pw44s.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendStatusChangeEmail(String toEmail, Long orderId, String newStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nao-responda@seuecommerce.com");
            message.setTo(toEmail);
            message.setSubject("Atualização do Pedido #" + orderId);

            String body = "Olá!\n\n" +
                    "O status do seu pedido #" + orderId + " mudou.\n" +
                    "Novo Status: " + newStatus.replace("_", " ") + "\n\n" +
                    "Acesse o site para ver mais detalhes.";

            message.setText(body);

            mailSender.send(message);
            System.out.println("E-mail enviado com sucesso para: " + toEmail);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());
        }
    }
}