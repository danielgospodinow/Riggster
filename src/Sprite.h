#ifndef SPRITE_H
#define SPRITE_H

#pragma once

#include "stdafx.h"
#include "Sprite.h"

class Sprite
{
public:
	Sprite(const char* imageLocation, SDL_Rect sizeRect);
	Sprite(const char* imageLocation, SDL_Rect sizeRect, SDL_Rect cropRect);
	Sprite(Sprite& sprite);
	~Sprite();

	Sprite& operator=(Sprite& sprite)
	{
		this->_texture = sprite.getTexture();
		this->_cropRect = sprite.getCropRect();
		this->_posnsizeRect = sprite.getPosnsizeRect();

		return *this;
	}

	void draw();

	void setupSprite(SDL_Texture* texture, SDL_Rect cropRect, SDL_Rect posnsizeRect);

	void setPosition(Vec2 position);
	void setTexture(SDL_Texture* texture);
	void setCropRect(SDL_Rect cropRect);
	void setPosnsizeRect(SDL_Rect posnsizeRect);

	SDL_Texture* getTexture();
	SDL_Rect getCropRect();
	SDL_Rect getPosnsizeRect();
	Vec2 getPosition();

private:
	SDL_Texture* _texture;
	SDL_Rect _cropRect;
	SDL_Rect _posnsizeRect;
	Vec2 _position;
};

#endif
