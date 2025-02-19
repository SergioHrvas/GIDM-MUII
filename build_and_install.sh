#!/bin/bash

# Compilar el proyecto en modo debug
./gradlew assembleDebug

# Instalar la APK en el emulador o dispositivo
./gradlew installDebug
