package unitTests;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import core.Barcode;
import org.junit.Test;


public class BarcodeTest {
	
	private Barcode barcode = new Barcode();

	/**
	 * Compares if two byte arrays are exactly identical.
	 * @param a
	 * @param b
	 * @return truth value of wether the two arrays are identical
	 */
	public boolean compareByteArrays(byte[] a, byte[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}	// Test input
	private String uuid = "rDQQb9hKQ0+1h6wmEA2leQ==";
	


	@Test
	public void test() throws IOException {
		
		barcode.generate(uuid, "barcode");
		barcode.generate(uuid, "barcode2");
		
		//ImageIO.write((RenderedImage) barcodeImage, "png", new File("src/barcode.png"));
		
		// Files to read from
		File originalBarcode = new File("src/barcode2.png");
		File newBarcode = new File("src/barcode.png");
		
		// We're comparing the byte arrays for our test below
		byte[] originalBytes = new byte[(int)originalBarcode.length()];
		byte[] newBytes = new byte[(int)newBarcode.length()];
		
		FileInputStream fisOrig = new FileInputStream(originalBarcode);
		FileInputStream fisNew = new FileInputStream(newBarcode);
		
		fisOrig.read(originalBytes);
		fisNew.read(newBytes);
		
		System.out.println(originalBytes.length);
		System.out.println(newBytes.length);
		
		// Check if barcode_original.png is the same as barcode.png.
		assertTrue(compareByteArrays(originalBytes, newBytes));
		
		fisOrig.close();
		fisNew.close();
	}

}
