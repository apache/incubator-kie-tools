/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const ZIP_FILE_PART_KEY = "zipFile";
export const ZIP_FILE_NAME = "file.zip";

export type DevModeUploadResult =
  | {
      success: true;
      uploadedPaths: string[];
    }
  | {
      success: false;
      message: string;
    };

export interface DevModeEndpoints {
  base: string;
  upload: string;
  quarkusDevUi: string;
  swfDevUi: string;
  swaggerUi: string;
  health: {
    live: string;
    ready: string;
    started: string;
  };
}

export const buildEndpoints = (routeUrl: string): DevModeEndpoints => ({
  base: routeUrl,
  upload: `${routeUrl}/upload`,
  quarkusDevUi: `${routeUrl}/q/dev`,
  swfDevUi: `${routeUrl}/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances`,
  swaggerUi: `${routeUrl}/q/swagger-ui`,
  health: {
    live: `${routeUrl}/q/health/live`,
    ready: `${routeUrl}/q/health/ready`,
    started: `${routeUrl}/q/health/started`,
  },
});

export const WEB_TOOLS_ID_KEY = "SERVERLESS_LOGIC_WEB_TOOLS_ID";
