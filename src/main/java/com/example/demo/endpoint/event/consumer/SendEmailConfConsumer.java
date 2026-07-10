package com.example.demo.endpoint.event.consumer;

import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.mail.Mailer;
import com.example.demo.mail.Email;
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
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SendEmailConfConsumer implements Consumer<SendEmailRequested> {

    private final Mailer mailer;

    @Override
    @SneakyThrows
    public void accept(SendEmailRequested event) {

        File tempPdfFile = File.createTempFile("document-", ".pdf");

        try (FileOutputStream fos = new FileOutputStream(tempPdfFile)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);

            document.open();
            document.add(new Paragraph("Hello,"));
            document.add(new Paragraph("Here is your document"));
            document.close();
        }

        InternetAddress recipient = new InternetAddress(event.getTo());

        mailer.accept(new Email(
                recipient,
                Collections.emptyList(),
                Collections.emptyList(),
                "Here is your document",
                "<html><body><p>Hello you are subscribe in lv2</p></body></html>",
                List.of(tempPdfFile)
        ));

        // 4. Nettoyage du fichier temporaire
        tempPdfFile.delete();
    }
}