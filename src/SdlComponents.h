#ifndef SDLCOMPONENTS_H
#define SDLCOMPONENTS_H

#pragma once

#include "stdafx.h"

class SdlComponents
{
public:
	static SdlComponents *getInstance();

	void initSDL();
	void clearSDL();

	SDL_Renderer* getRenderer();
	SDL_Window* getWindow();
	SDL_Event* getMainEvent();

	SDL_Keycode checkInput();

	SDL_Surface* crop_surface(SDL_Surface* sprite_sheet, int x, int y, int width, int height)
	{
		SDL_Surface* surface = SDL_CreateRGBSurface(sprite_sheet->flags, width, height, sprite_sheet->format->BitsPerPixel, sprite_sheet->format->Rmask, sprite_sheet->format->Gmask, sprite_sheet->format->Bmask, sprite_sheet->format->Amask);
		SDL_Rect rect = { x, y, width, height };
		SDL_BlitSurface(sprite_sheet, &rect, surface, 0);
		return surface;
	}

private:
    SdlComponents(){}
    SdlComponents(SdlComponents const&) {}
    SdlComponents operator=(SdlComponents const&) {}

	static SdlComponents* instance;

	SDL_Window* window;
	SDL_Renderer *renderer;
	SDL_Event *mainEvent;
};

#endif
