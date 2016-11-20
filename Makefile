CC = g++
CFLAGS = -c -Wall
LINKER_FLAGS = -lSDL2main -lSDL2 -lSDL2_image -lSDL2_mixer -lpthread

all: riggster

riggster: Riggster.o Game.o Character.o Level.o SdlComponents.o Sprite.o stdafx.o tinyxml2.o Tile.o
			$(CC) Riggster.o Game.o Character.o Level.o SdlComponents.o Sprite.o stdafx.o tinyxml2.o Tile.o -o Riggster $(LINKER_FLAGS)
Riggster.o: Riggster.cpp
			$(CC) $(CFLAGS) Riggster.cpp

Game.o:	Game.cpp
			$(CC) $(CFLAGS) Game.cpp

Character.o:	Character.cpp
			$(CC) $(CFLAGS) Character.cpp

Level.o:	Level.cpp
			$(CC) $(CFLAGS) Level.cpp

SdlComponents.o:	SdlComponents.cpp
			$(CC) $(CFLAGS) SdlComponents.cpp

Sprite.o:	Sprite.cpp
			$(CC) $(CFLAGS) Sprite.cpp

stdafx.o:	stdafx.cpp
			$(CC) $(CFLAGS) stdafx.cpp

tinyxml2.o:	tinyxml2.cpp
			$(CC) $(CFLAGS) tinyxml2.cpp

Tile.o:	Tile.cpp
			$(CC) $(CFLAGS) Tile.cpp

clean:
			rm -rf *.o Riggster
