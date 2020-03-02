package com.github.xuqplus.itext7demo.service;

import com.github.xuqplus.itext7demo.domain.Seal;
import com.github.xuqplus.itext7demo.repository.SealRepository;
import com.github.xuqplus.itext7demo.req.Document;
import com.github.xuqplus.itext7demo.req.SignRequest;
import com.github.xuqplus.itext7demo.req.Stamper;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import net.coobird.thumbnailator.Thumbnails;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SignService {

	final static String dir = "d:/tmp/data/";
	final static String signedDir = "d:/tmp/data/signed/";
	final static String tmpDir = "d:/tmp/data/tmp/";

	static {
		new File(signedDir).mkdirs();
		new File(tmpDir).mkdirs();
	}

	@Autowired
	SealRepository sealRepository;

	// 签名要素
	private String ksFile = "classpath:ks";
	private char[] password = "123123".toCharArray();
	private String contact = "445172495@qq.com";
	private String reason = "-_-.测试签名不需要理由.-_-";
	private String location = "闵行浦江镇";
	private String digestAlgorithm = DigestAlgorithms.SHA256;
	private MakeSignature.CryptoStandard subfilter = MakeSignature.CryptoStandard.CADES;

	public void sign(SignRequest signRequest) throws Exception {
		List<Document> documents = signRequest.getDocuments();
		List<Stamper> stampers = signRequest.getStampers();
		Map<Integer, List<Stamper>> docStamperMap = new HashMap<>();
		for (Stamper stamper : stampers) {
			Integer docIndex = stamper.getDocIndex();
			if (!docStamperMap.containsKey(docIndex)) {
				docStamperMap.put(docIndex, new ArrayList<>());
			}
			docStamperMap.get(docIndex).add(stamper);
		}
		for (int i = 0; i < documents.size(); i++) {
			Document pdfDoc = documents.get(i);
			File document = new File(dir + pdfDoc.getFilename());
			File outputDocument = new File(signedDir + pdfDoc.getFilename() + ".signed.pdf");
			signADocument(document, outputDocument, docStamperMap.get(i));
		}
	}

	public void signADocument(File document, File outputDocument, List<Stamper> stampers) throws Exception {
		if (CollectionUtils.isEmpty(stampers)) {
			return;
		}
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		try (FileInputStream stream = new FileInputStream(ResourceUtils.getFile(ksFile))) {
			ks.load(stream, password);
		}
		String alias = ks.aliases().nextElement();
		PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
		Certificate[] chain = ks.getCertificateChain(alias);
		try (InputStream fis = new FileInputStream(document)) {
			PdfReader reader = new PdfReader(fis);
			for (int i = 0, stampersSize = stampers.size(); i < stampersSize; i++) {
				Stamper stamperDesc = stampers.get(i);
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					// 计算签名矩形
					Rectangle pageSize = reader.getPageSize(stamperDesc.getPageIndex() + 1);
					float height = pageSize.getHeight();
					float ury = height - stamperDesc.getTop();
					float llx = stamperDesc.getLeft();
					float lly = ury - stamperDesc.getHeight();
					float urx = llx + stamperDesc.getWidth();
					Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
					rectangle = handleRotation(rectangle, stamperDesc.getRotateAngle());

					// 可追加签名
					PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
					PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
					appearance.setReason(reason);
					appearance.setLocation(location);
					appearance.setContact(contact);
					appearance.setSignatureCreator(contact);
					appearance.setVisibleSignature(rectangle, stamperDesc.getPageIndex() + 1, "sig-" + System.currentTimeMillis());
					appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC); // 显示图片不显示描述
					appearance.setSignDate(GregorianCalendar.getInstance());
					appearance.setLayer2Text("haha");
					appearance.setLayer4Text("haha");

					Seal seal = sealRepository.getOne(stamperDesc.getSealId());
					String filename = seal.getFilename();
					String tempFile = tmpDir + seal.getFilename();
					Thumbnails.of(new File(dir + filename))
							.forceSize(stamperDesc.getWidth() * 10, stamperDesc.getHeight() * 10)
							.rotate(stamperDesc.getRotateAngle())
							.outputFormat(StringUtils.getFilenameExtension(filename))
							.toFile(tempFile);
					appearance.setSignatureGraphic(Image.getInstance(new File(tempFile).toURL()));

					// 摘要
					ExternalDigest digest = new BouncyCastleDigest();
					// 签名
					ExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, provider.getName());
					MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, subfilter);
					boolean isLast = i >= stampersSize - 1;
					if (isLast) {
						// 写出
						try (FileOutputStream fos = new FileOutputStream(outputDocument)) {
							fos.write(os.toByteArray());
						}
					} else {
						// 重读
						reader = new PdfReader(new ByteArrayInputStream(os.toByteArray()));
					}
				}
			}
		}
	}

	// 旋转之后签名框变大
	private static final float ADJUSTMENT = 0;

	public static Rectangle handleRotation(Rectangle rectangle, float rotationDegrees) {
		if (rotationDegrees % 180 == 0) {
			return rectangle;
		}
		float llx = rectangle.getLeft() + ADJUSTMENT;
		float lly = rectangle.getBottom() + ADJUSTMENT;
		float urx = rectangle.getRight() - ADJUSTMENT;
		float ury = rectangle.getTop() - ADJUSTMENT;

		float width = urx - llx;
		float height = ury - lly;
		float adjustX = getAdjustX(width, height, rotationDegrees);
		float adjustY = getAdjustY(width, height, rotationDegrees);

		return new Rectangle(llx - adjustX - ADJUSTMENT, lly - adjustY - ADJUSTMENT, urx + adjustX + ADJUSTMENT, ury + adjustY + ADJUSTMENT, rectangle.getRotation());
	}

	private static float getAdjustX(float width, float height, float rotationDegrees) {
		double degrees = getDegrees(rotationDegrees);
		double cos = Math.cos(degrees);
		double sin = Math.sin(degrees);
		return (float) ((cos * width + sin * height - width) / 2);
	}

	private static float getAdjustY(float width, float height, float rotationDegrees) {
		double degrees = getDegrees(rotationDegrees);
		double cos = Math.cos(degrees);
		double sin = Math.sin(degrees);
		return (float) (sin * width + cos * height - height) / 2;
	}

	private static double getDegrees(float rotationDegrees) {
		float degrees = rotationDegrees;
		degrees = degrees % 360;
		if (degrees < 0) {
			degrees = degrees + 360;
		}
		if (degrees > 180) {
			degrees = degrees - 180;
		}
		if (degrees > 90) {
			degrees = 180 - degrees;
		}
		return Math.PI / 180 * degrees;
	}
}
