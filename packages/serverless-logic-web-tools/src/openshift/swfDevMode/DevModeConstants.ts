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

import { v4 as uuid } from "uuid";

export const ZIP_FILE_PART_KEY = "zipFile";
export const ZIP_FILE_NAME = "file.zip";

export const DEV_MODE_FEATURE_NAME = "Dev Mode for Serverless Workflow";

export interface UploadApiResponseError {
  error: string;
}

export interface UploadApiResponseSuccess {
  paths: string[];
}

export const resolveWebToolsId = () => {
  const webToolsId = localStorage.getItem(SWF_DEV_MODE_ID_KEY) ?? uuid();
  localStorage.setItem(SWF_DEV_MODE_ID_KEY, webToolsId);
  return webToolsId;
};

export const resolveDevModeResourceName = (webToolsId: string) => {
  const sanitizedVersion = process.env.WEBPACK_REPLACE__version!.replace(/\./g, "");
  return `dev-${webToolsId}-${sanitizedVersion}`;
};

export type DevModeUploadResult =
  | {
      success: true;
      uploadedPaths: string[];
    }
  | {
      success: false;
      message: string;
      sentPaths?: string[];
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

export const SWF_DEV_MODE_ID_KEY = "SWF_DEV_MODE_ID";
