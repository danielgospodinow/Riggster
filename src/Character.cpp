#include "stdafx.h"
#include "Character.h"

Character::Character(string name, Sprite* sprite) :
	_name(name),
	_characterSprite(sprite)
{
	_moveDelay = 0.0f;
	_moveTimer = 0;
	_canMove = true;
	_isDead = false;
}

Character::~Character()
{
	delete _characterSprite;
}

Vec2 Character::getPosition()
{
	return this->_characterSprite->getPosition();
}

void Character::setPosition(Vec2 position)
{
	this->_characterSprite->setPosition(position);
}

void Character::drawAndUpdate(float deltaTime)
{
	if (_isDead)
	{
		this->setPosition(Vec2(20,20));
		_isDead = !_isDead;

		return;
	}

	_moveTimer += 1 * deltaTime;
	if (_moveTimer > _moveDelay)
		_canMove = true;

	this->_characterSprite->draw();
}

void Character::runAction(CharacterAction movement)
{
	if(!_canMove)
		return;

	switch (movement)
	{
	case MoveUp: this->setPosition(this->getPosition() + Vec2(0, -20)); break;
	case MoveLeft: this->setPosition(this->getPosition() + Vec2(-20, 0));  break;
	case MoveDown: this->setPosition(this->getPosition() + Vec2(0, 20));  break;
	case MoveRight: this->setPosition(this->getPosition() + Vec2(20, 0));  break;
	}

	_canMove = false;
	_moveTimer = 0;
}
