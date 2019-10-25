package com.github.xuqplus.itext7demo;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static com.itextpdf.forms.xfdf.XfdfConstants.DEST;

class ATest {

	@Test
	void a() throws FileNotFoundException {
		PdfDocument pdf = new PdfDocument(new PdfWriter("a.pdf"));
		Document document = new Document(pdf);
		String line = "Hello! Welcome to iTextPdf";
		document.add(new Paragraph(line));
		document.close();
		System.out.println("Awesome PDF just got created.");
	}
}