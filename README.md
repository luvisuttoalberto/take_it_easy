[![Build Status](https://travis-ci.com/luvisuttoalberto/take_it_easy.svg?branch=main)](https://travis-ci.com/luvisuttoalberto/take_it_easy)
# Take it Easy

## About this project

This is the exam project submission for the *Software Developement Methods AY 2020/21* course, offered by *University of Trieste*, Italy.  
It consists in an implementation of the original board game *"Take it Easy"* by *Peter Burley* for sole educational purposes, no copyright infringment is intended.  
The authors of this project are **Alberto Luvisutto**, **Nicolas Plasencia**, **Michele Rispoli** and **Azad Sadr**.

## How to play
The game consists in placing hexagonal tiles on a board with 19 hexagonal slots.  
Each tile displays three values, one on the *top* (1,5 or 9), one on the *bottom-left* (2,6 or 7) and one on the *bottom-right* (3,4 and 8), and there are 27 possible tiles, corresponding to all the possible combinations of these values.  
Each player has its own board and, at each turn, **all the players place the same randomly picked tile on their board**.  
Any of the 27 possible tiles may only appear once in a game and cannot be repositioned once placed.  
When all the players have placed the current tile, a new one is randomly picked and the process repeats until all the players have filled their board.  

**Points** are scored by aligning tiles presenting the **same value** in the spot corresponding to the line orientation (i.e. vertical, diagonal bottom-left, diagonal bottom-right):
when all the tiles in a line present the same number, that line is worth the common number times the number of tiles comprising the line. If any two numbers along the line differ the line is worth 0 points.  

>*As an example, consider the following board:*  
>![Points_example](readme_img/board_scores.png)  
>*The three tiles on the right form a vertical line of 5s (highlighted in yellow) that is worth 5\*3=15 points.*  
>*On the contrary, the central bottom-right diagonal line will be worth no points because there are two tiles in it presenting different values in the bottom-right spot (underlinded in red); this also means that you can stop worrying about what you put in the remaining bottom-right spots along that line (the red Xs).*  

The game ends when all the players have placed the last tile, and **the player who scored most points across all the lines wins**.

### Starting a game
To start a new game navigate to the local lobby from the main menu.  
![local_lobby](readme_img/local_lobby.png)  
In this screen you can set-up the match by *adding* any number of players. You may *rename* or *remove* players and you may also customize the **game seed** to challenge a friend on the same tile set or replay a game to improve your score. Notice that, if not explicitly set, a new random seed is picked at each new match.  
Once you're done setting up the match click on "Start Match" to start playing 

### In-game controls
![local_lobby](readme_img/local_match.png)  
The in game screen shows the current player's board: click on any empty position and then on "PLACE TILE" to make your move. Once the current tile is placed you cannot reposition it, so choose carefully!  
At any point you may look at any player's board by clicking on the "View" button next to their name in the list on the right.  
You may also remove a player from the game by clicking on the "X" near their name and then "Confirm" on the prompt that appears (although you cannot kick the last player in the match).  
You may also interrupt the current game by going back to the lobby or to the main menu using the buttons in the bottom right of the window (NOTE: a new random seed will be picked!).  

When all the players have placed the last tile the game ends displaying all players' scores and stating the winner(s) in the bottom of the window. Two new buttons appear to allow to quickly start a new match against the same players, either using the same seed (i.e. replay with the same tile sequence) or picking a new random one.

## Developement Details
The project was developed using **IntelliJ IDEA Community Edition**, leveraging **gradle** for setting up the build.  
The work was carried on Windows, Mac and Linux machines using git/github to allow parallel collaborative implementation whenever possible (see this repo's history for all the details).  
Furthermore, the core engine of the game was developed using a **test-driven approach** (which also comprised setting up *Travis* for automated testing at each build).  
The graphical interface, implemented in *JavaFX*, was developed with the aid of the **SceneBuilder** and tested manually.  

The project architecture was designed through brainstorming sessions that involved the whole team, and most of the implementation was carried on through **pair-programming** (constantly switching roles and members of each pair, to maximize experience :) ).  

We used the `javafx` and `json` libraries to implement this project.

## Architecture Overview

The codebase is organized in packages:
- `takeiteasy.core` cointains the **core game engine** that runs the game logic
- `takeiteasy.GUI` cointains the javafx **GUI implementation**, which uses the `IGame` interface to control the game and retrieves its state (as a JSON file), acting as client. We strived to keep the game and GUI logic separated.
- `unittests` cointains all the tests pertaining the core game engine
- `takeiteasy.utility` cointains a single utility class that provides facilities to generate the standard set of hexagonal coordinates.  

The JSON interface is detailed in the `takeiteasy.JSONKeys` class and acts as a reference for any GUI implementation that may be used to interface with the core engine.

The application entrypoint is defined in the `takeiteasy.Main` class, which simply launches the `takeiteasy.GUI.FXApplication`.

Here's a brief overview of the main packages:
### Core Game Engine

#### `takeiteasy.core.board`
- `HexCoordinates`  
  Class representing a set of three hexagonal coordinates, used to map tiles to positions on each player's board. [This website](https://www.redblobgames.com/grids/hexagons/) offered precious insight in designing this class. 
- `IBoard`  
  Public interface of the generic game board, that grants the fullfillment of the Open-Closed SOLID principle (there are several variation of the classic Take It Easy rules that require different boards)
- `BoardVanilla`  
  Class implementing the board for playing a 'classic' Take It Easy game. Features methods for placing and retrieving tiles and for computing the score.
#### `takeiteasy.core.game`
#### `takeiteasy.core.gamematch`
#### `takeiteasy.core.player`
#### `takeiteasy.core.tilepool`

### GUI