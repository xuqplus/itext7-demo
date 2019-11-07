package com.github.xuqplus.itext7demo.i5;

import com.itextpdf.text.DocumentException;
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
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

@Slf4j
class SignTest {

	private String keystore = "ks";
	private char[] password = "123123".toCharArray();

	@Test
	void a() throws IOException, GeneralSecurityException, DocumentException {
		String src = "a.pdf";
		String dest = "target/a.pdf";
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(new FileInputStream(keystore), password);
		String alias = (String) ks.aliases().nextElement();
		PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
		Certificate[] chain = ks.getCertificateChain(alias);
		sign(src, String.format(dest, 1), chain, pk, DigestAlgorithms.SHA256,
				provider.getName(), MakeSignature.CryptoStandard.CMS, "Test 1", "Ghent");
		sign(src, String.format(dest, 2), chain, pk, DigestAlgorithms.SHA512,
				provider.getName(), MakeSignature.CryptoStandard.CMS, "Test 2", "Ghent");
		sign(src, String.format(dest, 3), chain, pk, DigestAlgorithms.SHA256,
				provider.getName(), MakeSignature.CryptoStandard.CADES, "Test 3", "Ghent");
		sign(src, String.format(dest, 4), chain, pk, DigestAlgorithms.RIPEMD160,
				provider.getName(), MakeSignature.CryptoStandard.CADES, "Test 4", "Ghent");
	}

	public void sign(String src,
	                 String dest,
	                 Certificate[] chain,
	                 PrivateKey pk,
	                 String digestAlgorithm,
	                 String provider,
	                 MakeSignature.CryptoStandard subfilter,
	                 String reason,
	                 String location)
			throws GeneralSecurityException, IOException, DocumentException {
		// Creating the reader and the stamper
		PdfReader reader = new PdfReader(src);
		FileOutputStream os = new FileOutputStream(dest);
		PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
		// Creating the appearance
		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
		appearance.setReason(reason);
		appearance.setLocation(location);
		appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig");
		// Creating the signature
		ExternalDigest digest = new BouncyCastleDigest();
		ExternalSignature signature =
				new PrivateKeySignature(pk, digestAlgorithm, provider);
		MakeSignature.signDetached(appearance, digest, signature, chain,
				null, null, null, 0, subfilter);
	}
}