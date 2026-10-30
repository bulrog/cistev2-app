// Copies the `games` collection from cistes2dot0 to cistes2.
// The owner uid changes between the two projects (Firebase Auth is not migrated), so every
// game is re-attached to the new account.
// Usage: gcloud auth application-default login   (personal account)
//        npm install && node copy-firestore.js [--dry-run]
const admin = require('firebase-admin');

const OLD_UID = '7K8Jc8D6KBey8iRHSxx30YUbjYW2';
const NEW_UID = 'RDftpz1SXPXa7HFhHmuj3EwL6mV2';
// Test documents whose zip never existed in Storage.
const SKIP = new Set(['WvALZfVkPUcj1Ep0eNGF', 'rQluFjAHvo4TXP7SeACE']);

const dryRun = process.argv.includes('--dry-run');
const source = admin.initializeApp({projectId: 'cistes2dot0'}, 'source').firestore();
const target = admin.initializeApp({projectId: 'cistes2'}, 'target').firestore();

(async () => {
  const snapshot = await source.collection('games').get();
  console.log(`${snapshot.size} games found in cistes2dot0`);
  for (const doc of snapshot.docs) {
    if (SKIP.has(doc.id)) {
      console.log(`skip ${doc.id} ${doc.get('name')}`);
      continue;
    }
    if (doc.get('userID') !== OLD_UID) {
      throw new Error(`${doc.id} has an unexpected userID ${doc.get('userID')}`);
    }
    console.log(`${dryRun ? '[dry-run] ' : ''}copy ${doc.id} ${doc.get('name')} playable=${doc.get('playable')}`);
    if (!dryRun) {
      await target.collection('games').doc(doc.id).set({...doc.data(), userID: NEW_UID});
    }
  }
})().catch(e => {
  console.error(e);
  process.exit(1);
});
