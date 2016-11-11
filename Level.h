#ifndef LEVEL_H
#define LEVEL_H

#pragma once

#include "stdafx.h"
#include "Character.h"
#include <vector>
#include "tinyxml2.h"
#include "Tile.h"

class Level
{
public:
	Level(int mapWidth, int mapHeight, Character* character, std:: string levelName);
	~Level();
	void drawAndUpdate(float deltaTime);

	int getCharacterMapX() { return this->_characterMapX; };
	int getCharacterMapY() { return this->_characterMapY; };

	void setCharacterMapX(int newX) { this->_characterMapX = newX; };
	void setCharacterMapY(int newY) { this->_characterMapY = newY; };

private:
	void handleCharacterMovement();
	bool isTileWalkable(Vec2 position);
	void runTileAction(Tile* currentTile);
	std::vector<Tile*> loadMapFromTMX(string mapName);
	Tile* getTileOnPos(Vec2 position);

	int _mapWidth;
	int _mapHeight;
	std::vector<Tile*> _map;

	Character* _character;
	int _characterMapX;
	int _characterMapY;
};

#endif