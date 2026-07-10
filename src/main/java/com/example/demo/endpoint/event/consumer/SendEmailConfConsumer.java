package com.example.demo.endpoint.event.consumer;

import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SendEmailConfConsumer implements Consumer<SendEmailRequested> {

  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(SendEmailRequested event) {
    log.info("Start debug service {}", event.getTo());

    File tempPdfFile = File.createTempFile("document-", ".pdf");

    try (FileOutputStream fos = new FileOutputStream(tempPdfFile)) {
      Document document = new Document();
      PdfWriter.getInstance(document, fos);
      document.open();

      document.add(new Paragraph("Titre : " + event.getPdfTitle()));
      document.add(new Paragraph(" "));
      document.add(new Paragraph("Content :"));
      document.add(new Paragraph(event.getPdfContent()));

      document.close();
      log.debug("PDF generate : {}", tempPdfFile.getAbsolutePath());
    }

    InternetAddress recipient = new InternetAddress(event.getTo());

    Email email =
        new Email(
            recipient,
            Collections.emptyList(),
            Collections.emptyList(),
            "Here is your docs",
            "<html><body><p>Hello,<br> Here is your docs.</p></body></html>",
            List.of(tempPdfFile));

    try {
      mailer.accept(email);
      log.info("E-mail sending :::{}", event.getTo());
    } catch (Exception e) {
      log.error("Error sending email {}", event.getTo(), e);
      throw e;
    } finally {
      if (tempPdfFile.exists() && !tempPdfFile.delete()) {
        log.warn("Impossible to delete temporary files : {}", tempPdfFile.getAbsolutePath());
      }
    }
  }
}
