package com.github.xuqplus.itext7demo;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class CTest {

	@Test
	void a() throws IOException {
		try (PdfWriter pdfWriter = new PdfWriter("c.pdf")) {
			try (PdfDocument pdfDocument = new PdfDocument(pdfWriter)) {
				try (Document document = new Document(pdfDocument)) {
					document.setProperty(0, "hello");
					log.info("{}", document.getTopMargin());
					log.info("{}", document.getRightMargin());
					log.info("{}", document.getBottomMargin());
					log.info("{}", document.getLeftMargin());

					PageSize ps = PageSize.A4.rotate();
					PdfPage page = pdfDocument.addNewPage(ps);
					PdfCanvas canvas = new PdfCanvas(page);
					canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);

					//Draw X axis
					canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
							.lineTo(ps.getWidth() / 2 - 15, 0)
							.stroke();
					//Draw X axis arrow
					canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
							.moveTo(ps.getWidth() / 2 - 25, -10)
							.lineTo(ps.getWidth() / 2 - 15, 0)
							.lineTo(ps.getWidth() / 2 - 25, 10).stroke()
							.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
					//Draw Y axis
					canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
							.lineTo(0, ps.getHeight() / 2 - 15)
							.stroke();
					//Draw Y axis arrow
					canvas.saveState()
							.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
							.moveTo(-10, ps.getHeight() / 2 - 25)
							.lineTo(0, ps.getHeight() / 2 - 15)
							.lineTo(10, ps.getHeight() / 2 - 25).stroke()
							.restoreState();
					//Draw X serif
					for (int i = -((int) ps.getWidth() / 2 - 61);
					     i < ((int) ps.getWidth() / 2 - 60); i += 40) {
						canvas.moveTo(i, 5).lineTo(i, -5);
					}
					//Draw Y serif
					for (int j = -((int) ps.getHeight() / 2 - 57);
					     j < ((int) ps.getHeight() / 2 - 56); j += 40) {
						canvas.moveTo(5, j).lineTo(-5, j);
					}
					canvas.stroke();
				}
			}
		}
	}
}