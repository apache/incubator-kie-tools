/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const execSync = require("child_process").execSync;
const fs = require("fs");
const path = require("path");
const { env } = require("./env");
const buildEnv = env;
const execOpts = { stdio: "inherit" };

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

console.log(`[drools-and-kogito] Build info:`);
console.log(JSON.stringify(buildInfo, null, 2));

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
  console.log(`[drools-and-kogito] Nothing to do.`);
  console.log(`[drools-and-kogito] Done.`);
  process.exit(0);
} else if (localM2DirExists) {
  console.log(`[drools-and-kogito] Cleaning up 'dist' directory...`);
  fs.rmSync("./dist", { recursive: true });
}
fs.mkdirSync("./dist", { recursive: true });

// TODO: cache restoration

console.log(`[drools-and-kogito] Cleaning up 'dist' directory...`);
if (fs.existsSync("./dist-tmp")) {
  fs.rmSync("./dist-tmp", { recursive: true });
}
fs.mkdirSync("./dist-tmp", { recursive: true });

// cloning

const droolsRepoDir = path.join("dist-tmp", "drools");
const optaplannerRepoDir = path.join("dist-tmp", "optaplanner");
const kogitoRuntimesRepoDir = path.join("dist-tmp", "kogito-runtimes");
const kogitoAppsRepoDir = path.join("dist-tmp", "kogito-apps");

console.log(`[drools-and-kogito] Cloning Drools...`);

execSync(
  `git clone --branch ${buildEnv.root.streamName} --depth 50 ${buildEnv.droolsAndKogito.repos.drools.url} "${droolsRepoDir}"`,
  execOpts
);
execSync(`git checkout ${buildEnv.droolsAndKogito.repos.drools.gitRef}`, {
  ...execOpts,
  cwd: droolsRepoDir,
});

console.log(`[drools-and-kogito] Cloning OptaPlanner...`);
execSync(
  `git clone --branch ${buildEnv.root.streamName} --depth 50 ${buildEnv.droolsAndKogito.repos.optaplanner.url} "${optaplannerRepoDir}"`,
  execOpts
);
execSync(`git checkout ${buildEnv.droolsAndKogito.repos.optaplanner.gitRef}`, {
  ...execOpts,
  cwd: optaplannerRepoDir,
});

console.log(`[drools-and-kogito] Cloning Kogito Runtimes...`);
execSync(
  `git clone --branch ${buildEnv.root.streamName} --depth 50 ${buildEnv.droolsAndKogito.repos.kogitoRuntimes.url} "${kogitoRuntimesRepoDir}"`,
  execOpts
);
execSync(`git checkout ${buildEnv.droolsAndKogito.repos.kogitoRuntimes.gitRef}`, {
  ...execOpts,
  cwd: kogitoRuntimesRepoDir,
});

console.log(`[drools-and-kogito] Cloning Kogito Apps...`);
execSync(
  `git clone --branch ${buildEnv.root.streamName} --depth 50 ${buildEnv.droolsAndKogito.repos.kogitoApps.url} "${kogitoAppsRepoDir}"`,
  execOpts
);
execSync(`git checkout ${buildEnv.droolsAndKogito.repos.kogitoApps.gitRef}`, {
  ...execOpts,
  cwd: kogitoAppsRepoDir,
});

// update versions
const streamsMavenVersion =
  buildEnv.root.streamName === "main"
    ? `999-SNAPSHOT` //
    : buildEnv.root.streamName.replace(".x", ".999-SNAPSHOT"); // 10.1.x becomes 10.1.999-SNAPSHOT

console.log(`[drools-and-kogito] Updating versions to ${streamsMavenVersion}...`);
execSync(`find . -name "pom.xml" -exec sed -i.bak "s/${streamsMavenVersion}/${buildEnv.versions.kogito}/g" {} ";"`, {
  ...execOpts,
  cwd: "./dist-tmp",
});
execSync(`find . -name "pom.xml.bak" -exec rm {} ";"`, {
  ...execOpts,
  cwd: "./dist-tmp",
});

// patching
console.log(`[drools-and-kogito] Patching pom.xml files to remove Tests and Integration Tests modules...`);
removeMavenModule(`drools\\-test\\-coverage`);
removeMavenModule(`.*integration\\-tests`);
removeMavenModule(`integration\\-tests`);
removeMavenModule(`.*integration\\-test`);
removeMavenModule(`.*\\-integration\\-test\\-.*`);
removeMavenModule(`.*\\-integrationtests`);
removeMavenModule(`.*integration\\-tests\\-.*`);
removeMavenModule(`.*\\-integrationtest`);
removeMavenModule(`.*\\-it`);
removeMavenModule(`.*\\-live\\-reload\\-test`);
removeMavenModule(`kie\\-archetypes`);
removeMavenModule(`apps\\-integration\\-tests`);
removeMavenModule(`apps\\-integration\\-tests`);

// building

console.log(`[drools-and-kogito] Building Drools...`);
try {
  execSync(
    `mvn deploy -ntp -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -DaltDeploymentRepository=drools-and-kogito--dist-1st-party-m2::default::file:${DIST_REPO}`,
    {
      ...execOpts,
      cwd: droolsRepoDir,
    }
  );
} catch (e) {
  // Try it again!
  execSync(
    `mvn deploy -ntp -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -DaltDeploymentRepository=drools-and-kogito--dist-1st-party-m2::default::file:${DIST_REPO}`,
    {
      ...execOpts,
      cwd: droolsRepoDir,
    }
  );
}

console.log(`[drools-and-kogito] Building OptaPlanner...`);
execSync(
  `mvn deploy -ntp -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${DIST_REPO} -DaltDeploymentRepository=drools-and-kogito--dist-1st-party-m2::default::file:${DIST_REPO}`,
  {
    ...execOpts,
    cwd: optaplannerRepoDir,
  }
);

console.log(`[drools-and-kogito] Building Kogito Runtimes...`);
execSync(
  `mvn deploy -ntp -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${DIST_REPO} -DaltDeploymentRepository=drools-and-kogito--dist-1st-party-m2::default::file:${DIST_REPO}`,
  {
    ...execOpts,
    cwd: kogitoRuntimesRepoDir,
  }
);

console.log(`[drools-and-kogito] Building Kogito Apps...`);
execSync(
  `mvn deploy -ntp -DskipTests -DskipITs -T 0.5C -Dformatter.skip -Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.install.skip=true -Dmaven.repo.local.tail=${DIST_REPO} -DaltDeploymentRepository=snapshot-repo::default::file:${DIST_REPO} -Dquarkus.container-image.build=false`,
  {
    ...execOpts,
    cwd: kogitoAppsRepoDir,
  }
);

console.log("[drools-and-kogito] Removing source code directory to free up disk space... ");
fs.rmSync("./dist-tmp", { recursive: true });

console.log(`[drools-and-kogito] Finished building. Final artifacts are in '${DIST_REPO}'`);

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
  execSync(`find . -name "pom.xml" -exec sed -i.bak "s#<module>${moduleName}</module>##g" {} ";"`, {
    ...execOpts,
    cwd: "./dist-tmp",
  });
  execSync(`find . -name "pom.xml.bak" -exec rm {} ";"`, {
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
