package com.github.xuqplus.itext7demo;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
class BTest {

	@Test
	void a() throws IOException {
		PdfReader pdfReader = new PdfReader("a.pdf");
		PdfDocument pdfDocument = new PdfDocument(pdfReader);
		Document document = new Document(pdfDocument);

		document.setProperty(0, "hello");

		log.info("{}", document.getTopMargin());
		log.info("{}", document.getRightMargin());
		log.info("{}", document.getBottomMargin());
		log.info("{}", document.getLeftMargin());

	}

	@Test
	void b() throws IOException {

		Document pdf = new Document(new PdfDocument(new PdfWriter("b.pdf")));
//		ImageData imageData = new PngImageData(UrlUtil.toURL("a.png")) {
//		};
//		Image image = new Image(imageData);
//		image.setAutoScale(true);
//		image.setAutoScaleHeight(true);
//		image.setAutoScaleWidth(true);
//		image.setMarginLeft(100F);
//		image.setMarginBottom(100F);
//		pdf.add(image);

		Paragraph paragraph = new Paragraph("text");
		byte[] bytes = Files.readAllBytes(new File("b.jpg").toPath());
		paragraph.add("hello, 世界.")
				.add(new Image(ImageDataFactory.create("b.jpg")))
				.add("hello, 世界.")
				.add(new Image(ImageDataFactory.create(bytes)))
				.add("hello, 世界.")
				.add(new Image(ImageDataFactory.createRawImage(bytes))); // 无效
		pdf.add(paragraph);

		pdf.close();
	}
}