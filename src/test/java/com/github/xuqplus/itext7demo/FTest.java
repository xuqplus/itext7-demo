package com.github.xuqplus.itext7demo;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA;
import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD;
import static com.itextpdf.kernel.colors.ColorConstants.BLUE;
import static com.itextpdf.kernel.colors.ColorConstants.RED;
import static com.itextpdf.kernel.colors.ColorConstants.WHITE;
import static com.itextpdf.kernel.colors.ColorConstants.YELLOW;
import static com.itextpdf.kernel.colors.DeviceRgb.BLACK;
import static com.itextpdf.kernel.colors.DeviceRgb.GREEN;

@Slf4j
class FTest {

	@Test
	void a() throws IOException {
		PdfFont font = PdfFontFactory.createFont(HELVETICA);
		try (PdfWriter writer = new PdfWriter(this.getClass().getSimpleName() + ".pdf")) {
			try (PdfDocument pdf = new PdfDocument(writer)) {
				PageSize ps = PageSize.A4;
				try (Document document = new Document(pdf, ps)) {
					pdf.addEventHandler(PdfDocumentEvent.END_PAGE, event -> {
						PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
						PdfDocument pdfDoc = docEvent.getDocument();
						PdfPage page = docEvent.getPage();
						int pageNumber = pdfDoc.getPageNumber(page);
						Rectangle pageSize = page.getPageSize();
						PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

						//Set background
						Color limeColor = new DeviceCmyk(0.208f, 0, 0.584f, 0);
						Color blueColor = new DeviceCmyk(0.445f, 0.0546f, 0, 0.0667f);
						pdfCanvas
								.saveState()
								.setFillColor(pageNumber % 2 == 1 ? limeColor : blueColor)
								.rectangle(pageSize.getLeft(), pageSize.getBottom(),
										pageSize.getWidth(), pageSize.getHeight())
								.fill().restoreState();
						//Add header and footer
						pdfCanvas
								.beginText()
								.setFontAndSize(font, 9)
								.moveText(pageSize.getWidth() / 2 - 60, pageSize.getTop() - 20)
								.showText("THE TRUTH IS OUT THERE")
								.moveText(60, -pageSize.getTop() + 30)
								.showText(String.valueOf(pageNumber))
								.endText();
						pdfCanvas
								.beginText()
								.setColor(WHITE, true)
								.setFontAndSize(font, 40F)
								.moveText(pageSize.getWidth() / 2 - 200, pageSize.getHeight() / 2 - 60)
								.showText("123 123 123 123 123 ")
								.endText();
						//Add watermark
						try (Canvas canvas = new Canvas(pdfCanvas, pdfDoc, page.getPageSize())) {
							canvas
									.setFontColor(WHITE)
									.setFont(font)
									.setFontSize(70F)
									.showTextAligned(new Paragraph("ABC ABC ABC ABC "),
											298, 421, pdfDoc.getPageNumber(page),
											TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45)
									.close();
						}
						pdfCanvas.stroke().release();
					});

					PdfFont bold = PdfFontFactory.createFont(HELVETICA_BOLD);
					Table table = new Table(10);
					table.setWidth(UnitValue.createPercentValue(100))
							.setTextAlignment(TextAlignment.CENTER)
							.setHorizontalAlignment(HorizontalAlignment.CENTER);
					try (BufferedReader br = new BufferedReader(new FileReader("f.csv"))) {
						String line = br.readLine();
						process(table, line, bold, true);
						while ((line = br.readLine()) != null) {
							process(table, line, font, false);
						}
					}
					document.add(table);
				}
			}
		}
	}

	private void process(Table table, String line, PdfFont font, boolean isHeader) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		int columnNumber = 0;
		while (tokenizer.hasMoreTokens()) {
			if (isHeader) {
				Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
				cell.setNextRenderer(new RoundedCornersCellRenderer(cell));
				cell.setPadding(5).setBorder(null);
				table.addHeaderCell(cell);
			} else {
				columnNumber++;
				Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
				cell.setFont(font).setBorder(new SolidBorder(BLACK, 0.5f));
				switch (columnNumber) {
					case 4:
						cell.setBackgroundColor(GREEN);
						break;
					case 5:
						cell.setBackgroundColor(YELLOW);
						break;
					case 6:
						cell.setBackgroundColor(RED);
						break;
					default:
						cell.setBackgroundColor(BLUE);
						break;
				}
				table.addCell(cell);
			}
		}
	}

	private class RoundedCornersCellRenderer extends CellRenderer {
		public RoundedCornersCellRenderer(Cell modelElement) {
			super(modelElement);
		}

		@Override
		public void drawBorder(DrawContext drawContext) {
			Rectangle rectangle = getOccupiedAreaBBox();
			float llx = rectangle.getX() + 1;
			float lly = rectangle.getY() + 1;
			float urx = rectangle.getX() + getOccupiedAreaBBox().getWidth() - 1;
			float ury = rectangle.getY() + getOccupiedAreaBBox().getHeight() - 1;
			PdfCanvas canvas = drawContext.getCanvas();
			float r = 4;
			float b = 0.4477f;
			canvas
					.moveTo(llx, lly)
					.lineTo(urx, lly).lineTo(urx, ury - r)
					.curveTo(urx, ury - r * b, urx - r * b, ury, urx - r, ury)
					.lineTo(llx + r, ury)
					.curveTo(llx + r * b, ury, llx, ury - r * b, llx, ury - r)
					.lineTo(llx, lly).stroke();
			super.drawBorder(drawContext);
		}
	}
}