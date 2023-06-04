#!/usr/bin/bash
VERSION="{{{VERSION}}}"


if [[ -z $VERSION ]] || [[ "$VERSION" == "\{\{\{VERSION\}\}\}" ]]; then
	VERSION=""
else
	VERSION="-${VERSION}"
fi


\ls "./RedstoneTerminal-resourcepack"*.zip >/dev/null 2>/dev/null
if [[ $? -eq 0 ]]; then
	\rm -fv --preserve-root  "./RedstoneTerminal-resourcepack"*.zip  || exit 1
fi
\ls "./RedstoneTerminal-resourcepack"*.sha1 >/dev/null 2>/dev/null
if [[ $? -eq 0 ]]; then
	\rm -fv --preserve-root  "./RedstoneTerminal-resourcepack"*.sha1  || exit 1
fi
if [[ -f "./plugin/resources/RedstoneTerminal-resourcepack.zip" ]]; then
	\rm -fv --preserve-root  "./plugin/resources/RedstoneTerminal-resourcepack.zip"  || exit 1
fi
if [[ -f "./plugin/resources/RedstoneTerminal-resourcepack.sha1" ]]; then
	\rm -fv --preserve-root  "./plugin/resources/RedstoneTerminal-resourcepack.sha1"  || exit 1
fi


\pushd  "resourcepack/"  >/dev/null  || exit 1
	\zip -r -9  "../RedstoneTerminal-resourcepack${VERSION}.zip"  *  || exit 1
\popd >/dev/null


\sha1sum  "RedstoneTerminal-resourcepack${VERSION}.zip" \
	> "RedstoneTerminal-resourcepack${VERSION}.sha1"  || exit 1


\cp  "RedstoneTerminal-resourcepack${VERSION}.zip"   "plugin/resources/RedstoneTerminal-resourcepack.zip"
\cp  "RedstoneTerminal-resourcepack${VERSION}.sha1"  "plugin/resources/RedstoneTerminal-resourcepack.sha1"
