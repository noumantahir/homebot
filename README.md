# homebot

HomeBot is an internet controlled differential drive robot. The basic control was via an Xbox 360 controller and a pair of VR Goggles, the idea was to make user feel at home even when user is miles away.

It was developed during a Hackathon arranged by Kandy.io, HomeBot secured second place in the competion.

HomeBot uses a pair of Android Devices, one connected with the robot and other as a control device. The middle man was kandy server for streaming videos and firebase for sending real time commands to robot.

The Robot Android device was paired with Robot using wired arduino connection via USB-OTG and Controller devices was paired with Xbox 360 controller and a VR head set

## set up instructions

- There are two android projects in this repo, one is intended for controller phone and other for the robot itself
- import the project to android studio
- make a kandy project and add api key, api secret, two usernames and relevant passwords to MainActiviy.java of both android projects
- make a relevant firebase project and add two apps for user and robot, add the google-services.json to each of app modules

With these credentials added, the app should compile with no issues what so ever

Instructions for building up robot and VR combo (later...)

## HomeBot Videos Playlist
https://www.youtube.com/watch?v=YfnGFEC_Qhs&list=PLmtYV-2a2Jmb5AgO0bG46Vx371JiQetJg

## Collaborizm Project Link
https://www.collaborizm.com/project/B1aTqu4lx
