# WiFi_Positining

 Gachon Univefrsity Sensor and Wireless Network Class Term Project.
 
This is a project for the experimental implementation of WiFi Indoor Positioning technology.

This is an app repository for clients, and the server implementations can be found at the link below.<br>
Node.js : https://github.com/chromato99/WiFi_Positioning_Server <br>
Golang : https://github.com/chromato99/WiFi_Positioning_Server_go

Introduction Presentation : https://docs.google.com/presentation/d/1FHGp0ds0RjuQ8WeXwLOzmqLqWt84qh_Q/edit?usp=sharing&ouid=115551594729015574224&rtpof=true&sd=true

# Implementation

We used WiFi Fingerprinting technique to implement WiFi Positioning. This is for more general use as there are APs that do not yet support WiFi RTT.

We implemented it in two parts: server and client. The client measures WiFi data and sends it to the server, and the server stores the data and performs an operation to estimate the location.

Therefore, you must first have a dataset that records data about the location beforehand. 

Then when estimating the position, the previously stored data set and newly measured data are compared and the average of the difference is used.

# Tech Stack

Android Studio
