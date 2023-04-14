const execSync = require("child_process").execSync;

const parameters = [
  { name: "Target", values: ["--target", "-t"] },
  { name: "Kind cluster name", values: ["--kind-cluster-name", "-kcn"] },
  { name: "Architecture", values: ["--arch", "-a"] },
];
const targetOptions = ["build-only", "kind", "minikube", "openshift"];

let target;
let kindClusterName;
let arch;

if (process.argv.indexOf("--help") > 0 || process.argv.indexOf("-h") > 0) {
  console.info(
    "This script helps you building new images to validate changes made to the dmn-dev-deployment-base-image and its dependencies."
  );
  console.info("* Docker is required for building *");
  console.info("Parameters:");
  console.info("  --target, -t:");
  console.info("      description: Where to create and load the built image.");
  console.info("      options:");
  console.info("        build-only: Builds the image locally and store it in your local Docker image registry.");
  console.info(
    "        kind: Builds the image locally and push it to your Kind cluster (the --kind-cluster-name parameter is required in this case)."
  );
  console.info("        minikube: Builds the image locally and push it to your Minikube cluster.");
  console.info(
    "        openshift: Builds the image directly on your OpenShift cluster and store it in an ImageStream (the 'oc' CLI tool needs to be installed and logged in to your cluster)."
  );
  console.info("  --arch, -a:");
  console.info(
    "      description: [Optional] The target build architecture. If not provided will default to the native architecture. (This parameter is ignored if targeting OpenShift as it will always build amd64)"
  );
  console.info("      options:");
  console.info("        arm64: ARM image, good for ARM Macs.");
  console.info("        amd64: x86_64 image, good for everything else.");
  console.info("  --kind-cluster-name, -knc:");
  console.info("      description: [Required if target = kind] Your Kind cluster name. Required to load images to it.");
  console.info("  --help, -h:");
  console.info("      description: Displays this help text.");
  console.info("");
  console.info("Examples of usage:");
  console.info("  - Building and loading image to a Kind cluster:");
  console.info("      pnpm create-test-image --target kind -kind-cluster-name kie-sandbox-dev-cluster");
  console.info("        or");
  console.info("      pnpm create-test-image -t kind -kcn kie-sandbox-dev-cluster");
  console.info("  - Building and loading and arm64 image to a Minikube cluster:");
  console.info("      pnpm create-test-image --target minikube --arch arm64");
  console.info("        or");
  console.info("      pnpm create-test-image -t minikube -a arm64");
  console.info("  - Creating an OpenShift build:");
  console.info("      pnpm create-test-image --target openshift");
  console.info("        or");
  console.info("      pnpm create-test-image -t openshift");
  console.info("  - Build only:");
  console.info("      pnpm create-test-image --target build-only");
  console.info("        or");
  console.info("      pnpm create-test-image -t build-only");
  console.info("  - Build only x86_64 image:");
  console.info("      pnpm create-test-image --target build-only --arch amd64");
  console.info("        or");
  console.info("      pnpm create-test-image -t build-only -a amd64");
  return;
}

function parseInputParameters(parameter) {
  let singleIndexParamter = false;
  let inputParameterName;
  let argIndex = parameter.values.reduce((acc, parameterName) => {
    const index = process.argv.indexOf(parameterName);
    const indexWithEqual = process.argv.indexOf(process.argv.find((arg) => arg.includes(parameterName + "=")));
    if (index >= 0) {
      singleIndexParamter = false;
      acc = index;
      inputParameterName = parameterName;
    } else if (indexWithEqual >= 0) {
      singleIndexParamter = true;
      acc = indexWithEqual;
      inputParameterName = parameterName;
    }
    return acc;
  }, -1);

  if (argIndex === -1) {
    throw new Error();
  }

  if (singleIndexParamter) {
    return process.argv[argIndex].replace(inputParameterName + "=", "").toLowerCase();
  }
  return process.argv[argIndex + 1].toLowerCase();
}

try {
  target = parseInputParameters(parameters[0]);

  if (!targetOptions.includes(target)) {
    console.error("Invalid target supplied. Please choose an option between: build-only | kind | minkube | openshift.");
    return;
  }
} catch (e) {
  console.error("Target not supplied. Please chose an option between: build-only, kind, minkube or openshift.");
  return;
}

if (target === "kind") {
  try {
    kindClusterName = parseInputParameters(parameters[1]);
  } catch (e) {
    console.error("Kind target selected, but no kind-cluster-name parameter was provided.");
    return;
  }
}

try {
  arch = parseInputParameters(parameters[2]);
  if (arch && !["arm64", "amd64"].includes(arch)) {
    console.error("Invalid architecture provided. Please choose and option between: arm64 | amd64");
    return;
  }
} catch (e) {
  // No op
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

const tag = execSync("build-env dmnDevDeploymentBaseImageEnv.testDevImageName | tr -d '\n'").toString();

function buildArchImage() {
  let platform = {
    arm64: "linux/arm64",
    amd64: "linux/amd64",
  }[arch];
  createAndUseDockerBuilder();
  execSync(
    `docker buildx build --platform ${platform} --load -t ${tag} --build-arg QUARKUS_PLATFORM_VERSION=$(build-env quarkusPlatform.version) --build-arg KOGITO_RUNTIME_VERSION=$(build-env kogitoRuntime.version) --build-arg ROOT_PATH=/ . -f Containerfile`,
    { stdio: "inherit" }
  );
}

function buildNativeImage() {
  execSync(
    `docker build -t ${tag} --build-arg QUARKUS_PLATFORM_VERSION=$(build-env quarkusPlatform.version) --build-arg KOGITO_RUNTIME_VERSION=$(build-env kogitoRuntime.version) --build-arg ROOT_PATH=/ . -f Containerfile`,
    { stdio: "inherit" }
  );
}

function buildImage() {
  if (arch) {
    buildArchImage();
  } else {
    buildNativeImage();
  }
}

function createOpenShiftImageStream() {
  const contents = `<<EOF
apiVersion: image.openshift.io/v1
kind: ImageStream
metadata:
  name: dmn-dev-deployment-base-image
spec:
  lookupPolicy:
    local: true
EOF
  `;

  execSync(`oc apply -f - ${contents}`, { stdio: "inherit" });
}

function createOpenShfitBuildConfig() {
  const quarkusPlatformVersion = execSync("build-env quarkusPlatform.version");
  const kogitoRuntimeVersion = execSync("build-env kogitoRuntime.version");
  const rootPath = "/";

  const contents = `<<EOF
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: dmn-dev-deployment-base-image
spec:
  output:
    to:
      kind: ImageStreamTag
      name: ${tag}
  strategy:
    dockerStrategy:
      dockerfilePath: Containerfile
      buildArgs:
        - name: QUARKUS_PLATFORM_VERSION
          value: ${quarkusPlatformVersion}
        - name: KOGITO_RUNTIME_VERSION
          value: ${kogitoRuntimeVersion}
        - name: ROOT_PATH
          value: ${rootPath}
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

function createOpenshiftBuild() {
  try {
    createOpenShiftImageStream();
    createOpenShfitBuildConfig();
  } catch (e) {
    console.error("-> Failed to create required resources. Are you logged in the 'oc' CLI?");
    return;
  }
  execSync("oc start-build --from-dir=. dmn-dev-deployment-base-image --follow", { stdio: "inherit" });
}

execSync("pnpm cleanup && pnpm copy:assets");

switch (target) {
  case "build-only":
    buildImage();
    break;
  case "kind":
    buildImage();
    console.info("-> Pushing image to Kind cluster internal registry...");
    execSync(`kind load docker-image ${tag} --name ${kindClusterName}`, { stdio: "inherit" });
    break;
  case "minikube":
    buildImage();
    console.info("-> Pushing image to Minikube cluster internal registry...");
    execSync(`minikube image load ${tag}`, { stdio: "inherit" });
    break;
  case "openshift":
    createOpenshiftBuild();
    break;
}

console.info("-> Done!");
