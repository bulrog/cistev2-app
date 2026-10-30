// Copies the `games` collection from cistes2dot0 to cistes2.
// Usage: gcloud auth application-default login   (personal account)
//        npm install && node copy-firestore.js [--dry-run]
const admin = require('firebase-admin');

const dryRun = process.argv.includes('--dry-run');
const source = admin.initializeApp({projectId: 'cistes2dot0'}, 'source').firestore();
const target = admin.initializeApp({projectId: 'cistes2'}, 'target').firestore();

(async () => {
  const snapshot = await source.collection('games').get();
  console.log(`${snapshot.size} games found in cistes2dot0`);
  for (const doc of snapshot.docs) {
    console.log(`${dryRun ? '[dry-run] ' : ''}${doc.id} ${doc.get('name')} (userID ${doc.get('userID')})`);
    if (!dryRun) {
      await target.collection('games').doc(doc.id).set(doc.data());
    }
  }
})().catch(e => {
  console.error(e);
  process.exit(1);
});
