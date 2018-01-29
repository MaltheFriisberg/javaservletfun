package unitTests;

import static org.junit.Assert.*;

import java.util.UUID;

import core.TokenGenerator;
import org.junit.Test;

public class Token128BitTest {

	@Test
	public void testTokenDataType() {
		TokenGenerator tokenGenerator = new TokenGenerator();
		UUID token = tokenGenerator.generateToken();
		String tokenDataType = ((Object)token.getMostSignificantBits()).getClass().getName();
		assertEquals("java.lang.Long", tokenDataType);
		tokenDataType = ((Object)token.getLeastSignificantBits()).getClass().getName();
		assertEquals("java.lang.Long", tokenDataType);
	}
	
	@Test
	public void testTokenMaxNr() {
		double maxNr = Math.pow(2, 63)-1;
		double minNr = -Math.pow(2, 63)-1;
		
		TokenGenerator tokenGenerator = new TokenGenerator();
		for(int i = 0; i < 10000; i++) {
			UUID token = tokenGenerator.generateToken();
			assertTrue(token.getLeastSignificantBits() < maxNr && token.getLeastSignificantBits() > minNr);
			assertTrue(token.getMostSignificantBits() < maxNr && token.getMostSignificantBits() > minNr);
		}
	}
	
	@Test
	public void testTokenUniqueness() {
		TokenGenerator tokenGenerator = new TokenGenerator();
	
		for(int i = 0; i < 10000; i++) {
			UUID token = tokenGenerator.generateToken();
			UUID token2 = tokenGenerator.generateToken();
			assertFalse(token.equals(token2));
		}
	}
	
	@Test
	public void testHexStringUniqueness() {
		TokenGenerator tokenGenerator = new TokenGenerator();
		
		for(int i = 0; i < 10000; i++) {
			String base64HexString1 = tokenGenerator.generateBase64HexString();
			String base64HexString2 = tokenGenerator.generateBase64HexString();
			assertFalse(base64HexString1.equals(base64HexString2));
		}
	}

}
