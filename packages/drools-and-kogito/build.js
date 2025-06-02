#!/bin/bash -el \n node

const execSync = require("child_process").execSync;
const fs = require("fs");
const path = require("path");
const execOpts = { stdio: "inherit" };
const { env } = require("./env");
const buildEnv = env;

///

const DIST_REPO = path.resolve("./dist/1st-party-m2/repository");

if (buildEnv.droolsAndKogito.skip) {
  console.log(`[drools-and-kogito] Skip is on. Exiting.`);
  console.log(`[drools-and-kogito] Done.`);
  process.exit(0);
}

if (!buildEnv.versions.kogito.endsWith("-local") && !buildEnv.droolsAndKogito.forceBuild) {
  console.log(`[drools-and-kogito] Detected a non-local version for Drools and Kogito.`);
  console.log(
    `[drools-and-kogito] Building will not occur, as this version is expected to be either present on the local Maven repository (E.g., ~/.m2), or published in some publicly available Maven repository so that it can be downloaded.`
  );
  console.log(`[drools-and-kogito] Done.`);
  process.exit(0);
}

const buildInfo = getBuildInfo();

console.log();

const buildInfoMatches =
  buildInfo?.kogitoVersion === buildEnv.versions.kogito &&
  buildInfo?.droolsRepoGitRef === buildEnv.droolsAndKogito.repos.drools.gitRef &&
  buildInfo?.optaplannerRepoGitRef === buildEnv.droolsAndKogito.repos.optaplanner.gitRef &&
  buildInfo?.kogitoRuntimesRepoGitRef === buildEnv.droolsAndKogito.repos.kogitoRuntimes.gitRef &&
  buildInfo?.kogitoAppsRepoGitRef === buildEnv.droolsAndKogito.repos.kogitoApps.gitRef;

const localM2DirExists = fs.existsSync("./dist/1st-party-m2/repository");
const forceBuild = buildEnv.droolsAndKogito.forceBuild;

console.log(`[drools-and-kogito] Local m2 exists: ${localM2DirExists}`);
console.log(`[drools-and-kogito] Build info matches: ${buildInfoMatches}`);
console.log(`[drools-and-kogito] Force build: ${forceBuild}`);

if (localM2DirExists && buildInfoMatches && !forceBuild) {
  console.log(`[drools-and-kogito] Nothing to do. Exiting.`);
  process.exit(0);
} else if (localM2DirExists) {
  console.log(`[drools-and-kogito] Cleaning up 'dist' and 'dist-tmp' directories...`);
  fs.rmSync("./dist", { recursive: true });
}

fs.mkdirSync("./dist", { recursive: true });

if (fs.existsSync("./dist-tmp")) {
  fs.rmSync("./dist-tmp", { recursive: true });
}
fs.mkdirSync("./dist-tmp", { recursive: true });

// cloning

console.log(`[drools-and-kogito] Cloning Drools...`);
execSync(
  `git clone $(build-env droolsAndKogito.repos.drools.url) --branch $(build-env root.streamName) --depth 50 ./dist-tmp/drools`,
  execOpts
);
execSync(`git checkout $(build-env droolsAndKogito.repos.drools.gitRef)`, {
  ...execOpts,
  cwd: "./dist-tmp/drools",
});

console.log(`[drools-and-kogito] Cloning OptaPlanner...`);
execSync(
  `git clone $(build-env droolsAndKogito.repos.optaplanner.url) --branch $(build-env root.streamName) --depth 50 ./dist-tmp/optaplanner`,
  execOpts
);
execSync(`git checkout $(build-env droolsAndKogito.repos.optaplanner.gitRef)`, {
  ...execOpts,
  cwd: "./dist-tmp/optaplanner",
});

console.log(`[drools-and-kogito] Cloning Kogito Runtimes...`);
execSync(
  `git clone $(build-env droolsAndKogito.repos.kogitoRuntimes.url) --branch $(build-env root.streamName) --depth 50 ./dist-tmp/kogito-runtimes`,
  execOpts
);
execSync(`git checkout $(build-env droolsAndKogito.repos.kogitoRuntimes.gitRef)`, {
  ...execOpts,
  cwd: "./dist-tmp/kogito-runtimes",
});

console.log(`[drools-and-kogito] Cloning Kogito Apps...`);
execSync(
  `git clone $(build-env droolsAndKogito.repos.kogitoApps.url) --branch $(build-env root.streamName) --depth 50 ./dist-tmp/kogito-apps`,
  execOpts
);
execSync(`git checkout $(build-env droolsAndKogito.repos.kogitoApps.gitRef)`, {
  ...execOpts,
  cwd: "./dist-tmp/kogito-apps",
});

// update versions
const streamsMavenVersion =
  buildEnv.root.streamName === "main"
    ? `999-SNAPSHOT` //
    : buildEnv.root.streamName.replace(".x", ".999-SNAPSHOT"); // 10.1.x becomes 10.1.999-SNAPSHOT

console.log(`[drools-and-kogito] Updating versions to ${streamsMavenVersion}...`);
execSync(
  `find . -name "pom.xml" -exec sed -i.bak 's/${streamsMavenVersion}/${buildEnv.versions.kogito}/g' {} \\; -exec rm {}.bak \\;`,
  {
    ...execOpts,
    cwd: "./dist-tmp",
  }
);

// patching
console.log(`[drools-and-kogito] Patching pom.xml files to remove Tests and Integration Tests modules...`);
removeMavenModule(`drools\\-test\\-coverage`);
removeMavenModule(`.*\\-integration\\-tests`);
removeMavenModule(`integration\\-tests`);
removeMavenModule(`.*\\-integration\\-test`);
removeMavenModule(`.*\\-integration\\-test\\-.*`);
removeMavenModule(`.*\\-integrationtests`);
removeMavenModule(`.*integration\\-tests\\-.*`);
removeMavenModule(`.*\\-integrationtest`);
removeMavenModule(`.*\\-it`);
removeMavenModule(`kie\\-archetypes`);
removeMavenModule(`apps\\-integration\\-tests`);

// building

console.log(`[drools-and-kogito] Building Drools...`);
execSync(
  `mvn deploy -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -DaltDeploymentRepository=snapshot-repo::default::file:${DIST_REPO}`,
  {
    ...execOpts,
    cwd: "./dist-tmp/drools",
  }
);

console.log(`[drools-and-kogito] Building OptaPlanner...`);
execSync(
  `mvn deploy -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${path.resolve("./dist/1st-party-m2/repository")} -DaltDeploymentRepository=snapshot-repo::default::file:${DIST_REPO}`,
  {
    ...execOpts,
    cwd: "./dist-tmp/optaplanner",
  }
);

console.log(`[drools-and-kogito] Building Kogito Runtimes...`);
execSync(
  `mvn deploy -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${path.resolve("./dist/1st-party-m2/repository")} -DaltDeploymentRepository=snapshot-repo::default::file:${DIST_REPO}`,
  {
    ...execOpts,
    cwd: "./dist-tmp/kogito-runtimes",
  }
);

console.log(`[drools-and-kogito] Building Kogito Apps...`);
execSync(
  `mvn deploy -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${path.resolve("./dist/1st-party-m2/repository")} -DaltDeploymentRepository=snapshot-repo::default::file:${DIST_REPO} -Dquarkus.container-image.build=false`,
  {
    ...execOpts,
    cwd: "./dist-tmp/kogito-apps",
  }
);

console.log(
  `[drools-and-kogito] Finished building. Final artifacts are in '${path.resolve("./dist/1st-party-m2/repository")}'`
);

console.log("[drools-and-kogito] Writing build info to ./dist/buildInfo.json");
fs.writeFileSync(
  "./dist/buildInfo.json",
  JSON.stringify({
    kogitoVersion: buildEnv.versions.kogito,
    droolsRepoGitRef: buildEnv.droolsAndKogito.repos.drools.gitRef,
    optaplannerRepoGitRef: buildEnv.droolsAndKogito.repos.optaplanner.gitRef,
    kogitoRuntimesRepoGitRef: buildEnv.droolsAndKogito.repos.kogitoRuntimes.gitRef,
    kogitoAppsRepoGitRef: buildEnv.droolsAndKogito.repos.kogitoApps.gitRef,
  }),
  "utf-8"
);

console.log(`[drools-and-kogito] Done.`);

//
// utilities

function removeMavenModule(moduleName) {
  process.stdout.write(`[drools-and-kogito] Excluding Maven module pattern ${moduleName} from the build...`);
  execSync(`find . -name "pom.xml" -exec sed -i.bak 's#<module>${moduleName}</module>##g' {} \\; -exec rm {}.bak \\;`, {
    ...execOpts,
    cwd: "./dist-tmp",
  });
  console.log(" Done.");
}

function getBuildInfo() {
  if (!fs.existsSync(`./dist/buildInfo.json`)) {
    return undefined;
  } else {
    return JSON.parse(fs.readFileSync(`./dist/buildInfo.json`), "utf-8");
  }
}
