# About this project

This is the exam project submission for the *Software Developement Methods AY 2020/21* course, offered by *University of Trieste*, Italy.  
It consists in an implementation of the original board game *"Take it Easy"* by *Peter Burley* for sole educational purposes, no copyright infringment is intended.  
The authors of this project are **Alberto Luvisutto**, **Nicolas Plasencia**, **Michele Rispoli** and **Azad Sadr**.

# Overview
The game consists in placing hexagonal tiles on a board with 19 hexagonal slots. There are 27 possible tiles, as these are the possible ways of picking a value for the vertical row (1,5,9), one for the bottom left row (2,6,7) and one for the bottom right row (3,4,8). Each player has its own board and, at each turn, all the players place the same randomly picked tile on it. The game ends once all the 19 tiles have been placed, at which point players' scorers are computed basing on the rows with matching values in their board: a row of tiles *all* presenting the same matching value for a particular direction grants $  ext{\#tiles}   imes   ext{value}$ points to that player (e.g. a verticl row of 5 tiles all presenting a 9 in the top position is worth $5  imes 9 = 45$ points; it would have been worth *none* instead if any two of those tiles presented a different values in the top position). The player with the highest total score wins the match.

# Implementation Details
WIP  
Classes
- `enum GameState`
  - Fields:
    - `MAINMENU`
    - `OPTIONS`
    - `LOCALLOBBY`
    - `NETWORKEDLOBBY`
    - `STATISTICS`
    - `INMATCH`
- `class Game`
  - Fields:
    - `private GameState current_state`
    - `?? GUI client ??`
  - Methods:
    - `public void run()`
    - `private ?? get_available_options()`
- `class GameMatch`
  - Fields:
    - `private Player[] players`
    - `private TilePool tile_pool`
    - `status???`
  - Methods:
    - `public void? setup(... seed)` : setup players and init `tile_pool`
    - `public play()` // main function: cycle through the players, pop the tile, wait for the placing of the tiles, stop the game
    - `private compute_scores()` // be used in play()
    - `private show_scores()` // be used in play()
- `class TilePool`: // extracted tiles for a single game
  - Fields:
    - `private Tile[] extracted_tiles` 
  - Methods:
    - `public TilePool(seed = none)` // constructor takes the seed and calls generate_tile_pool, or generate the seed if not provided
    - `public Tile get_tile(index)`
    - `private Tile[] generate_tile_pool(seed)` // performs the random generation based on the seed
- `class Tile`
  - Fields:
    - `private int? top`
    - `private int? left`
    - `private int? right`
  - Methods:
    - `public Tile(top, left, right)` - checks top,left,right validity?
    - `public get_top`
    - `public get_left`
    - `public get_right`
- `class HexCoordinates`
  - Fields
    - `private x`
    - `private y`
    - `private z`
  - Methods
    - ... ctor and getters
- `class Board`
  - Fields:
    - `private Tile[] storage`
  - Method:
    - `public place_tile(tile, coordinates)` // inside the board class? Or outside and it takes a board object too?
    - `public unsigned int compute_score()`
    - `private unsigned int map_cube_coords_to_index(x, y, z)` // map from coordinates to storage, probably static, return an index
- `enum PlayerStatus`
  - Fields:
    - `WAITING_MATCH`
    - `PLACING`
    - `WAITING_PLAYERS`
    - `LEFT`
- `class Player`
  -Fields:
    - `private String name`
    - `private PlayerStatus current_status` // what is he doing in this moment: placing the tile, ready for a new round, finished game
    - `private IBoard object` (basically, the player acts on its own board)
  - Methods:
    - `public place_tile(newtile)` transitions status from "placing" to "waiting"
    - `private HexCoordinates input_coordinates()`
