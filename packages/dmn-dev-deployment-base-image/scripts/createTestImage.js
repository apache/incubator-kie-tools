const execSync = require("child_process").execSync;
const yargs = require("yargs");
const { hideBin } = require("yargs/helpers");
const fs = require("fs");

try {
  yargs(hideBin(process.argv))
    .version(false)
    .scriptName("")
    .usage("Usage: pnpm create-test-image <command> [options]")
    .example(
      "pnpm create-test-image minikube -t my-repo/my-image:my-tag",
      "Build and load an image to a Minikube cluster"
    )
    .example(
      "pnpm create-test-image kind --kind-cluster-name kie-sandbox-dev-cluster -t my-repo/my-image:my-tag",
      "Build and load an image to a Kind cluster name kie-sandbox-dev-cluster"
    )
    .example("pnpm create-test-image openshift -t my-repo/my-image:my-tag", "Build an image in a OpenShift cluster")
    .example(
      "pnpm create-test-image build-only -t quay.io/my-user/my-image-name:latest -f my/context/path/Containerfile -c my/context/path",
      "Create an image from the Containerfile and context path"
    )
    .wrap(Math.min(150, yargs.terminalWidth()))
    .options({
      t: {
        alias: "tag",
        demandOption: true,
        describe: "Name and optionally a tag in the 'name:tag' format",
        type: "string",
        nargs: 1,
      },
      f: {
        alias: "file",
        demandOption: false,
        default: "Containerfile",
        describe: "Dockerfile/Containerfile path",
        type: "string",
        normalize: true,
        nargs: 1,
        coerce: (arg) => {
          if (!fs.existsSync(arg)) {
            throw new Error(
              `ERROR! -f, --file: The provided Dockerfile/Containerfile was not found. Are you sure it exists? Provided path: ${arg}`
            );
          }
          return arg;
        },
      },
      c: {
        alias: "context",
        demandOption: false,
        default: ".",
        describe: "The path to be packaged with your built image",
        type: "string",
        normalize: true,
        nargs: 1,
        coerce: (arg) => {
          if (!fs.existsSync(arg)) {
            throw new Error(
              `ERROR! -c, --context: The provided Context path was not found. Are you sure it exists? Provided path: ${arg}`
            );
          }
          return arg;
        },
      },
      a: {
        alias: "arch",
        demandOption: false,
        describe:
          "The target build architecture. If not provided will default to the native architecture (This parameter is ignored if targeting OpenShift as it will always build amd64)",
        type: "string",
        nargs: 1,
        choices: ["amd64", "arm64"],
      },
      "kind-cluster-name": {
        demandOption: false,
        describe: "Your Kind cluster name. Used only when loading image into Kind cluster",
        type: "string",
        nargs: 1,
        default: "kind",
      },
      "build-arg": {
        demandOption: false,
        describe:
          "Build arg to be passed to the Docker builder in the format '<arg>=<value>' (Can be used multiple times)",
        type: "array",
        default: [],
        coerce: (args) => {
          const regex = new RegExp(/(.*=.*)+/);
          const results = args.map((arg) => regex.test(arg.toString().trim()));
          if (!results.every(Boolean)) {
            throw new Error(
              `ERROR! --build-arg: Invalid build argument supplied ("${args.join(
                " "
              )}"). Use the format 'var1=value1 var2=value2 ...'`
            );
          }
          return args;
        },
      },
    })
    .command({
      alias: "build-only",
      command: "build-only [options]",
      describe: "Builds the image locally and store it in your local Docker image registry",
      handler: (argv) => {
        console.info(`
  Building local image.
    - tag: ${argv.tag}
    - file: ${argv.file}
    - context: ${argv.context}
    - buildArg: ${argv.buildArg.join(" ")}
    - arch: ${argv.arch ?? "native"}
        `);

        buildImage(argv.tag, argv.file, argv.context, argv.buildArg, argv.arch);
      },
    })
    .command({
      alias: "kind",
      command: "kind [options]",
      describe: "Builds the image locally and push it to your Kind cluster",
      handler: (argv) => {
        console.info(`
  Building local image and loading it to Kind cluster.
    - tag: ${argv.tag}
    - file: ${argv.file}
    - context: ${argv.context}
    - buildArg: ${argv.buildArg?.join(" ") ?? " - "}
    - arch: ${argv.arch ?? "native"}
    - kindClusterName: ${argv.kindClusterName}
        `);

        const clusters = execSync("kind get clusters").toString().split("\n");
        if (!clusters.includes(argv.kindClusterName)) {
          throw new Error(
            `ERROR! --kind-cluster-name: Kind cluster named ${argv.kindClusterName} not found. Are you sure it's correct?`
          );
        }

        buildImage(argv.tag, argv.file, argv.context, argv.buildArg, argv.arch);
        execSync(`kind load docker-image ${argv.tag} --name ${argv.kindClusterName}`, { stdio: "inherit" });
      },
    })
    .command({
      alias: "minikube",
      command: "minikube [options]",
      describe: "Builds the image locally and push it to your Minikube cluster",
      handler: (argv) => {
        console.info(`
  Building local image and loading it to Minikube cluster.
    - tag: ${argv.tag}
    - file: ${argv.file}
    - context: ${argv.context}
    - buildArg: ${argv.buildArg?.join(" ") ?? " - "}
    - arch: ${argv.arch ?? "native"}
        `);

        buildImage(argv.tag, argv.file, argv.context, argv.buildArg, argv.arch);
        execSync(`minikube image load ${argv.tag}`, { stdio: "inherit" });
      },
    })
    .command({
      alias: "openshift",
      command: "openshift [options]",
      describe:
        "Builds the image directly on your OpenShift cluster and store it in an ImageStream (the 'oc' CLI tool needs to be installed and logged in to your cluster)",
      handler: (argv) => {
        console.info(`
  Building image into OpenShift cluster.
    - tag: ${argv.tag}
    - file: ${argv.file}
    - context: ${argv.context}
    - buildArg: ${argv.buildArg?.join(" ") ?? " - "}
    - arch: amd64
        `);

        createOpenshiftBuild(argv.tag, argv.file, argv.context, argv.buildArg);
      },
    })
    .demandCommand()
    .fail((message) => {
      prettyPrintError(message);
      yargs.exit(1);
    })
    .help("h")
    .alias("h", "help").argv;
} catch (e) {
  prettyPrintError(e);
  yargs.exit(1);
}

function prettyPrintError(error) {
  console.error("\x1b[31m%s\x1b[0m", error);
}

function createAndUseDockerBuilder() {
  try {
    console.info("-> Checking for existing kie-tools-builder...");
    execSync("docker buildx inspect kie-tools-builder", { stdio: "inherit" });
    console.info("-> kie-tools-builder found, using it.");
    execSync("docker buildx use kie-tools-builder", { stdio: "inherit" });
  } catch (e) {
    console.info("- kie-tools-builder not found, creating it.");
    execSync("docker buildx create --name kie-tools-builder --driver docker-container --bootstrap --use", {
      stdio: "inherit",
    });
  }
}

function buildArchImage(tag, file, context, buildArg, arch) {
  let platform = {
    arm64: "linux/arm64",
    amd64: "linux/amd64",
  }[arch];
  createAndUseDockerBuilder();
  console.log(buildArg);
  execSync(
    `docker buildx build --platform ${platform} --load -t ${tag} ${buildArg
      .map((arg) => `--build-arg ${arg} `)
      .join(" ")} ${context} -f ${file}`,
    { stdio: "inherit" }
  );
}

function buildNativeImage(tag, file, context, buildArg) {
  execSync(`docker build -t ${tag} ${buildArg.map((arg) => `--build-arg ${arg}`).join(" ")} ${context} -f ${file}`, {
    stdio: "inherit",
  });
}

function buildImage(tag, file, context, buildArg, arch) {
  if (arch) {
    buildArchImage(tag, file, context, buildArg, arch);
  } else {
    buildNativeImage(tag, file, context, buildArg);
  }
}

function createOpenShiftImageStream(repo) {
  const contents = `<<EOF
apiVersion: image.openshift.io/v1
kind: ImageStream
metadata:
  name: ${repo}
spec:
  lookupPolicy:
    local: true
EOF
  `;

  execSync(`oc apply -f - ${contents}`, { stdio: "inherit" });
}

function createOpenShfitBuildConfig(repo, repoWithReference, file, buildArg) {
  const contents = `<<EOF
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: ${repo}
spec:
  output:
    to:
      kind: ImageStreamTag
      name: ${repoWithReference}
  strategy:
    dockerStrategy:
      dockerfilePath: ${file}
      ${
        buildArg?.length > 0
          ? `buildArgs: ${buildArg
              .map((arg) => {
                const [key, value] = arg.split("=");
                return `
       - name: ${key}
         value: ${value}`;
              })
              .join("")}`
          : ""
      }
  source:
    type: Binary
    binary: {}
  resources:
    limits:
      memory: 4Gi
EOF
  `;

  execSync(`oc apply -f - ${contents}`, { stdio: "inherit" });
}

function createOpenshiftBuild(tag, file, context, buildArg) {
  const { repo, repoWithReference } = getImageDetailsFromFullUrl(tag);

  try {
    createOpenShiftImageStream(repo);
    createOpenShfitBuildConfig(repo, repoWithReference, file, buildArg);
  } catch (e) {
    throw new Error("-> Failed to create required resources. Are you logged in the 'oc' CLI?");
  }
  execSync(`oc start-build --from-dir=${context} ${repo} --follow`, { stdio: "inherit" });
}

function getImageDetailsFromFullUrl(fullUrl) {
  // This regex matches only the image name and tag (https://regex101.com/r/Cets3B/1)
  const [repoWithReference] = /[^/\n]+(?=[^/\n]*$)/.exec(fullUrl);
  const reference = repoWithReference.includes("@")
    ? repoWithReference.split("@").pop()
    : repoWithReference.split(":").pop();
  const repo = repoWithReference.replace(reference, "").slice(0, -1);

  return { repo, reference, repoWithReference };
}

console.info("-> Done!");
