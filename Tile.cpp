#include "stdafx.h"
#include "Tile.h"
#include "math.h"

SDL_Texture* Tile::tileSet = nullptr;

Tile::Tile(Vec2 position, TileType tileType, LayerType layerType, int tileID) :
	_position(position),
	_tileType(tileType),
	_layerType(layerType),
	_tileID(tileID)
{
	_sizeRect = {_position.x * 20, _position.y * 20, 20, 20};

	int cropX = 0, cropY = 0;
	int rowCount = std::ceil(_tileID / 57);
	cropX = (_tileID - rowCount * 57) * 17;
	cropY = rowCount * 17;

	_cropRect = { cropX, cropY, 16, 16 };
}

Tile::~Tile()
{
	SDL_DestroyTexture(tileSet);
}

void Tile::draw()
{
	if (tileSet == NULL)
		cout << "tile set is null" << endl;

	SDL_RenderCopy(SdlComponents::getInstance()->getRenderer(), tileSet, &_cropRect, &_sizeRect);
}
