export enum OpenshiftApiVersions {
  BUILD = "build.openshift.io/v1",
  BUILD_CONFIG = "build.openshift.io/v1",
  IMAGE_STREAM = "image.openshift.io/v1",
  PROJECT = "project.openshift.io/v1",
  ROUTE = "route.openshift.io/v1",
}

export const OpenShiftLabelNames = {
  RUNTIME: "app.openshift.io/runtime",
  VERSION: "app.openshift.io/runtime-version",
  TRIGGERS: "image.openshift.io/triggers",
};
