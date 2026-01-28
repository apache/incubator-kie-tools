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

import { Operation } from "fast-json-patch";

export const YAML_FIXTURES = {
  basicDeployment: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-app
`,

  deploymentWithLabels: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,

  deploymentWithNullLabels: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels: null
spec:
  replicas: 1
`,

  deploymentWithAnnotations: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  annotations:
    description: "My application"
spec:
  replicas: 1
`,

  serviceWithoutMetadata: `
apiVersion: v1
kind: Service
spec:
  ports:
    - port: 80
`,
};

export const JSON_PATCHES = {
  // Add operation patches
  addLabel: [
    {
      op: "add",
      path: "/metadata/labels/environment",
      value: "production",
    },
  ] as Operation[],

  addMultipleLabels: [
    {
      op: "add",
      path: "/metadata/labels/environment",
      value: "production",
    },
    {
      op: "add",
      path: "/metadata/labels/team",
      value: "platform",
    },
  ] as Operation[],

  addAnnotation: [
    {
      op: "add",
      path: "/metadata/annotations/version",
      value: "1.0.0",
    },
  ] as Operation[],

  // Replace operation patches
  replaceReplicas: [
    {
      op: "replace",
      path: "/spec/replicas",
      value: 3,
    },
  ] as Operation[],

  replaceName: [
    {
      op: "replace",
      path: "/metadata/name",
      value: "new-app-name",
    },
  ] as Operation[],

  // Remove operation patches
  removeLabel: [
    {
      op: "remove",
      path: "/metadata/labels/app",
    },
  ] as Operation[],

  removeAnnotation: [
    {
      op: "remove",
      path: "/metadata/annotations/description",
    },
  ] as Operation[],

  // Test operation patches
  testLabelExists: [
    {
      op: "test",
      path: "/metadata/labels",
      value: undefined,
    },
  ] as Operation[],

  testLabelIsNull: [
    {
      op: "test",
      path: "/metadata/labels",
      value: null,
    },
  ] as Operation[],

  testSpecificValue: [
    {
      op: "test",
      path: "/spec/replicas",
      value: 1,
    },
  ] as Operation[],
};

export const RESOURCE_PATCHES = {
  // Patches with test filters
  addLabelsIfUndefined: {
    testFilters: [
      {
        op: "test" as const,
        path: "/metadata/labels",
        value: undefined,
      },
    ],
    jsonPatches: [
      {
        op: "add" as const,
        path: "/metadata/labels",
        value: {},
      },
    ],
  },

  addLabelsIfNull: {
    testFilters: [
      {
        op: "test" as const,
        path: "/metadata/labels",
        value: null,
      },
    ],
    jsonPatches: [
      {
        op: "add" as const,
        path: "/metadata/labels",
        value: {},
      },
    ],
  },

  addAnnotationsIfUndefined: {
    testFilters: [
      {
        op: "test" as const,
        path: "/metadata/annotations",
        value: undefined,
      },
    ],
    jsonPatches: [
      {
        op: "add" as const,
        path: "/metadata/annotations",
        value: {},
      },
    ],
  },

  // Patches without test filters
  addEnvironmentLabel: {
    jsonPatches: [
      {
        op: "add" as const,
        path: "/metadata/labels/environment",
        value: "production",
      },
    ],
  },

  updateReplicas: {
    jsonPatches: [
      {
        op: "replace" as const,
        path: "/spec/replicas",
        value: 5,
      },
    ],
  },

  addLabelWithToken: {
    jsonPatches: [
      {
        op: "add" as const,
        path: "/metadata/labels/partOf",
        value: "${{ $.devDeployment.uniqueName }}",
      },
    ],
  },

  // Invalid patches for error handling tests
  invalidPath: {
    jsonPatches: [
      {
        op: "add" as const,
        path: "/nonexistent/path/to/field",
        value: "test",
      },
    ],
  },
};

export const TOKEN_MAPS = {
  devDeploymentUniqueName: {
    devDeployment: {
      uniqueName: "my-unique-name",
    },
  },
};

export const EXPECTED_RESULTS = {
  hasEnvironmentLabel: "environment: production",
  hasTeamLabel: "team: platform",
  hasVersionAnnotation: "version: 1.0.0",
  hasThreeReplicas: "replicas: 3",
  hasNewName: "name: new-app-name",
  hasEmptyLabels: "labels: {}",
  hasPartOfLabel: "partOf: my-unique-name",
  hasEmptyAnnotations: "annotations: {}",
  hasFiveReplicas: "replicas: 5",
  hasLabelsSection: "labels:",
  hasAppLabel: "app: my-app",
  hasDescriptionAnnotation: 'description: "My application"',
  hasMyAppName: "name: my-app",
  hasOneReplica: "replicas: 1",
};
