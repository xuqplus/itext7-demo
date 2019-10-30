package com.github.xuqplus.itext7demo;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
class ETest {

	@Test
	void a() throws IOException {
		PdfDocument pdf = new PdfDocument(new PdfWriter(this.getClass().getSimpleName() + ".pdf"));
		PageSize ps = PageSize.A5;
		Document document = new Document(pdf, ps);

		//Set column parameters
		float offSet = 36;
		float columnWidth = (ps.getWidth() - offSet * 2 + 10) / 3;
		float columnHeight = ps.getHeight() - offSet * 2;

		//Define column areas
		Rectangle[] columns = {
				new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
				new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
				new Rectangle(
						offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)};
		document.setRenderer(new ColumnDocumentRenderer(document, columns));

		// adding content
		Image inst = new Image(ImageDataFactory.create("a.png")).setWidth(columnWidth);
		String articleInstagram = new String(
				Files.readAllBytes(Paths.get("HELP.md")), StandardCharsets.UTF_8);

		// The method addArticle is defined in the full  NewYorkTimes sample
		String title = "Instagram May Change Your Feed, Personalizing It With an Algorithm";
		String author = "By MIKE ISAAC MARCH 15, 2016";
		addArticle(document, title, author, inst, articleInstagram);
		addArticle(document, title, author, inst, articleInstagram);
		addArticle(document, title, author, inst, articleInstagram);
		addArticle(document, title, author, inst, articleInstagram);
		addArticle(document, title, author, inst, articleInstagram);
		document.close();
	}

	public static void addArticle(Document doc, String title, String author, Image img, String text) throws IOException {
		Paragraph p1 = new Paragraph(title)
				.setFontSize(14);
		doc.add(p1);
		doc.add(img);
		Paragraph p2 = new Paragraph()
				.setFontSize(7)
				.setFontColor(ColorConstants.GRAY)
				.add(author);
		doc.add(p2);
		Paragraph p3 = new Paragraph()
				.setFontSize(10)
				.add(text);
		doc.add(p3);
	}
}