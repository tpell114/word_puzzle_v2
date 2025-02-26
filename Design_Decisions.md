# Protocol Design Document: Multiplayer Criss-Cross Word Puzzle with Java RMI 

## Introduction
### Objective: This document outlines the design and implementation of a distributed multiplayer Criss-Cross word puzzle game using Java RMI.
### Scope: The system enables single-player and multi-player gameplay where users take turns guessing words or letters. The system includes microservices for word repository, user account management, and a scoreboard.
### Technologies Used: Java RMI, ConcurrentHashMap, Remote Interfaces

## System Architecture
### Components
*  Client: Handles user interactions and communicates with the game server, account service, and scoreboard via RMI.
*  Server: Manages game logic, tracks game state, and coordinates multiple players.
*  Account Service: Manages user registration and scores.
*  Word Repository: Stores and retrieves words for puzzle generation.
*  Scoreboard: Computes player rankings.
*  RMI Registry: Registers all remote services.

### Data Exchange and Method Invocation Flow
#### User Registration and Score Management

|Client Call| Method (Server Side)| Parameters Sent | Return Value | Action on the Server|
|-------------|---------------------|-----------------|--------------|---------------------|
|User starts game and registers|registerUser(String username) (AccountService)| username (String)| Boolean (true if registered, false if already exists) |Adds the user to ConcurrentHashMap<String, Integer> with an initial score of 0 if they don’t already exist.                     |
|User requests their score        | getUserScore(String username) (AccountService) | username (String) | Integer (current score of user) |  Retrieves and returns the user's score.   |
|Server updates score after a game  | updateUserScore(String username, Integer value) (AccountService) | username (String), value (Integer, score update amount) | void  | Updates user score in ConcurrentHashMap.                    |
|Client requests the leaderboard |  getScores(Integer n) (Scoreboard)  |  n (Integer, number of top players to return) |   List<Map.Entry<String, Integer>> (sorted scores) | Retrieves the top n users from AccountService and sorts them.                    |


#### Game Setup and Joining a Game

|Client Call| Method (Server Side)| Parameters Sent | Return Value | Action on the Server|
|-----------|---------------------|-----------------|--------------|---------------------|
| Client requests to start a game|  startGame(String username, ClientCallbackInterface client, Integer numWords, Integer difficultyFactor) (Server)|username (String), client (RMI callback), numWords (Integer), difficultyFactor (Integer)  |  Integer (gameID)            | Generates a random gameID, creates a PuzzleObject, and initializes the game puzzle.                    |
| Client requests to join an existing game | joinGame(Integer gameID, String username, ClientCallbackInterface client) (Server) | gameID (Integer), username (String), client (RMI callback) | Boolean (true if joined, false if gameID invalid)             | Adds the player to the existing game’s players list. Sends a callback to all existing players notifying them of the new player.                    |
| Server starts the game (after enough players join)  | issueStartSignal(Integer gameID) (Server)| gameID (Integer)   |  void            |  Notifies all clients via RMI callback that the game has started.                   |

#### Gameplay - Player Actions

|Client Call| Method (Server Side)| Parameters Sent | Return Value | Action on the Server|
|-----------|---------------------|-----------------|--------------|---------------------|
| Client requests the initial puzzle|  getInitialPuzzle(Integer gameID) (Server)  | gameID (Integer) |  char[][] (puzzle state) |  Returns the current puzzle state from PuzzleObject. |
| Client makes a letter guess  | playerGuess(String username, Integer gameID, String guess) (Server)| username (String), gameID (Integer), guess (String)| void |    Checks if the guess is correct. If correct, updates puzzleSlave. If incorrect, decreases guess counter. Calls handleGameWin() or handleGameLoss() if conditions are met.|
|  Server updates player turn  | onYourTurn(char[][] puzzle, Integer guessCounter, Integer wordCounter) (Client Callback)| puzzle (char[][]), guessCounter (Integer), wordCounter (Integer) |  void  |   Notifies the next player that it's their turn and updates the puzzle state.                  |
| Server notifies all other players|  onOpponentTurn(char[][] puzzle, Integer guessCounter, Integer wordCounter) (Client Callback)| puzzle (char[][]), guessCounter (Integer), wordCounter (Integer)| void|Updates other players with the latest game state while they wait for their turn.|

#### Game Completion and Exit Handling

|Client Call| Method (Server Side)| Parameters Sent | Return Value | Action on the Server|
|-----------|---------------------|-----------------|--------------|---------------------|
|Server detects game win|onGameWin(char[][] puzzle, Integer guessCounter, Integer wordCounter, Map<String, Integer> scores) (Client Callback)|puzzle (char[][]), guessCounter (Integer), wordCounter (Integer), scores (Map<String, Integer>)|void|Notifies all clients that the game has been won, updates scores, and ends the game session.|
|Server detects game loss|onGameLoss(char[][] puzzle, Integer guessCounter, Integer wordCounter, Map<String, Integer> scores) (Client Callback)| puzzle (char[][]), guessCounter (Integer), wordCounter (Integer), scores (Map<String, Integer>)                | void             | Notifies all players that the game has been lost, and ends the session.                    |
| Client quits the game| playerQuit(Integer gameID, String username) (Server)|gameID (Integer), username (String)|    void| Removes player from the game. If no players are left, deletes the PuzzleObject.                    |
| Client disconnects|handleExit() (Client)|None|void|Calls playerQuit() if the user was in an active game, then gracefully exits.|

#### Word Repository Operations

|Client Call| Method (Server Side)| Parameters Sent | Return Value | Action on the Server|
|-----------|---------------------|-----------------|--------------|---------------------|
| Client adds a word|  addWord(String word) (WordRepository)|  word (String)| Boolean (true if added, false if already exists)| Adds word to repository and sorts list.|
|  Client removes a word| removeWord(String word) (WordRepository)| word (String)| Boolean (true if removed, false if not found)|  Deletes word from repository.|
|Client checks if a word exists| checkWord(String word) (WordRepository)|word (String)|  Boolean (true if exists, false if not)|Searches for word in repository.|
| Server fetches a word for the puzzle| getWord(int minLength) (WordRepository)|  minLength (Integer)| String (random word matching criteria)|Retrieves a random word that meets the required length.|

### Summary of Data Flow

#### Client  ↔ Server:
* The client interacts with the game server to start/join a game, make guesses, and receive game updates.
#### Server   ↔Word Repository:
* The server allows players to add, remove, and check words (since these actions go through the game server).
#### Server  ↔ Account Service:
* The server retrieves and updates player scores.
#### Client    ↔Account Service:
* The client registers users and retrieves their scores.
#### Client ↔  Scoreboard:
* The client requests leaderboard rankings.

####  Scoreboard ↔ Account Service:
* The scoreboard retrieves scores from the Account Service and ranks players.

## Design Decisions & Justification

#### Decision category
* justification


## Challenges & Solutions

| Challenges   |   Solutions |
|------------- |-------------|
|              |        |
|              |             |
|              |             |
|              |             |
|              |             |
|              |             |
|              |             |



## Coding Standards

* Programming Language: Java
* IDE: Visual Studio Code
* Version Control: GitHub 
* Naming Conventions: Camel case

## Code Review Process
* Communication through discord 


## Statement of Contribution
| Name| Server|Client| Word Repo| Word repo interface| Account Service|AccountInterface|ClientcallbackInterface|Constants|CrisscrossPuzzleInterface|PuzzleObject|RMIRegistry|Scorebooard|Scoreboard Interface|
-----|-------|------|-----------|--------------------|----------------|----------------|-----------------------|---------|-------------------------|------------|-----------|-----------|--------------------|
|Tyler|Tyler |Tyler | Tyler     |Tyler               |                |                |Tyler                  |Tyler    |Tyler                    |Tyler       |Tyler      |           |                    |
|Juan |      |      | Juan      |                    |Juan            |Juan            |                       |         |                         |            |           |Juan       |Juan                |  

