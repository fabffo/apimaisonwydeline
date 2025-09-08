// src/main/java/com/wydeline/service/InvoiceService.java
package com.wydeline.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;      // ✅ le bon Path
import java.nio.file.Paths;    // ✅ pour Paths.get(...)
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value; // ✅ @Value Spring
import org.springframework.stereotype.Service;

import com.lowagie.text.Paragraph;
import com.wydeline.controller.StripeWebhookController;
import com.wydeline.model.Commande;
import com.wydeline.repository.CommandeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactureService {

  @Value("${app.invoices.dir}") 
  private String invoicesDir;  // ✅ injecté par Spring

  private final CommandeRepository commandeRepository;

//InvoiceService.java – dans renderInvoicePdf(Commande cmd)
public byte[] renderInvoicePdf(Commande cmd) {
 try (var baos = new ByteArrayOutputStream()) {
   var doc = new com.lowagie.text.Document();
   com.lowagie.text.pdf.PdfWriter.getInstance(doc, baos);
   doc.open();

   // En-tête
   doc.add(new com.lowagie.text.Paragraph("Maison Wydeline - Facture #" + cmd.getId()));
   doc.add(new com.lowagie.text.Paragraph("Client (email) : " +
       (cmd.getEmailClient() != null ? cmd.getEmailClient() : "-")));
   doc.add(new com.lowagie.text.Paragraph("Date : " + java.time.LocalDate.now()));
   doc.add(new com.lowagie.text.Paragraph(" "));

   // === Adresse de livraison ===
   var adr = cmd.getLivraison();
   if (adr != null) {
     var title = new com.lowagie.text.Paragraph("Adresse de livraison");
     title.getFont().setStyle(com.lowagie.text.Font.BOLD);
     doc.add(title);

     // lignes d'adresse (on n’affiche que les champs non vides)
     java.util.function.Function<String,String> nz = s -> (s == null || s.isBlank()) ? null : s;

     String[] lignesAdr = new String[] {
       // Nom complet + (Entreprise)
       java.util.stream.Stream.of(nz.apply(adr.getPrenom()), nz.apply(adr.getNom()))
         .filter(java.util.Objects::nonNull).reduce((a,b) -> a + " " + b).orElse(null),
       (nz.apply(adr.getEntreprise()) != null ? "Entreprise : " + adr.getEntreprise() : null),
       nz.apply(adr.getAdresse()),
       nz.apply(adr.getComplement()),
       // Code postal + Ville
       java.util.stream.Stream.of(nz.apply(adr.getCodePostal()), nz.apply(adr.getVille()))
         .filter(java.util.Objects::nonNull).reduce((a,b) -> a + " " + b).orElse(null),
       nz.apply(adr.getPays()),
       (nz.apply(adr.getTelephone()) != null ? "Tél. : " + adr.getTelephone() : null)
     };

     for (String l : lignesAdr) {
       if (l != null) doc.add(new com.lowagie.text.Paragraph(l));
     }
     doc.add(new com.lowagie.text.Paragraph(" "));
   }

   // === Détails commande ===
   var table = new com.lowagie.text.pdf.PdfPTable(4);
   table.addCell("Produit");
   table.addCell("Variante");
   table.addCell("Qté");
   table.addCell("PU (€)");

   for (var lc : cmd.getLignes()) {
     table.addCell(lc.getProduit().getNom());
     table.addCell(
       ((lc.getTaille()!=null) ? "T" + lc.getTaille() : "") +
       ((lc.getCouleur()!=null) ? " " + lc.getCouleur() : "")
     );
     table.addCell(String.valueOf(lc.getQuantite()));
     table.addCell(lc.getPrixUnitaire().toString());
   }
   doc.add(table);

   doc.add(new com.lowagie.text.Paragraph(" "));
   doc.add(new com.lowagie.text.Paragraph("Total: " + cmd.getMontantTotal() + " €"));

   doc.close();
   return baos.toByteArray();
 } catch (Exception e) {
   throw new RuntimeException("Erreur génération facture", e);
 }
}

  public Path store(Long orderId, byte[] pdf) {
	  try {
	    var dir = Path.of(invoicesDir);
	    log.info("InvoiceService: création du dossier si besoin: {}", dir.toAbsolutePath());
	    Files.createDirectories(dir);

	    Path p = dir.resolve("invoice-" + orderId + ".pdf");
	    Files.write(p, pdf);
	    log.info("InvoiceService: PDF écrit à {}", p.toAbsolutePath());
	    return p;
	  } catch (IOException e) {
	    log.error("InvoiceService: erreur écriture PDF pour commande {}", orderId, e);
	    throw new RuntimeException(e);
	  }
	}

}
