# CPEN 391 User App

![Build](https://github.com/rmcreyes/cpen391-user-app/workflows/Build/badge.svg)

## Purpose

Android app in our CPEN 391 project to be used by the customer.
The goal of the user app is to allow registered users to manage their basic information like payment method and license plates, as well as to allow users to view their current and past parking sessions. 

### Validating the app
-	Supports multiple users (has a signup/ login function)
-	Allows users to view the status of their current parking sessions
-	Allow users to add and edit their payment and other personal information 
-	Allow users to add/edit/delete license plates  (should allow users to add multiple license plates under one account) 
-	Allow user to view their past parking history 
-	Allow admin only accounts to view current meter statuses 


## App Architecture  

![image](https://user-images.githubusercontent.com/42983386/113367008-c4c57700-930f-11eb-8e76-f93f68526e7b.png)
![image](https://user-images.githubusercontent.com/42983386/113367084-fa6a6000-930f-11eb-8671-03f4dbc0acb3.png)


## Test Procedues
The testing procedures are to verfiy that the android app works, and also to validate the the app functions as expected and supports all the requirements. 

1.	Unit tests
- The [Unit Test files](app/src/test/java/com/cpen391/userapp) include unit tests to test how data received from the backend is processed before being displayed on the front-end
2.	Manual UI test
- 	 The [Manual UI Test Procedure](Manual_Test_Procedure.pdf) outlines the steps to manually test the user interface of the app. 
- 	 Each Pull Request will also outline the steps to test specific new functions. 

