#include "stdafx.h"
#include "Game.h"
#include "Level.h"
#include "Tile.h"

Game::Game()
{
	SdlComponents::getInstance()->initSDL();
	freopen("CON", "w", stdout); // redirects stdout
	freopen("CON", "w", stderr); // redirects stderr

	this->_isGameOver = false;
	this->_last = 0;
	this->_deltaTime = 0.0f;

	this->_character = new Character("Danio", new Sprite("assets/characters.png", { 0, 0, 20, 20 }, { 0, 187, 16, 16 }));
	Tile::initTileSet();
	this->_level = new Level(40, 30, _character, "level1");
}

Game::~Game()
{
	delete this->_character;
	delete this->_level;
	SdlComponents::getInstance()->clearSDL();
}

void Game::startGame()
{
	while (!this->_isGameOver && SdlComponents::getInstance()->getMainEvent()->type != SDL_QUIT)
	{
		updateDeltaTime();
		SDL_PollEvent(SdlComponents::getInstance()->getMainEvent());
		SDL_RenderClear(SdlComponents::getInstance()->getRenderer());

		this->_level->drawAndUpdate(_deltaTime);
		this->_character->drawAndUpdate(_deltaTime);

		SDL_RenderPresent(SdlComponents::getInstance()->getRenderer());
	}
}

void Game::updateDeltaTime()
{
	long now = SDL_GetTicks();
	if (now > this->_last)
	{
		this->_deltaTime = ((float)(now - this->_last)) / 1000;
		this->_last = now;
	}
}
