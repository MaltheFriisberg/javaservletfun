/**
 * 
 */
package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * @author User
 *
 */
public class Barcode {
	
	/**
	 * Generates a barcode image from a UUID.
	 * @param uuid
	 * @return barcode file stream
	 * @throws IOException 
	 */
	public FileOutputStream generate(String uuid, String fileName) throws IOException {
		Code128Bean bean = new Code128Bean();
	      final int dpi = 160;

	      //Configure the barcode generator
	      bean.setModuleWidth(UnitConv.in2mm(2.8f / dpi));

	      bean.doQuietZone(false);

	      //Open output file
	      File outputFile = new File("src/"+ fileName + ".png");

	      FileOutputStream out = new FileOutputStream(outputFile);
	    
	      BitmapCanvasProvider canvas = new BitmapCanvasProvider(
	          out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

	      //Generate the barcode
	      bean.generateBarcode(canvas, uuid);
	   
	      //Signal end of generation
	      canvas.finish();
		return out;
	}
}
