const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationForLunch = functions.firestore
    .document('users/{userId}')
    .onUpdate((change, context) => {
        const previousValues = change.before.data();
        const nextValues = change.after.data();

        const previousRestaurantName = previousValues.restaurantName;
        const nextRestaurantName = nextValues.restaurantName;
        const token = nextValues.messToken;
        var payload;

        if (nextRestaurantName !== null){
            payload = {
                data: {
                    type: "notification",
                    tag: nextRestaurantName
                },
            };
        } else {
            payload = {
                data: {
                    type: "suppress",
                    tag: previousRestaurantName
                },
            };
        }

        const options = {
            priority: "high",
            timeToLive: 60*60*24
        };

        return admin.messaging().sendToDevice(token, payload, options);
    });
