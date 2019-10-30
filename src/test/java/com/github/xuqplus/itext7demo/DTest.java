package com.github.xuqplus.itext7demo;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class DTest {

	@Test
	void a() throws IOException {
		try (PdfWriter pdfWriter = new PdfWriter(this.getClass().getSimpleName() + ".pdf")) {
			try (PdfDocument pdfDocument = new PdfDocument(pdfWriter)) {
				try (Document document = new Document(pdfDocument)) {
					PageSize ps = PageSize.A4;
					PdfPage pdfPage = pdfDocument.addNewPage(ps);
					PdfCanvas canvas = new PdfCanvas(pdfPage);

					canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
							.setColor(Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB)), true);


					List<String> text = new ArrayList<>();
					text.add("123123");
					text.add("123123123123123123");
					text.add("123123123123123123");
					text.add("123123123123123123123123123123123123");
					text.add("123123123123123123123123123123123123");
					text.add("123123123123123123123123123123123123");
					text.add("123123123123123123123123123123123123");
					text.add("123123123123123123123123123123123123");
					text.add("123123123123123123123123123123123123");

					canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
					Color yellowColor = new DeviceCmyk(0.f, 0.0537f, 0.769f, 0.051f);
					float lineHeight = 5;
					float yOffset = -40;
					canvas.beginText()
							.setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 1)
							.setColor(yellowColor, true);
					for (int j = 0; j < text.size(); j++) {
						String line = text.get(j);
						float xOffset = ps.getWidth() / 2 - 45 - 8 * j;
						float fontSizeCoeff = 6 + j;
						float lineSpacing = (lineHeight + j) * j / 1.5f;
						int stringWidth = line.length();
						for (int i = 0; i < stringWidth; i++) {
							float angle = (10 / 2 - i) / 2f;
							float charXOffset = (4 + (float) j / 2) * i;
							canvas.setTextMatrix(fontSizeCoeff, 0,
									angle, fontSizeCoeff / 1.5f,
									xOffset + charXOffset, yOffset - lineSpacing)
									.showText(String.valueOf(line.charAt(i)));
						}
					}
					canvas.endText();
				}
			}
		}
	}
}