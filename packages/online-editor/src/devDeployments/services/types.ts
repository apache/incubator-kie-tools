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

import { K8sResourceYaml, TokenMap } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { KubernetesService, KubernetesServiceArgs } from "./KubernetesService";
import { CloudAuthSessionType } from "../../authSessions/AuthSessionApi";
import { v4 as uuid } from "uuid";

export enum DeploymentState {
  UP = "UP",
  DOWN = "DOWN",
  IN_PROGRESS = "IN_PROGRESS",
  PREPARING = "PREPARING",
  ERROR = "ERROR",
}

export type KieSandboxDeployment = {
  name: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
  resources: K8sResourceYaml[];
  workspaceId: string;
  resourceName: string;
};

export interface DeployArgs {
  workspaceZipBlob: Blob;
  tokenMap: TokenMap;
}

export type ResourceArgs = {
  namespace: string;
  resourceName: string;
  createdBy: string;
};

export type KieSandboxDevDeploymentsServiceProps = {
  id: string;
  type: CloudAuthSessionType;
  args: KubernetesServiceArgs;
  deployments: KieSandboxDeployment[];
};

export type KieSandboxDevDeploymentsServiceType = KieSandboxDevDeploymentsServiceProps & {
  isConnectionEstablished(): Promise<KubernetesConnectionStatus>;
  loadDevDeployments(): Promise<KieSandboxDeployment[]>;
  deploy(args: DeployArgs): Promise<void>;
  deleteDevDeployment(resourceName: string): Promise<void>;
  uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void>;
};

export abstract class KieSandboxDevDeploymentsService implements KieSandboxDevDeploymentsServiceType {
  id: string;
  type = CloudAuthSessionType.OpenShift;
  deployments = [];

  constructor(readonly args: KubernetesServiceArgs, id?: string, deployments?: KieSandboxDeployment[]) {
    this.id = id ?? uuid();
  }

  get kubernetesService() {
    return new KubernetesService(this.args);
  }

  abstract isConnectionEstablished(): Promise<KubernetesConnectionStatus>;

  abstract loadDevDeployments(): Promise<KieSandboxDeployment[]>;

  abstract deploy(args: DeployArgs): Promise<void>;

  abstract deleteDevDeployment(resourceName: string): Promise<void>;

  abstract uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void>;
}

export type DevDeploymentTokens = {
  uniqueName: string;
  defaultContainerImageUrl: string;
};

export type WorkspaceTokens = {
  id: string;
  name: string;
  resourceName: string;
};

export type KubernetesTokens = {
  namespace: string;
};

export type UploadServiceTokens = {
  apiKey: string;
};

export type LabelTokens = {
  createdBy: string;
};

export type AnnotationTokens = {
  uri: string;
  workspaceId: string;
};

export const defaultLabelTokens: LabelTokens = {
  createdBy: "tools.kie.org/created-by",
} as const;

export const defaultAnnotationTokens: AnnotationTokens = {
  uri: "tools.kie.org/uri",
  workspaceId: "tools.kie.org/workspace-id",
} as const;

export const CREATED_BY_KIE_TOOLS = "kie-tools";

export const TOKENS_PREFIX = "devDeployment";

export type Tokens = DevDeploymentTokens & {
  workspace: WorkspaceTokens;
  kubernetes: KubernetesTokens;
  uploadService: UploadServiceTokens;
  labels: LabelTokens;
  annotations: AnnotationTokens;
};

export type TokensArg = Omit<Tokens, "labels" | "annotations"> & Partial<Tokens>;

export type ResourceMetadata = {
  annotations?: Record<string, string>;
  labels?: Record<string, string>;
  name: string;
  namespace: string;
  creationTimestamp: string;
  uid: string;
};

export type IngressResource = {
  metadata: ResourceMetadata;
  spec: {
    rules: {
      http: {
        paths: {
          backend: {
            service: {
              name: string;
              port: {
                number: number;
              };
            };
            path: string;
            pathType: string;
          };
        }[];
      };
    };
  };
  status: {
    loadBalancer: {
      ingress: {
        hostname: string;
      }[];
    };
  };
} & K8sResourceYaml;

export type ServiceResource = {
  metadata: ResourceMetadata;
  spec: any;
} & K8sResourceYaml;

export type DeploymentCondition = {
  type: string;
  status: string;
  reason: string;
  message: string;
  lastTransitionTime: string;
  lastUpdateTime: string;
};

export type DeploymentResource = {
  metadata: ResourceMetadata;
  spec: {
    replicas: number;
    selector: {
      matchLabels: string;
    };
    template: {
      spec: {
        containers: {
          env: { name: string; value: string }[];
          image: string;
          imagePullPolicy: string;
          name: string;
        }[];
      };
    };
  };
  status: {
    availableReplicas: number;
    readyReplicas: number;
    replicas: number;
    updatedReplicas: number;
    conditions: DeploymentCondition[];
  };
} & K8sResourceYaml;
