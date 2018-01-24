# Features
- Quick to set up realtime chat functionality
- Works with existing firebase projects as well as new ones
- Easy to use, two-screen architecture
- Sends text messages and location updates (images, videos and voice in near future)
- Fully customisable
- Optional push notifications  implementation
- Android <-> iOS Compatibility
- Fully written in Kotlin
- A fully functional sample included in this repository.

# Installation

1. Set up the [Chatlover-Firebase](https://github.com/ApploverSoftware/Chatlover-Firebase) project in order to provide the backend functions for Chatlover's internal logic to work properly.
1. Connect your application to the [Firebase project you created in the 1st step](https://console.firebase.google.com)
1. Add `compile 'pl.applover:chatlover:0.13.1'` to your app's module `build.gradle` file under _dependencies_ tag
4. Sync gradle project
1. In your app's logic implement following features:
	1. Use `ChatUser.loginWithUid()` to log in with an existing user or create a new one and save them to database. The logged-in user will be persisted till the app is closed. Use completion to perform an action after success. You need to pass a user-unique uid and a name as parameters.
	1. Add a `ChannelListFragment` to an activity and container of your choice. Link it with the activity by calling `withListener()` method. The preferred method of creating in an Activity is `ChannelListFragment.newInstance().withListener(this)`.
	1. Create and display a `ChatFragment` (preferably in `onChatRequested` of `ChannelListListener`)
	1. To create a Channel call the makeChannel HTTP trigger from your project’s firebase functions or [write some other trigger to do it automatically when something happens in your application](https://firebase.google.com/docs/functions/database-events).
	1. Configure the Config objects (listed below) in your Application class or MainActivity (before the Chatlover fragments are created) for stable results.

6. To enable push notifications add the following to Manifest file under application tag:
``` xml
<service
    android:name="pl.applover.chatlover.services.ChatloverMessagingService">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>
<service
    android:name="pl.applover.chatlover.services.ChatloverInstanceIdService">
    <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT"/>
    </intent-filter>
</service>
```
7. If you need to combine Chatlover's push notifications with an existing implementation of Firebase Notification, you can check for _"chatlover"_ node in remoteMessage's data in your own `FirebaseMessagingService` and use `FirebaseChatMessagingService.handleChatMessage()` method for certain messages only. Every notification sent by ChatLover's send_notification function has a "chatlover" node and it is the only supported way of recognizing the notification's origin.

# Customization

Chatlover sports the lightest way of configuring the looks and behaviors of its views. The Config objects are singletons to which you pass your required configuration. If a given field is set to null, the default value is used. This makes them non-dynamic by design but makes it really straightforward to properly configure in Kotlin. 

Clone this repository and check MainActivity.config() in the Sample module for an example of how to use the configs. Note that for the styling to work properly, the configuration should take place before creating any Chatlover fragments. 

The objects' fields are listed below (Defaults given in square brackets where applicable).:

### ChannelListConfig


- **picturePlaceholder**: _Drawable_

Channel picture placeholder drawable


- **pictureDecider**: _(channel: Channel, userRef: StorageReference, channelRef: StorageReference) -> StorageReference_

Function that returns a storage reference of the picture chosen for given channel


- **pictureSize**: _Int_

Size of the channel picture in dp


- **nameSize**: _Float_

Channel name text size in sp


- **nameColour**: _Int_

Channel name text colour


- **nameDecider**: _(channel: Channel) -> String_

Function that returns name of a channel


- **lastMsgSize**: _Float_

Last message text size in sp


- **lastMsgColour**: _Int_

Last message text colour


- **timeSize**: _Float_

Time text size


- **timeColour**: _Int_

Time text colour


- **itemBackground**: _Int_

Channel List item background colour


- **dividerColour**: _Int_

Divider line colour


- **onFragmentViewCreated**: _(View?) -> Unit_

Function called after View creation of the Fragment


- **swipeActions**: _List<SwipeAction<Channel>>_

List of (max. 4) swipeActions, consisting of colours, icons, texts, and functions to be called on click. If empty list is passed, the item is not swipeable


- **swipeActionWidth**: _Float_

Width of a single swipeAction button in dp.

### ChatViewConfig


- **bubbleShape**: _Drawable_

Background Drawable of the message bubble


- **bubbleColourOwn**: _Int_

Colour of user’s own message bubbles


- **bubbleColourOther**: _Int_

Colour of other users’ message bubbles


- **textSize**: _Float_

Size of text message or location message title in sp


- **textSizeSecondary**: _Float_

Size of location message address in sp


- **textColour**: _Int_

Colour of text message or location message title


- **textColourSecondary**: _Int_

Colour of location message address


- **labelColour**: _Int_

Colour of time label


- **labelSize**: _Float_

Size of time label


- **labelTimeFormat**: _String_

SimpleDateFormat of time label


- **labelIsShown**: _Boolean_

Determines if time label is shown or not


- **headerColour**: _Int_

Date header and lines colour


- **headerSize**: _Float_

Date header text size


- **headerIsShown**: _Boolean_

Determines if date header is shown


- **headerTimeFormat**: _String_

SimpleDateFormat of date header


- **avatarIsShown**: _Boolean_

Determines if others’ avatars are shown or not


- **avatarSize**: _Int_

Avatars’ size in dp


- **avatarPlaceholder**: _Drawable_

Avatar’s placeholder drawable


- **avatarOnClick**: _(ChatUser) -> Unit_

Function called when avatar is clicked


- **inputBackground**: _Drawable_

Background drawable of the input field


- **inputHint**: _String_

Input field’s hint text


- **inputTextSize**: _Float_

Input field’s text size


- **inputTextColour**: _Int_

Input field’s text colour


- **inputMaxLines**: _Int_

Input field’s max lines number


- **iconSend**: _Drawable_

Icon drawable for send action


- **iconLocation**: _Drawable_

Icon drawable for send location action


- **onTxtClick**: _(message: Message, context: Context) -> Unit_

Function called on text message click


- **onTxtLongClick**: _(message: Message, context: Context) -> Unit_

Function called on text message long click


- **onLocClick**: _(message: Message, context: Context) -> Unit_

Function called on location message click


- **onLocLongClick**: _(message: Message, context: Context) -> Unit_

Function called on location message long click


- **onFragmentViewCreated**: _(View?, Channel) -> Unit_

Function called when Fragment View is created


- **onBackPressed**: _() -> Unit_

Function called when back arrow is pressed


- **locationFoundText**: _String_

### NotificationsConfig


- **notificationTarget**: _Class<out Activity>_ 

Activity to be opened on notification click


- **areNotificationsEnabled**: _Boolean_

Determines if the notifications are shown or not


- **notificationCreator**: _(title: String?, body: String?, message: Message, channel: Channel, sender: ChatUser) -> Notification_

Function that constructs the system notification


- **onTokenRefresh**: _() -> Unit_

Function to call after Firebase token is refreshed (If you overwrite it, ChatUser’s fcmToken field will not be updated automatically)
