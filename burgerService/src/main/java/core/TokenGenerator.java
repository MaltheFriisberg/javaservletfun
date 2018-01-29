package core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class TokenGenerator {

	public UUID generateToken() {
		UUID uuid = UUID.randomUUID();
		return uuid;
	}
	
	public String generateBase64HexString() {
		return generateBase64HexString(generateToken().toString());
	}

	public String generateBase64HexString(String token) {
        UUID uuid = UUID.fromString(token);

		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES*2);

		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());

		byte[] encodedBytes = Base64.getEncoder().encode(buffer.array());
		String str = new String(encodedBytes, StandardCharsets.UTF_8);

		return str;
	}

}
