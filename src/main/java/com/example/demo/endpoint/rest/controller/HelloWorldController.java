package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.event.consumer.SendEmailConfConsumer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class HelloWorldController {

  private final SendEmailConfConsumer sendEmailConfConsumer;

  @GetMapping("/hello")
  public String helloWorld(
      @RequestParam String to, @RequestParam String title, @RequestParam String content) {

    var event = SendEmailRequested.builder().to(to).pdfTitle(title).pdfContent(content).build();

    CompletableFuture.runAsync(
        () -> {
          try {
            sendEmailConfConsumer.accept(event);
          } catch (Exception e) {
            log.error("Error while sending email", e);
          }
        });

    return "... pdf sent successfully";
  }
}
