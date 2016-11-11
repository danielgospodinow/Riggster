#include "Sprite.h"

Sprite::Sprite(const char* imageLocation, SDL_Rect sizeRect)
{
	this->_position = Vec2(sizeRect.x, sizeRect.y);
	this->_posnsizeRect = sizeRect;
	_texture = IMG_LoadTexture(SdlComponents::getInstance()->getRenderer(), imageLocation);

	if (_texture == NULL)
		cout << "Couldn't load image!" << endl;
}

Sprite::Sprite(const char* imageLocation, SDL_Rect sizeRect, SDL_Rect cropRect)
{
	this->_position = Vec2(sizeRect.x, sizeRect.y);
	this->_posnsizeRect = sizeRect;
	this->_cropRect = cropRect;
	_texture = IMG_LoadTexture(SdlComponents::getInstance()->getRenderer(), imageLocation);

	if (_texture == NULL)
		cout << "Couldn't load image!" << endl;
}

Sprite::Sprite(Sprite &sprite)
{
	this->_texture = sprite.getTexture();
	this->_cropRect = sprite.getCropRect();
	this->_posnsizeRect = sprite.getPosnsizeRect();
}

Sprite::~Sprite()
{
	SDL_DestroyTexture(_texture);
}

void Sprite::draw()
{
	if(this->_cropRect.w == 0)
		SDL_RenderCopy(SdlComponents::getInstance()->getRenderer(), _texture, NULL, &_posnsizeRect);
	else
		SDL_RenderCopy(SdlComponents::getInstance()->getRenderer(), _texture, &_cropRect, &_posnsizeRect);
}

void Sprite::setupSprite(SDL_Texture * texture, SDL_Rect cropRect, SDL_Rect posnsizeRect)
{
	this->setTexture(texture);
	this->setCropRect(cropRect);
	this->setPosnsizeRect(posnsizeRect);
}

void Sprite::setPosition(Vec2 position)
{
	this->_position = position;
	_posnsizeRect.x = position.x;
	_posnsizeRect.y = position.y;
}

Vec2 Sprite::getPosition()
{
	return this->_position;
}

void Sprite::setTexture(SDL_Texture * texture)
{
	this->_texture = texture;
}

void Sprite::setCropRect(SDL_Rect cropRect)
{
	this->_cropRect = cropRect;
}

void Sprite::setPosnsizeRect(SDL_Rect posnsizeRect)
{
	this->_posnsizeRect = posnsizeRect;
}

SDL_Texture * Sprite::getTexture()
{
	return this->_texture;
}

SDL_Rect Sprite::getCropRect()
{
	return this->_cropRect;
}

SDL_Rect Sprite::getPosnsizeRect()
{
	return this->_posnsizeRect;
}
