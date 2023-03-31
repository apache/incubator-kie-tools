export enum DeploymentState {
  UP = "UP",
  DOWN = "DOWN",
  IN_PROGRESS = "IN_PROGRESS",
  PREPARING = "PREPARING",
  ERROR = "ERROR",
}

export interface DeployedModel {
  resourceName: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
}
