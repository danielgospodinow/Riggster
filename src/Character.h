#ifndef CHARACTER_H
#define CHARACTER_H

#pragma once

#include "Sprite.h"

class Character
{
public:
	enum CharacterAction {MoveUp, MoveLeft, MoveDown, MoveRight};

	Character(string name, Sprite* sprite);
	~Character();
	void drawAndUpdate(float deltaTime);

	Vec2 getPosition();
	void setPosition(Vec2 position);

	void runAction(CharacterAction movement);

	bool canMove() { return this->_canMove; };
	void setCanMove(bool canMove) { this->_canMove = canMove; };

	void setDead(bool isDead) { this->_isDead = isDead; if (isDead) { _canMove = false; } };
	bool isDead() { return _isDead; };

private:
	string _name;
	Sprite* _characterSprite;

	bool _canMove;
	float _moveDelay;
	float _moveTimer;

	bool _isDead;
};

#endif

