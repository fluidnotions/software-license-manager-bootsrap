package com.groupfio.pgp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class PGPDecrypt {

	private String passphrase;
	private String publicKeyFileName;
	private String secretKeyFileName;
	//private String inputFileName;
	//private String outputFileName;
	
	private InputStream publicKeyIn;
	private InputStream secretKeyIn;
	
	private boolean asciiArmored = false;
	private boolean integrityCheck = true;

	private static final String secKey = "-----BEGIN PGP PRIVATE KEY BLOCK-----"
			+ "\r\n"
			+ "Version: BCPG C# v1.6.1.0"
			+ "\r\n"
			+ "\r\n"
			+ "lQOsBFRg3tsBCAChz6VMHmEpz7n0o9H88TjRgVDNxKbE9V184mfOA7dHPxF2brtR"
			+ "\r\n"
			+ "86Tn0Oyxgi2y3OHG48DtxYOqcE3MJz9zsPXbPf6mo/kLA+WhtBOjNMHlfNWwlno9"
			+ "\r\n"
			+ "78SfjJ5saz5HQ6dyf5Xc1q4U3DDYeBHlzMwnyOGr9XfA3UhUekYnKOpomBndbb9z"
			+ "\r\n"
			+ "ugdwcdTvdCTpORVctyKbZC7NEBBfk/WtPIYR4BFPjSVqQZip8LjLrfHHRfXRV1mn"
			+ "\r\n"
			+ "IgXJDVUITdRXEI4GS0pFUc0Q/Q+0BCEvW4YF1s9zE2JMHVFfkVgLMpW5CNiImshc"
			+ "\r\n"
			+ "XwNRlLwqI602/plDKWiRxmzb5HBM8r0+IUlZABEBAAH/AwMCzls9mmi/mdZgaVum"
			+ "\r\n"
			+ "lq2beN3RrQpy/TXGV0Z7ZUUZF1JDpe0iSBr0t7b9qnu4Cn8Yomb/eB8TWi+CoZzx"
			+ "\r\n"
			+ "Tiopa8H3/aA3tTVCs1T76KTUMH7wL5JDQEP5f6RvdhYGzqsQ4NS903kfxPmZpY4e"
			+ "\r\n"
			+ "dnvxZt6bdq2cUJDz3kXdghxd1JksLe4hh/eutiujFEfE6TU9Nc8TF90U5cAV1sad"
			+ "\r\n"
			+ "HVMtNtBDyLHk+8kWg6xzki6IPL0s3D5FwwUApf+GreTgNpPb0xlEn8a4zfdDhUGA"
			+ "\r\n"
			+ "jVzQrEihSTSiJ5NNDpAfpgwLS2AOp9N5x64gz7FusZPVD64Z28e4fdSTVbN6xxwv"
			+ "\r\n"
			+ "3UyxYpL9lQQxkxIr78U92S201u3yCjZ7fQF7EWKsLw3Gq9MluziWffrdkR8neMQY"
			+ "\r\n"
			+ "a5bV1OTM6jCfp9GjiTMp/KtcXmoRR5CVBHSy/cE7ojk4ycFPP79dttzWxnxT1tDm"
			+ "\r\n"
			+ "SYFjjfjB1ScyQyHgBpAagjYTuiHZAaZd6Mae2vmormPnJkQxWS4KHOVw8+rNghkC"
			+ "\r\n"
			+ "wkf7HonanyfjHOcNi7ah3jESplezM+IPHY3QlSgKtIAn/nlMVy91vx5VPROJXkHG"
			+ "\r\n"
			+ "ZIz/BGvDUD84oF99tRAbVNBkxQ4PNWpq157F+uPJ5BirdaSVtiPFFH/EMPvJ+sPI"
			+ "\r\n"
			+ "W4faqw//9ftKL7E2fsgJOFg0xKmyRw/UhRGdh9El2PhTfR81vy1eWNg+xwfzd+MX"
			+ "\r\n"
			+ "3/9GziNxGNQYSRQpAQPg+EGtb/e4YABAfNBueKWZT8UufLHIUkiGNEtWq0hn2AEs"
			+ "\r\n"
			+ "SXDVoqF6CZnliID6nn69NFK90j3/Ksh5MaFZZ9z7AM8G71mRADXX6rqm1enCpuY6"
			+ "\r\n"
			+ "T/DPwGJ6fnQRCNYg8bRijiz89QOpc/i9wY4sYdATXbQQdGVzdEBleGFtcGxlLmNv"
			+ "\r\n"
			+ "bYkBHAQQAQIABgUCVGDe2wAKCRC1WoynhGe4GHtDB/4spqso+CR8sCeH67xdKC6W"
			+ "\r\n"
			+ "dn248XNl7NNbekxTsdAXqlM11x3xRi7YdrxMKqmqh9qr5A/6XFCUbIJOLxXap6J/"
			+ "\r\n"
			+ "U2WPzxm8qH4h5tKmKzin017JIHiOyOyqrn3r01c+7Lk45ZhHiSCfy0S2IQ4NOevj"
			+ "\r\n"
			+ "yPJxLY1yqh1ySe3Vx1o5OeCIzYHi2oCgbc6kDSek5hP4hSCtDe2ap3oGvROaCJQD"
			+ "\r\n"
			+ "umWeHbumTe8rWRLLiUgccEni5BYSG6AZjuHBgVO3x82tY0Has7Mpzx3iKXtKFqqC"
			+ "\r\n"
			+ "lrmaBG9MtA3q3kJlMrBM16q7BsH0gsV/qS4sPqM6eHNBk2XiZPjnrKmegrPZO9ht"
			+ "\r\n" + "=nlv+" + "\r\n" + "-----END PGP PRIVATE KEY BLOCK-----";
	
	
	public PGPDecrypt() {
		super();
		InputStream secretKeyIn = new ByteArrayInputStream(secKey.getBytes());
		setSecretKeyIn(secretKeyIn);
	}

	public byte[] decrypt(byte[] string) throws Exception {
		InputStream keyIn;
		if(secretKeyFileName!=null){
			keyIn = new FileInputStream(secretKeyFileName);
		}else{
			keyIn = this.secretKeyIn;
		}
	
		byte[] decrypted = PGPUtils.decryptPayload(string, keyIn, passphrase.toCharArray());
		keyIn.close();
		return decrypted;
	}

	public boolean isAsciiArmored() {
		return asciiArmored;
	}

	public void setAsciiArmored(boolean asciiArmored) {
		this.asciiArmored = asciiArmored;
	}

	public boolean isIntegrityCheck() {
		return integrityCheck;
	}

	public void setIntegrityCheck(boolean integrityCheck) {
		this.integrityCheck = integrityCheck;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPublicKeyFileName() {
		return publicKeyFileName;
	}

	public void setPublicKeyFileName(String publicKeyFileName) {
		this.publicKeyFileName = publicKeyFileName;
	}

	public String getSecretKeyFileName() {
		return secretKeyFileName;
	}

	public void setSecretKeyFileName(String secretKeyFileName) {
		this.secretKeyFileName = secretKeyFileName;
	}

	public InputStream getPublicKeyIn() {
		return publicKeyIn;
	}

	public void setPublicKeyIn(InputStream publicKeyIn) {
		this.publicKeyIn = publicKeyIn;
	}

	public InputStream getSecretKeyIn() {
		return secretKeyIn;
	}

	public void setSecretKeyIn(InputStream secretKeyIn) {
		this.secretKeyIn = secretKeyIn;
	}

	
	/*public boolean signEncrypt() throws Exception {
		FileOutputStream out = new FileOutputStream(outputFileName);
		FileInputStream publicKeyIn = new FileInputStream(publicKeyFileName);
		FileInputStream secretKeyIn = new FileInputStream(secretKeyFileName);

		PGPPublicKey publicKey = PGPUtils.readPublicKey(publicKeyIn);
		PGPSecretKey secretKey = PGPUtils.readSecretKey(secretKeyIn);

		PGPUtils.signEncryptFile(out, this.getInputFileName(), publicKey,
				secretKey, this.getPassphrase(), this.isAsciiArmored(),
				this.isIntegrityCheck());

		out.close();
		publicKeyIn.close();
		secretKeyIn.close();

		return true;
	}*/

}
