# Collabin #

Collabin is an upcoming project discovery and collaboration mobile app that aims to help bring innovators and ideas together. More specifically, Collabin connects people with a project idea seeking collaborators, with people who are looking to use their skills on a project they can believe in.
![Alt text](/screenshots/collabin.png?raw=true "Collabin")
## Our Goals##
1. Encourage people to work together, whether it’s a DIY for fun or a start-up. 
1. Make it easier to form awesome teams for your awesome ideas.
1. Make it easier for people to discover projects that they can be passionate about. 

## How It Works##

There are 2 ways the app can be used (you can do both!)

### 1)	I have a Project Idea and want to find collaborators ###
The user creates a “New Project” and fills out the profile for the newly created project. This includes:
### ###
* 	The name and description of the project
* 	The “Type” of project this falls under (ie Software, Electrical Engineering, Business)
* 	The skills that this project would prefer its collaborators to have.

The user that created the project can then swipe right or left (interested or not interested) through profiles of other users with skills matching the preferred skills of the project that they specified, provided by our app’s server.

### 2)	I want to get involved in a project, but I don’t have an idea right now ###
The user fills out their “Personal Profile” which includes:
### ###
* 	Name, location, and a brief summary about themselves
* 	Any skills that they possess 
* 	The types of projects that they would prefer to work on.

The user can then swipe right or left (interested or not interested) through profiles of projects created by other users that match the preferred project types the user specified.

When a project owner has specified interest in a user and that same user has specified interest in the project, the two users are notified and can message each other using our app to figure out how they want to work together; the rest is history.


## Implementation Details ##
For anyone who wants to contribute to this project, or is just interested in the nitty gritty details of the implementation behind this app. Here is a very brief description of the parts of the app and the source code files associated with each part.
Collabin consists of 2 main parts:
### ###
1. The **REST API** <br> <br>
  The source code can be found under the "server" folder in the root directory. The main files are as follows: <br> <br>
  **urls.py** - Defines the URI routes for each resource in the REST API <br>
  **models.py** - The schema for the database expressed as models from Django's ORM <br>
  **views.py** - Receives and processes the actual requests and returns a response to the client. URIs from urls.py map to                    classes and functions in views.py <br>
  **serializers.py** - Handles conversion between formats when receiving and returning data as well as checking the validity                        of request data.

2. The **Android Client** <br> <br>
  The main source code can be found in the Collabin/app/src/main/java/tunakleague.com.collabin directory. It is divided into   several packages, grouped by functionality.<br> <br>
  **authentication** - User login and registration <br>
  **messaging** - Messaging between users that have been matched. <br>
  **notifications** - Receiving of any push notifications from Google Cloud Messaging (GCM) such as when a match has    been                       found. <br>
  **profiles** - Display and editing of user and project profiles <br>
  **search** - Searching for either potential projects to work on or users to recruit to a project.
