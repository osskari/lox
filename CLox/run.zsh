#!/bin/zsh

echo "---- BUILD -----------"
echo ""
make

echo ""
echo "---- RUN -------------"
echo ""
./build/lox
