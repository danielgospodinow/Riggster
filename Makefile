CC = g++
CFLAGS = -c -Wall
LINKER_FLAGS = -lSDL2main -lSDL2 -lSDL2_image -lSDL2_mixer -lpthread

all: riggster

riggster: Riggster.o Game.o Character.o Level.o SdlComponents.o Sprite.o stdafx.o tinyxml2.o Tile.o
			$(CC) Riggster.o Game.o Character.o Level.o SdlComponents.o Sprite.o stdafx.o tinyxml2.o Tile.o -o Riggster $(LINKER_FLAGS)
Riggster.o: src/Riggster.cpp
			$(CC) $(CFLAGS) src/Riggster.cpp

Game.o:	src/Game.cpp
			$(CC) $(CFLAGS) src/Game.cpp

Character.o:	src/Character.cpp
			$(CC) $(CFLAGS) src/Character.cpp

Level.o:	src/Level.cpp
			$(CC) $(CFLAGS) src/Level.cpp

SdlComponents.o:	src/SdlComponents.cpp
			$(CC) $(CFLAGS) src/SdlComponents.cpp

Sprite.o:	src/Sprite.cpp
			$(CC) $(CFLAGS) src/Sprite.cpp

stdafx.o:	src/stdafx.cpp
			$(CC) $(CFLAGS) src/stdafx.cpp

tinyxml2.o:	src/tinyxml2.cpp
			$(CC) $(CFLAGS) src/tinyxml2.cpp

Tile.o:	src/Tile.cpp
			$(CC) $(CFLAGS) src/Tile.cpp

clean:
			rm -rf *.o Riggster
