package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloWorldController {
  private final EventProducer<SendEmailRequested> eventProducer;

  @GetMapping("/hello")
  @SneakyThrows
  public String helloWorld(@RequestParam String to,
  @RequestParam String title, @RequestParam String content) {
    var event = SendEmailRequested.builder()
            .to(to)
            .pdfTitle(title)
            .pdfContent(content)
            .build();
    eventProducer.accept(List.of(event));
    return "... pdf sent successfully";
  }
}
