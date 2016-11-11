#ifndef GAME_H
#define GAME_H

#pragma once

#include "Level.h"
#include "Character.h"

class Game
{
public:
	Game();
	~Game();

	void startGame();

private:
	void updateDeltaTime();

	bool _isGameOver;
	long _last;
	float _deltaTime;

	Level* _level;
	Character* _character;
};

#endif
