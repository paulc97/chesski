# Next Magnus - Chess AI
This is a Java Chess AI that is based on bitboards and that implements the variant *king of the hill* with several techniques such as principal 
variation search, dynamic peace square tables, transposition tables, move ordering and many more. Furthermore, the project includes a basic implementation of the Monte Carlo Tree Search. The implementation of the techniques can be found in the *Model* package. Involved techniques can be configured via the *moveSelector* method in the *MoveGenerator* class.

The AI allows to play games in different modes:

- local via the included game server
- via a remote game server
- and as an AI vs. AI game in the same thread

To start a game follow the steps below:
###Local game server
1. Start the *game-server.jar* in the project.
2. Open the UI in your browser via 127.0.0.1:8025
3. Execute the main in *GameserverPlayer1* to play against the bot.
4. Execute *GameserverPlayer2* to play AI vs. AI on the server.
5. To reset the game server status delete the content of the db folder.

###Remote game server
1. Configure the URI of the running web server in the main method of RemotePlayer1 or RemotePlayer2 and execute.

The game server can be found under: https://github.com/Theaninova/kothserver

###AI vs. AI
1. Execute the *LocalGame* class.

The game status will be printed on the console.



This project is part of the AI project "Projekt KI - Symbolische Künstliche Intelligenz" at TU Berlin.

