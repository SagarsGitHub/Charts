# Charts
Java app using springBoot framework to access Spotify and return a list of a User's top artists


# Basic Usage
This project can be compiled and run in any Java environment, however, there are a couple steps to follow before the program
will run.

First, set up an app with spotify and create a client ID and client secret.

Second, allow the popular artist function in the app permission list in spotify.

Third, enter the following into redirect URIs: http://localhost:8081/callback.

Fourth, replace the string variables named clientId and clientSecret with the proper data.


## Running the Application

Once the previous steps have been completed, you can run the main program in java. Using an API platform like postman or insomnia,
send a GET request to http://localhost:8081/test.

Once sent, you will recieve a URL redirect that will allow you to sign into spotify. Once signed in successfully, you will recieve
a list of your most listened to artists. (The results are a bit unpolished, but will be improved in future updates)
