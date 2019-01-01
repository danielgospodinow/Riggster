# Riggster
This project contains:
* A multiplayer RPG Roguelike game.
* A game server which manages the gameplay state through all the clients.

## Description
The server is containerized using `docker`. The image can be found at: https://hub.docker.com/r/danielgospodinow/riggster-server.

The purpose of dockerizing the server, in my case, is that it can the easly deployed on an AWS machine.

The server manages the communication between the players, the state of the enemies in the game, the state of the treasures on the map, etc.

Whereas the game, It has the typical experience for It's genre. A player's mission is to collect treasures, which drop health potions, mana potions, weapons and spells and with those collectables to go and kill enemies, meet new people online, etc.

## How to install

#### The server
The server can be easly deployed through `docker` with the `docker run -p 3000:3000 -t danielgospodinow/riggster-server` command, which runs the server and exposes the container's port 3000 to the machine's port 3000.

#### The client
In case of running the game, you should build it. 
Before building the game make sure you edit the `packr-YOUR_BUILD_TARGET-config.json` file in the Riggster directory. Set It's `jdk` property to a valid path JDK path.
* For Windows (64-bit) run `cd Riggster && ./build-win64.sh`
* For Linux (64-bit) run `cd Riggster && ./build-linux.sh`

###### Development screenshots:
<img src = "https://i.imgur.com/oDMio5s.png"/>
<img src = "https://i.imgur.com/g0rHvPy.png" width="2000px"/>
<img src = "https://i.imgur.com/MMnkufU.png" width="2000px"/>
