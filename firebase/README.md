# Firebase config

Project: `cistes2` (personal account, no organization).

- `firestore.rules`: collection `games`, one document per game.
- `storage.rules`: `<userId>/<gameId>.zip`.

Deploy with the Firebase CLI (`firebase deploy --only firestore:rules,storage`) or paste the
files in the console (Firestore > Rules, Storage > Rules).
