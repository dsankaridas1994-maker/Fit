const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("userProfiles").get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("userProfiles").doc(BOB_UID).set({
      userId: BOB_UID,
      name: "Bob",
      level: 1,
      xp: 100,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("userProfiles").doc(BOB_UID).get());
});

test("Authenticated user: can read and write their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("userProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice",
      level: 2,
      xp: 250,
    })
  );
  await assertSucceeds(aliceDb.collection("userProfiles").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's workout sessions", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("workoutSessions").doc("bob_session").set({
      userId: BOB_UID,
      workoutTitle: "Full Body Blast",
      durationSeconds: 900,
      caloriesBurned: 150,
      completedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("workoutSessions").doc("bob_session").get());
});

test("Authenticated user: can query own workout sessions", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("workoutSessions").doc("alice_session").set({
      userId: ALICE_UID,
      workoutTitle: "Morning Shred",
      durationSeconds: 600,
      caloriesBurned: 110,
      completedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("workoutSessions").where("userId", "==", ALICE_UID).get()
  );
});
