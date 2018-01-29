Feature: create account
Scenario: success account creation
	Given a user with name "user1" and CPR number "1234d" with bank account
	When I create an account for the user
	Then I see the status 409 #because the account already exists
	
Scenario: user has no bank account
	Given a user with name "user1" and CPR number "unknown" who does not have a bank account
	When I create an account for the user
	Then I see the status 503
