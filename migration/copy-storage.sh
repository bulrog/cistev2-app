#!/bin/sh
# Copies every game zip from the old bucket to the new one (keeps the <userId>/<gameId>.zip layout).
# Usage: gcloud auth login   (personal account), then ./copy-storage.sh
set -e
gcloud storage ls -r gs://cistes2dot0.appspot.com
gcloud storage cp -r 'gs://cistes2dot0.appspot.com/*' gs://cistes2.firebasestorage.app/
gcloud storage ls -r gs://cistes2.firebasestorage.app
