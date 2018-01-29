/**
 * BankServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package main.java.dtu.ws.fastmoney;

public interface BankServiceService extends javax.xml.rpc.Service {
    public java.lang.String getBankServicePortAddress();

    public  BankService getBankServicePort() throws javax.xml.rpc.ServiceException;

    public  BankService getBankServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
