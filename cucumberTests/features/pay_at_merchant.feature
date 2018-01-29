Feature: Pay at merchant
Scenario: Customer has enough credit to pay
	Given a barcode exists for the user with name "hans" and CPR "121212121212" with default balance #Barcodes.POST User.POST
	Given a merchant exists and we know their bank account id
	When the merchant makes a payment for the amount of 100 and description "test purchase" to the customer ##Transaction.POST
	Then the customer has 100 less money on their balance #Users.GET
	Then the merchant has 100 more money on their balance #Users.GET

Scenario: Customer does not have enough credit to pay 
	Given a barcode exists for the user with name "hans" and CPR "121212121212" with default balance
	Given a merchant exists and we know their bank account id
	When the merchant makes a payment for the amount of 10000 and description "test purchase" to the customer
	Then i see the statuscode 400 meaning failed transaction	
	Then the customer has 0 less money on their balance
	Then the merchant has 0 more money on their balance