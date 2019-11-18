package com.github.xuqplus.itext7demo.i5;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePdfTest {

	@Test
	void testCreateAPdf() throws FileNotFoundException, DocumentException {
		final String filename = "target/" + this.getClass().getSimpleName() + "1.pdf";

// step 1: Create a Document
		Document document = new Document();
// step 2: Create a PdfWriter
		PdfWriter writer = PdfWriter.getInstance(
				document, new FileOutputStream(filename));
// step 3: Open the Document
		document.open();
// step 4: Add content
		document.add(new Paragraph("Hello World!"));
// create a signature form field
		PdfFormField field = PdfFormField.createSignature(writer);
		field.setFieldName("fieldName0");
// set the widget properties
		field.setPage();
		field.setWidget(
				new Rectangle(72, 732, 144, 780), PdfAnnotation.HIGHLIGHT_INVERT);
		field.setFlags(PdfAnnotation.FLAGS_PRINT);
// add it as an annotation
		writer.addAnnotation(field);
// maybe you want to define an appearance
		PdfAppearance tp = PdfAppearance.createAppearance(writer, 72, 48);
		tp.setColorStroke(BaseColor.BLUE);
		tp.setColorFill(BaseColor.LIGHT_GRAY);
		tp.rectangle(0.5f, 0.5f, 71.5f, 47.5f);
		tp.fillStroke();
		tp.setColorFill(BaseColor.BLUE);
		ColumnText.showTextAligned(tp, Element.ALIGN_CENTER,
				new Phrase("SIGN HERE"), 36, 24, 25);
		field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);

		addSignatureField(writer);

// step 5: Close the Document
		document.close();
	}

	private void addSignatureField(PdfWriter writer) {
		PdfFormField field = PdfFormField.createSignature(writer);
		field.setFieldName("fieldName1");
		field.setPage(1);
		field.setWidget(new Rectangle(100, 100, 200, 200), PdfAnnotation.HIGHLIGHT_INVERT);
		field.setFieldFlags(PdfAnnotation.FLAGS_PRINT);
		writer.addAnnotation(field);
	}

	@Test
	void addingEmptySignature() throws IOException, DocumentException {
		String src = "a.pdf";
		String dest = "target/addingEmptySignature.pdf";
		String SIGNAME = "SIGNAME0";

		PdfReader reader = new PdfReader(src);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
// create a signature form field
		PdfFormField field = PdfFormField.createSignature(stamper.getWriter());
		field.setFieldName(SIGNAME);
// set the widget properties
		field.setWidget(new Rectangle(72, 732, 144, 780), PdfAnnotation.HIGHLIGHT_OUTLINE);
		field.setFlags(PdfAnnotation.FLAGS_PRINT);
// add the annotation
		stamper.addAnnotation(field, 1);
// close the stamper
		stamper.close();
	}
}
