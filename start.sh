#!/bin/sh

if [ -z $XDG_CURRENT_DESKTOP ]; then
  XDG_CURRENT_DESKTOP=RPIHome:GNOME
  export XDG_CURRENT_DESKTOP
fi

RPIHOME_HOME=/home/ivano/Progetti/rpihome/rpihome/rpihome/rpihome-wm
echo Using RPIHOME_HOME ${RPIHOME_HOME}

DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=n,suspend=n,address=8000"
#DEBUG=
java ${DEBUG} -cp "${RPIHOME_HOME}/libs/*:${RPIHOME_HOME}/target/classes/" com.github.ivano85.rpihome.wm.Main $* &> /var/log/rpihome.log
