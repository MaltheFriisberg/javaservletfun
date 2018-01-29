Feature: obtain barcode
Scenario: successfull barcode generation
	Given a barcode does not exists for the user with name "hans" and CPR "121212121212"
	When I create a barcode for the user ##Barcodes.POST
	Then I see the status code 200
	 
Scenario: user doesnt exists
	Given a user with uuid "213123" does not exists
	When I create a barcode for the user with uuid "213123" ##Barcode.POST
	Then I see the status code 404
	
Scenario: user reached limit of barcodes
	Given the user has 10 barcodes and tries to generate more ##Barcode.GET
	When i create a barcode for the user with name "hans" and CPR "121212121212" ##Barcode.POST
	Then I see that both results from generating new barcodes is the same

Scenario: user attempts to reuse barcode
	Given the user has used one of their barcodes and noted it down ##Transaction.POST
	When I try to make a transaction with the used barcode ##Transaction.POST
	Then I see the status code 400