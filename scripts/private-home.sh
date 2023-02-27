#!/usr/bin/env sh
set -x
# Unshare must be done with call of the script :
# sudo unshare -m $SCRIPT

TMP="/tmp/private-tmp"
HOME="/home/j4m3s"

# Private /tmp
mkdir -p "$TMP"
chown j4m3s "$TMP"

mount -t tmpfs none "$TMP"

mkdir -p "$TMP"/{work,upper,mount}
chown j4m3s "$TMP"/{work,upper,mount}

# Home overlay
mount -t overlay overlay -o lowerdir="$HOME",upperdir="$TMP"/upper,workdir="$TMP"/work "$TMP"/mount

# "replace" home by overlay
mount --bind $TMP/mount $HOME

# privilege descalation
exec su -l j4m3s
