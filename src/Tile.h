#ifndef TILE_H
#define TILE_H

#pragma once
class Tile
{
public:
	enum TileType { Walkable, Collider };
	enum LayerType { Background, Colliders };

	static SDL_Texture* tileSet;
	static void initTileSet() { tileSet = IMG_LoadTexture(SdlComponents::getInstance()->getRenderer(), "assets/map.png"); };

	Tile(Vec2 position, TileType tileType, LayerType layerType, int tileID);
	~Tile();

	void draw();

	Vec2 getPosition() { return this->_position; };
	void setPosition(Vec2 newPosition) { this->_position = newPosition; };
	TileType getType() { return this->_tileType; };
	int getGid() { return this->_tileID; };

private:
	Vec2 _position;
	TileType _tileType;
	LayerType _layerType;
	int _tileID;

	SDL_Rect _sizeRect;
	SDL_Rect _cropRect;
};

#endif