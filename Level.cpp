#include "stdafx.h"
#include "Level.h"
#include <sstream>

using namespace tinyxml2;

Level::Level(int mapWidth, int mapHeight, Character* character, std::string levelName) :
	_mapWidth(mapWidth),
	_mapHeight(mapHeight),
	_character(character)
{
	this->_map = loadMapFromTMX(levelName);

	this->_characterMapX = 0;
	this->_characterMapY = 0;
}


Level::~Level()
{
	for (int i = 0; i < _map.size(); i++)
		delete _map[i];
}

void Level::drawAndUpdate(float deltaTime)
{
	if (this->_character->canMove())
		this->handleCharacterMovement();

	for (int i = 0; i < _map.size(); i++)
		_map.at(i)->draw();
}

void Level::handleCharacterMovement()
{
	Vec2 vectorUp = Vec2(_characterMapX, _characterMapY - 1);
	Vec2 vectorLeft = Vec2(_characterMapX - 1, _characterMapY);
	Vec2 vectorDown = Vec2(_characterMapX, _characterMapY + 1);
	Vec2 vectorRight = Vec2(_characterMapX + 1, _characterMapY);

	switch (SdlComponents::getInstance()->checkInput())
	{
	case SDLK_w:
		if ((this->_character->getPosition() + Vec2(0, -20)).y < 0 || !isTileWalkable(vectorUp))
			return;

		runTileAction(getTileOnPos(vectorUp));

		this->_characterMapY -= 1;
		this->_character->runAction(Character::CharacterAction::MoveUp);
		break;
	case SDLK_a:
		if ((this->_character->getPosition() + Vec2(-20, 0)).x < 0 || !isTileWalkable(vectorLeft))
			return;

		runTileAction(getTileOnPos(vectorLeft));

		this->_characterMapX -= 1;
		this->_character->runAction(Character::CharacterAction::MoveLeft);
		break;
	case SDLK_s:
		if ((this->_character->getPosition() + Vec2(0, 20)).y >= globals::GAME_HEIGHT || !isTileWalkable(vectorDown))
			return;

		runTileAction(getTileOnPos(vectorDown));

		this->_characterMapY += 1;
		this->_character->runAction(Character::CharacterAction::MoveDown);
		break;
	case SDLK_d:
		if ((this->_character->getPosition() + Vec2(20, 0)).x >= globals::GAME_WIDTH || !isTileWalkable(vectorRight))
			return;

		runTileAction(getTileOnPos(vectorRight));

		this->_characterMapX += 1;
		this->_character->runAction(Character::CharacterAction::MoveRight);
		break;
	}
}

bool Level::isTileWalkable(Vec2 position)
{
	bool canWalk = true;

	Tile::TileType nextTileType;
	Tile* tile = getTileOnPos(position);
	if (tile)
		nextTileType = tile->getType();

	switch (nextTileType)
	{
	case Tile::TileType::Walkable: break;
	case Tile::TileType::Collider: canWalk = false; break;

	default: break;
	}

	return canWalk;
}

void Level::runTileAction(Tile * currentTile)
{
	switch (currentTile->getGid())
	{
	case 262:
		//End game
		this->_character->setDead(true);
		this->_map.clear();

		break;

	case 510:
		this->_character->setDead(true);
		break;

	default: break;
	}
}

std::vector<Tile*> Level::loadMapFromTMX(string mapName)
{
	std::vector<Tile*> tempMap;

	XMLDocument doc;
	stringstream ss;
	ss << "assets/" << mapName << ".tmx";
	doc.LoadFile(ss.str().c_str());

	XMLElement* mapNode = doc.FirstChildElement("map");
	XMLElement* layer = mapNode->FirstChildElement("layer");

	if (layer != NULL)
	{
		while (layer)
		{
			const char* layerName = layer->Attribute("name");
			XMLElement* data = layer->FirstChildElement("data");

			if (data != NULL)
			{
				int tileWidthNum = 0, tileHeightNum = 0;
				XMLElement* tile = data->FirstChildElement("tile");

				while (tile)
				{
					if (tileWidthNum >= _mapWidth)
					{
						tileWidthNum = 0;
						tileHeightNum += 1;
					}

					int gid = 0;
					tile->QueryIntAttribute("gid", &gid);

					if (gid != 0)
					{
						Tile::TileType tt = Tile::TileType::Walkable;
						Tile::LayerType lt = Tile::LayerType::Background;

						if (std::strcmp(layerName, "background") == 0)
						{
							tt = Tile::TileType::Walkable;
							lt = Tile::LayerType::Background;
						}
						else if (std::strcmp(layerName, "colliders") == 0)
						{
							tt = Tile::TileType::Collider;
							lt = Tile::LayerType::Colliders;
						}

						Tile* tempTile = new Tile(Vec2(tileWidthNum, tileHeightNum), tt, lt, gid - 1);

						//Override the old tile on the same position
						Tile* oldTile = NULL;
						for (int i = 0; i < tempMap.size(); i++)
						{
							if (tempMap.at(i)->getPosition() == Vec2(tileWidthNum, tileHeightNum))
							{
								oldTile = tempMap.at(i);
								break;
							}
						}
						if (oldTile != NULL)
							oldTile->setPosition(Vec2(0, 0));

						tempMap.push_back(tempTile);
					}

					tileWidthNum++;

					tile = tile->NextSiblingElement("tile");
				}
			}

			layer = layer->NextSiblingElement("layer");
		}
	}

	return tempMap;
}

Tile* Level::getTileOnPos(Vec2 position)
{
	Tile* wantedTile = NULL;

	for (int i = 0; i < _map.size(); i++)
	{
		if (_map.at(i)->getPosition() == position)
		{
			wantedTile = _map.at(i);
			break;
		}
	}

	return wantedTile;
}
