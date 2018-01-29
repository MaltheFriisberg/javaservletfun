/**
 * BankService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package main.java.dtu.ws.fastmoney;

public interface BankService extends java.rmi.Remote {
    public void retireAccount(java.lang.String arg0) throws java.rmi.RemoteException, BankServiceException;
    public void transferMoneyFromTo(java.lang.String arg0, java.lang.String arg1, java.math.BigDecimal arg2, java.lang.String arg3) throws java.rmi.RemoteException, BankServiceException;
    public Account getAccount(java.lang.String arg0) throws java.rmi.RemoteException, BankServiceException;
    public Account getAccountByCprNumber(java.lang.String arg0) throws java.rmi.RemoteException, BankServiceException;
    public AccountInfo[] getAccounts() throws java.rmi.RemoteException;
    public java.lang.String createAccountWithBalance(User arg0, java.math.BigDecimal arg1) throws java.rmi.RemoteException, BankServiceException;
}
