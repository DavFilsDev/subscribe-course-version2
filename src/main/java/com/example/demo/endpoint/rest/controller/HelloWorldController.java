package com.example.demo.endpoint.rest.controller;

import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@AllArgsConstructor
public class HelloWorldController {

    private final Mailer mailer;

    @GetMapping("/hello")
    public String helloWorld(
            @RequestParam String to,
            @RequestParam String title,
            @RequestParam String content) {

        CompletableFuture.runAsync(() -> {
            try {
                InternetAddress recipient = new InternetAddress(to);
                Email email = new Email(
                        recipient,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        "Sujet : " + title,
                        "<html><body><h1>" + title + "</h1><p>" + content + "</p></body></html>",
                        Collections.emptyList()
                );
                mailer.accept(email);
                log.info("Email envoyé à {}", to);
            } catch (Exception e) {
                log.error("Échec de l'envoi à {}", to, e);
            }
        });

        return "... email sent successfully (without PDF)";
    }
}