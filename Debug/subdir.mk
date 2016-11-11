################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../Character.cpp \
../Game.cpp \
../Level.cpp \
../Riggster.cpp \
../SdlComponents.cpp \
../Sprite.cpp \
../Tile.cpp \
../stdafx.cpp \
../tinyxml2.cpp 

OBJS += \
./Character.o \
./Game.o \
./Level.o \
./Riggster.o \
./SdlComponents.o \
./Sprite.o \
./Tile.o \
./stdafx.o \
./tinyxml2.o 

CPP_DEPS += \
./Character.d \
./Game.d \
./Level.d \
./Riggster.d \
./SdlComponents.d \
./Sprite.d \
./Tile.d \
./stdafx.d \
./tinyxml2.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cross G++ Compiler'
	g++ -ISDL2main -ISDL2 -ISDL2_image -ISDL2_mixer -Ipthread -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


