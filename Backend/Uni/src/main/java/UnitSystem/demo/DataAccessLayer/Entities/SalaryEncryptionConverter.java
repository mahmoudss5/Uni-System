package UnitSystem.demo.DataAccessLayer.Entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
public class SalaryEncryptionConverter implements AttributeConverter<BigDecimal, String> {

    // 16 bytes (128-bit) key
    private static final String ENCRYPTION_KEY = "MySuperSecretKey";
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

    // GCM recommends 12 bytes for IV
    private static final int IV_LENGTH_BYTE = 12;
    private static final int TAG_LENGTH_BIT = 128;

    @Override
    public String convertToDatabaseColumn(BigDecimal attribute) {

        if (attribute == null) {
            return null;
        }
        try {
            // 1. Generate a random IV for EVERY single encryption
            byte[] iv = new byte[IV_LENGTH_BYTE];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            // 2. Encrypt the salary
            byte[] encryptedBytes = cipher.doFinal(attribute.toString().getBytes());

            // 3. Combine IV + Encrypted Bytes into one single array
            // We need to save the IV with the encrypted data so we can use it for decryption later
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while encrypting salary", e);
        }
    }

    @Override
    public BigDecimal convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // 1. Decode the Base64 string
            byte[] decodedData = Base64.getDecoder().decode(dbData);

            // 2. Extract the IV from the first 12 bytes
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            // 3. Extract the actual encrypted salary bytes
            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // 4. Decrypt and convert back to BigDecimal
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new BigDecimal(new String(decryptedBytes));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while decrypting salary", e);
        }
    }
}