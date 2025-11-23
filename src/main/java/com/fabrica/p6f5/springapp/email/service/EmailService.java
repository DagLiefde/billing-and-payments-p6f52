package com.fabrica.p6f5.springapp.email.service;

import com.fabrica.p6f5.springapp.invoice.model.Invoice;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Email Service following Single Responsibility Principle.
 * Handles email sending functionality for invoices.
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${email.from}")
    private String fromEmail;
    
    @Value("${email.invoice.subject.prefix:Factura}")
    private String subjectPrefix;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Envía la factura por correo al cliente
     * 
     * @param invoice la factura a enviar
     * @param recipientEmail el correo del destinatario
     * @param pdfPath ruta del archivo PDF a adjuntar (opcional)
     */
    public void sendInvoiceEmail(Invoice invoice, String recipientEmail, String pdfPath) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            logger.warn("No se puede enviar email para factura {}: email del destinatario vacío", invoice.getId());
            throw new IllegalArgumentException("El correo del destinatario es requerido");
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(subjectPrefix + " " + invoice.getInvoiceNumber());
            helper.setText(buildEmailBody(invoice), true); // true = HTML
            
            // Adjuntar PDF si existe
            if (pdfPath != null && !pdfPath.trim().isEmpty()) {
                attachPdfIfExists(helper, pdfPath, invoice.getInvoiceNumber());
            }
            
            mailSender.send(message);
            logger.info("Email enviado exitosamente a {} para factura {}", recipientEmail, invoice.getId());
            
        } catch (MessagingException e) {
            logger.error("Error enviando email para factura {}", invoice.getId(), e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Envía la factura por correo usando el email del cliente de la factura
     * 
     * @param invoice la factura a enviar
     * @param pdfPath ruta del archivo PDF a adjuntar (opcional)
     */
    public void sendInvoiceEmail(Invoice invoice, String pdfPath) {
        if (invoice.getClientEmail() == null || invoice.getClientEmail().trim().isEmpty()) {
            logger.warn("No se puede enviar email para factura {}: email del cliente no configurado", invoice.getId());
            throw new IllegalArgumentException("El correo del cliente no está configurado en la factura");
        }
        sendInvoiceEmail(invoice, invoice.getClientEmail(), pdfPath);
    }
    
    /**
     * Adjunta el PDF si el archivo existe
     */
    private void attachPdfIfExists(MimeMessageHelper helper, String pdfPath, String invoiceNumber) {
        try {
            Path path = Path.of(pdfPath);
            if (Files.exists(path)) {
                File pdfFile = path.toFile();
                helper.addAttachment("factura-" + invoiceNumber + ".pdf", pdfFile);
                logger.debug("PDF adjuntado: {}", pdfPath);
            } else {
                logger.warn("No se pudo adjuntar PDF: archivo no existe en {}", pdfPath);
            }
        } catch (Exception e) {
            logger.warn("Error al adjuntar PDF desde {}: {}", pdfPath, e.getMessage());
        }
    }
    
    /**
     * Construye el cuerpo del email en HTML
     */
    private String buildEmailBody(Invoice invoice) {
        return String.format("""
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Factura Generada</h2>
                    <p>Estimado/a cliente,</p>
                    <p>Se ha generado su factura con los siguientes detalles:</p>
                    <table border="1" cellpadding="10" style="border-collapse: collapse; width: 100%%; margin: 20px 0;">
                        <tr style="background-color: #f8f9fa;">
                            <td style="font-weight: bold;">Número de Factura:</td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td style="font-weight: bold;">Cliente:</td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td style="font-weight: bold;">Fecha:</td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td style="font-weight: bold;">Fecha de Vencimiento:</td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td style="font-weight: bold;">Subtotal:</td>
                            <td>%s %s</td>
                        </tr>
                        <tr>
                            <td style="font-weight: bold;">Impuestos:</td>
                            <td>%s %s</td>
                        </tr>
                        <tr style="background-color: #e8f5e9;">
                            <td style="font-weight: bold; font-size: 1.1em;">Total:</td>
                            <td style="font-weight: bold; font-size: 1.1em;">%s %s</td>
                        </tr>
                    </table>
                    <p>La factura en PDF se encuentra adjunta a este correo.</p>
                    <p style="margin-top: 30px;">Saludos cordiales,<br><strong>Equipo de Facturación</strong></p>
                </div>
            </body>
            </html>
            """,
            invoice.getInvoiceNumber(),
            invoice.getClientName(),
            invoice.getInvoiceDate(),
            invoice.getDueDate(),
            invoice.getSubtotal(),
            invoice.getCurrency(),
            invoice.getTaxAmount() != null ? invoice.getTaxAmount() : "0.00",
            invoice.getCurrency(),
            invoice.getTotalAmount(),
            invoice.getCurrency()
        );
    }
}

