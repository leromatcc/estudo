package com.rends.processa.ProcessaImagem;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ReceiptScannerTest extends TestCase {


    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ReceiptScannerTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

	public void testReconhecimento(){
		ReceiptScanner rcpt = new ReceiptScannerImpl();
		rcpt.getTextFromReceiptImage("C:/tcc/workspace_estudo/ProcessaImagem/src/test/resources/images/receipt.jpg");
        assertTrue( true );

	}
}
