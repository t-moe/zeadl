#!/bin/sh

INTERFACE=enp0s26u1u2

ifconfig $INTERFACE | grep -q -e "\b192\.168\.2\.1\b"
if [ $? -ne 0 ]; then
	sudo ifconfig enp0s26u1u2 192.168.2.1
fi

export ADBHOST=192.168.2.100
adb kill-server
adb start-server
adb devices
