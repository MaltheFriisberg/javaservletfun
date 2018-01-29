package unitTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Barcode;
import core.TokenGenerator;
//import dtu.ws.fastmoney.BankServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

//import javax.xml.rpc.ServiceException;


public class UserTest {

    private TokenGenerator tokenGenerator;

    @Before
    public void build(){
        tokenGenerator = new TokenGenerator();
    }

    @After
    public void destroy(){
    }

    /*@Test
    public void testUserBasics() throws BankServiceException, RemoteException, ServiceException {

        User user = new User();
        assertNotNull(user);
        assertNotNull(user.getToken());

        User user2 = new User(tokenGenerator.generateToken().toString());
        assertNotNull(user2);
        assertNotNull(user2.getToken());

        assertTrue(user.getBalance() >= 0.0);
        assertNull(user.getGivenName());
        assertNull(user.getSurname());
        user.setName("John", "Bob");
        assertEquals("JohnBob",user.getGivenName()+user.getSurname());

        Assert.assertNull(user.getCprNumber());
        user.setCprNumber("01011900-1234");
        Assert.assertNotNull(user.getCprNumber());
        Assert.assertEquals("01011900-1234",user.getCprNumber());
        
    }*/

    /*@Test
    public void userGenerateBarcodeTest() throws IOException, ServiceException{
        User user = new User(tokenGenerator.generateToken().toString());
        assertNotNull(user.generateBarcode());



        Barcode barcode = new Barcode();
        barcode.generate(tokenGenerator.generateBase64HexString(user.getToken()), "testbarcode");



        // Files to read from
        File originalBarcode = new File("src/testbarcode.png");
        File newBarcode = new File("src/TmpBarcode.png");

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

    }*/

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
    }
}
