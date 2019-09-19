# revolut-backend-task

Implementation of the take-home backend task for Revolut. Runnable as either a standalone jar (**mvn clean install** followed by **java -jar /path/to/resulting/jar**) or via Maven (**mvn exec:java**)

API endpoints:
* POST /api/account - creates an account
* GET /api/account/{id} - retrieves an account by id
* GET /api/account/{id}/transfers - lists all transfers involving the specified account
* POST /api/transfer - performs money transfer between two accounts
* GET /api/transfer/{id} - retrieves a transfer by id

Notes:
* Accounts cannot have negative balance
* Any account is associated with one of the supported currencies. Currency conversion is not supported
* For the purposes of this task, there is no separate user entity (to associate multiple accounts with a single user)