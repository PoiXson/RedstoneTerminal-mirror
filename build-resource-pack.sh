#!/usr/bin/bash


if [[ -f RedstoneTerminal-resourcepack.zip ]]; then
	\rm -fv  RedstoneTerminal-resourcepack.zip  || exit 1
fi


\pushd  "resourcepack/"  >/dev/null  || exit 1
	\zip -r -9  ../RedstoneTerminal-resourcepack.zip *  || exit 1
\popd >/dev/null


\sha1sum RedstoneTerminal-resourcepack.zip > RedstoneTerminal-resourcepack.sha1  ||


\cp  RedstoneTerminal-resourcepack.zip   plugin/resources/
\cp  RedstoneTerminal-resourcepack.sha1  plugin/resources/
