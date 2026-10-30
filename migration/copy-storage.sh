#!/bin/sh
# Copies the game zips from the old bucket to the new one, under the new owner uid.
# Skips ede6a0d9-... (374 bytes, no matching game document).
# Usage: CLOUDSDK_CONFIG=$HOME/.gcloud-perso ./copy-storage.sh [--dry-run]
set -e
OLD_UID=7K8Jc8D6KBey8iRHSxx30YUbjYW2
NEW_UID=RDftpz1SXPXa7HFhHmuj3EwL6mV2
SRC=gs://cistes2dot0.appspot.com/$OLD_UID
DST=gs://cistes2.firebasestorage.app/$NEW_UID

for zip in $(gcloud storage ls "$SRC/*.zip" | grep -v ede6a0d9-2972-4690-90d5-322b9d6bdb95); do
  echo "$zip -> $DST/"
  [ "$1" = "--dry-run" ] || gcloud storage cp "$zip" "$DST/"
done
