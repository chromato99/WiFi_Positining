# WiFi_Positining

Gachon Univefrsity Sensor and Wireless Network Class Term Project.
 
This is a project for the experimental implementation of WiFi Indoor Positioning technology. We used WiFi Fingerprinting technique to implement WiFi Positioning. This is for more general use as there are APs that do not yet support WiFi RTT.

This is an app repository for clients, and the server implementations can be found at the link below.<br>
Golang : https://github.com/chromato99/WiFi_Positioning_Server_go<br>
Old Node.js implementation : https://github.com/chromato99/WiFi_Positioning_Server


Introduction Presentation : https://docs.google.com/presentation/d/1FHGp0ds0RjuQ8WeXwLOzmqLqWt84qh_Q/edit?usp=sharing&ouid=115551594729015574224&rtpof=true&sd=true

# Client App

This is an android app for demonstrating wifi positioning technology using wifi fingerprinting.

Therefore, this app is responsible for measuring the surrounding Wi-Fi signal data and sending it to the server. The data sent to the server is added as a new dataset or used to estimate the current location.

<img src="https://user-images.githubusercontent.com/20539422/175869621-58972adb-6cb1-4256-bdf8-72800a8674d1.png" width=35% height=35%>

<b>Position</b> : This is the location label input of the newly added data. (Not necessary when doing find position, not adding data)<br>
<b>Password</b> : This is the password input when doing add dataset. (Password can be set on the server and is not needed for find position)<br>
<b>Result ouput</b> : This is the server response output.<br>
<b>Add dataset</b> : Button that sends a request to add data to the server (Position and Password input at the top are required)<br>
<b>Find position</b> : A button that sends a request to find your current location.

# Implementation

This app was developed with Android Studio.
Implemented WiFi scan using WiFiManager library. 

The app scans the Wi-Fi data and sends the 'BSSID' and 'RSSI level' information to the server for parsing in json format.

See the link below for more details.<br>
https://developer.android.com/guide/topics/connectivity/wifi-scan

However, according to the [Android developer documentation](https://developer.android.com/guide/topics/connectivity/wifi-scan#wifi-scan-throttling), there are restrictions on Wi-Fi scanning on Android, so you must turn off the restrictions in the developer options to scan multiple times.

*There is code for testing WiFi RTT in the source code, but it is not currently used for app operation.

Data processing is handled by the server, so please refer to the server repository.<br>
https://github.com/chromato99/WiFi_Positioning_Server_go

# Tech Stack

Android Studio
