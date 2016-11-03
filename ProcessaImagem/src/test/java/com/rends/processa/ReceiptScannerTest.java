package com.rends.processa;

import junit.framework.TestCase;

public class ReceiptScannerTest extends TestCase {

	public void testReconhecimento(){
		ReceiptScanner rcpt = new ReceiptScannerImpl();
		rcpt.getTextFromReceiptImage("C:/tcc/workspace_estudo/ProcessaImagem/src/test/resources/images/receipt.jpg");
        assertTrue( true );

	}
	
	public void testReconhecimentoB(){
		ReceiptScanner rcpt = new ReceiptScannerImpl();
		rcpt.getTextFromReceiptImage("C:/tcc/workspace_estudo/ProcessaImagem/src/test/resources/images/receipt-b.jpg");
        assertTrue( true );

	}
}
