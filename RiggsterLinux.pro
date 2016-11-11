QT += core
QT -= gui

CONFIG += c++11

TARGET = RiggsterLinux
CONFIG += console
CONFIG -= app_bundle

TEMPLATE = app

SOURCES += Riggster.cpp \
        Character.cpp \
        Game.cpp \
        Level.cpp \
        SdlComponents.cpp  \
        Sprite.cpp \
        stdafx.cpp \
        Tile.cpp \
        tinyxml2.cpp \

HEADERS += \
    Character.h \
        Game.h \
        Level.h	 \
        SdlComponents.h \
        Sprite.h	 \
        stdafx.h \
        targetver.h \
        Tile.h \
        tinyxml2.h \
        Vec2.h \

LIBS += -lSDL2main \
    -lSDL2 \
    -lSDL2_mixer \
    -lSDL2_image \
    -lpthread \
