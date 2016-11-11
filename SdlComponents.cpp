#include "SdlComponents.h"

SdlComponents* SdlComponents::instance = nullptr;

SdlComponents* SdlComponents::getInstance()
{
	if (instance == nullptr)
		instance = new SdlComponents();

	return instance;
}

void SdlComponents::initSDL()
{
	SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO);

	this->window = SDL_CreateWindow("Riggster Game", 100, 100, 800, 600, SDL_WINDOW_SHOWN);
	if (window == NULL)
		cout << "Widnow failed to create!" << endl;

	this->renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);
	this->mainEvent = new SDL_Event();
}

void SdlComponents::clearSDL()
{
	SDL_DestroyWindow(this->window);
	SDL_DestroyRenderer(this->renderer);
	delete this->mainEvent;
}

SDL_Renderer* SdlComponents::getRenderer()
{
	return this->renderer;
}

SDL_Window* SdlComponents::getWindow()
{
	return this->window;
}

SDL_Event* SdlComponents::getMainEvent()
{
	return this->mainEvent;
}

SDL_Keycode SdlComponents::checkInput()
{
	if (this->mainEvent->type == SDL_KEYDOWN)
		return this->mainEvent->key.keysym.sym;
	return -1;
}
