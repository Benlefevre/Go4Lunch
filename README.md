# Go4Lunch

An Android app that allows you to choose a restaurant where you want to lunch.  
You can see where your colleagues want to lunch and they can see what restaurant you choose for lunch.

## Description

This app is developped in Java.

Training to use :

    - Firebase Auth
    - Firestore
    - Firebase Messaging
    - Google Cloud Functions
    - GoogleMap
    - Place API
    - Callback
    - Notification
    - WorkManager

## Features

    - Auth with mail, Google, Facebook and Twitter
    - See your location on a map
    - See all restaurants around you on the map and if someone choosen this restaurant
    - See all restaurants around you in a list 
    - Search a restaurant around you with autoComplete options
    - See the number of users and the rating for all restaurants
    - See where your colleagues want to eat
    - See all your colleagues in a list with their choices of restaurant
    - Receive a notification at 12:00 if you choosen a restaurant

## Run on 

After downloading or clone the repository, you need to create a new project in Firebase and Google Cloud Platform (think to restrict your apikey with your SHA-1 and the correct API).  

In Google Cloud Platform you have to activate :  

    - Google Map
    - Google Place API

Add your google platform apikey in :

    - go4lunch/gradle.properties
      - GOOGLE_MAPS_API_KEY="xxxxxxxxxxxxxxxxxxxxxx"

In Firebase, you have to activate :

    - Auth (with all needed providers and their own key and secret key)
    - Firestore (nothing to do)

Add your Firebase configuration file in :

    - go4lunch/app/google-service.json

After these operations the app is ready.

## Documentation

You can find a french documentation video  at :

    -assets/documentation/demo.mp4
[Go to documentation](https://github.com/Benlefevre/Go4Lunch/blob/master/assets/documentation/demo.mp4)

![demo](assets/documentation/demo.gif)

## Screenshot

### Login screen

![Login screen](assets/screenshot/1.webp)

### Auth by mail

![Auth by mail](assets/screenshot/2.webp)

### Auth by Google

![Auth by Google](assets/screenshot/3.webp)

### Auth by Facebbok

![Auth by Facebook](assets/screenshot/4.webp)

### Auth by Twitter

![Auth by Twitter](assets/screenshot/5.webp)

### Permissions

![Permissions](assets/screenshot/6.webp)

### Home Screen

![Home Screen](assets/screenshot/7.webp)

### Click on a marker

![Map click](assets/screenshot/8.webp)

### Restaurant screen

![Restaurant screen](assets/screenshot/9.webp)

### Home screen with selected restaurant

![Marker green](assets/screenshot/10.webp)

### Restaurant list

![Restaurant list](assets/screenshot/11.webp)

### Search field

![Search](assets/screenshot/12.webp)

### List with number of colleagues in a restaurant

![List rest users](assets/screenshot/13.webp)

### List of colleagues

![Colleagues](assets/screenshot/14.webp)
