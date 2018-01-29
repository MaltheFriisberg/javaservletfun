package dtu.ws.fastmoney;

public class BankServiceProxy implements dtu.ws.fastmoney.BankService {
  private String _endpoint = null;
  private dtu.ws.fastmoney.BankService bankService = null;
  
  public BankServiceProxy() {
    _initBankServiceProxy();
  }
  
  public BankServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initBankServiceProxy();
  }
  
  private void _initBankServiceProxy() {
    try {
      bankService = (new dtu.ws.fastmoney.BankServiceServiceLocator()).getBankServicePort();
      if (bankService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)bankService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)bankService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (bankService != null)
      ((javax.xml.rpc.Stub)bankService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public dtu.ws.fastmoney.BankService getBankService() {
    if (bankService == null)
      _initBankServiceProxy();
    return bankService;
  }
  
  public void retireAccount(java.lang.String arg0) throws java.rmi.RemoteException, dtu.ws.fastmoney.BankServiceException{
    if (bankService == null)
      _initBankServiceProxy();
    bankService.retireAccount(arg0);
  }
  
  public void transferMoneyFromTo(java.lang.String arg0, java.lang.String arg1, java.math.BigDecimal arg2, java.lang.String arg3) throws java.rmi.RemoteException, dtu.ws.fastmoney.BankServiceException{
    if (bankService == null)
      _initBankServiceProxy();
    bankService.transferMoneyFromTo(arg0, arg1, arg2, arg3);
  }
  
  public dtu.ws.fastmoney.Account getAccount(java.lang.String arg0) throws java.rmi.RemoteException, dtu.ws.fastmoney.BankServiceException{
    if (bankService == null)
      _initBankServiceProxy();
    return bankService.getAccount(arg0);
  }
  
  public dtu.ws.fastmoney.Account getAccountByCprNumber(java.lang.String arg0) throws java.rmi.RemoteException, dtu.ws.fastmoney.BankServiceException{
    if (bankService == null)
      _initBankServiceProxy();
    return bankService.getAccountByCprNumber(arg0);
  }
  
  public dtu.ws.fastmoney.AccountInfo[] getAccounts() throws java.rmi.RemoteException{
    if (bankService == null)
      _initBankServiceProxy();
    return bankService.getAccounts();
  }
  
  public java.lang.String createAccountWithBalance(dtu.ws.fastmoney.User arg0, java.math.BigDecimal arg1) throws java.rmi.RemoteException, dtu.ws.fastmoney.BankServiceException{
    if (bankService == null)
      _initBankServiceProxy();
    return bankService.createAccountWithBalance(arg0, arg1);
  }
  
  
}