#ifndef VEC2_H
#define VEC2_H

#pragma once

struct Vec2
{
	int x;
	int y;

	Vec2() { this->x = 0; this->y = 0; };
	Vec2(int x, int y) { this->x = x; this->y = y; };

	Vec2& operator=(const Vec2& vec2)
	{
		this->x = vec2.x;
		this->y = vec2.y;

		return *this;
	}

	Vec2 operator+(const Vec2& vec2) const
	{
		return Vec2(this->x + vec2.x, this->y + vec2.y);
	}

	Vec2 operator-(const Vec2& vec2) const
	{
		return Vec2(this->x - vec2.x, this->y - vec2.y);
	}

	bool operator==(const Vec2& otherVec)
	{ 
		return (this->x == otherVec.x && this->y == otherVec.y) ? true : false; 
	}
};

#endif
