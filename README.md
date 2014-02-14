cs
==

Client-Server application in Java
It's a chat client-server application written in java that implements the following commands:
LIST - list the users connected to the server
MSG - sends a private message to a selected user
BCAST - sends a message to all users connected
NICK - changes the username of the current user to the one selected
QUIT - deconnects the user


Rules
two users connected to the server can not have the same nickname
errors such as "user does not exist" are properly handled
