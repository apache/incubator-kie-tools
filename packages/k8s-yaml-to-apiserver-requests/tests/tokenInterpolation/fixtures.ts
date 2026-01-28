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

export const YAML_FIXTURES = {
  // Basic token interpolation fixtures
  simpleToken: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
`,

  multipleTokens: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
  namespace: \${{ namespace }}
`,

  // JSON Path token interpolation fixtures
  jsonPathSimple: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployment.name }}
`,

  jsonPathNested: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployment.metadata.name }}
`,

  jsonPathArray: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployments[0].name }}
`,

  // Recursive token interpolation fixtures
  recursiveSimple: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.resources.Route['\${{ $.uniqueName }}'].host }}
`,

  recursiveDeeplyNested: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.resources['\${{ $.kind }}']['\${{ $.name }}'].host }}
`,

  recursiveMaxDepth: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.a }}
`,

  // Edge case fixtures
  noTokens: `
apiVersion: v1
kind: Service
metadata:
  name: static-name
  namespace: default
`,

  unresolvableToken: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.nonexistent }}
`,

  numericValue: `
apiVersion: v1
kind: Service
spec:
  replicas: \${{ $.replicas }}
`,

  booleanValue: `
apiVersion: v1
kind: Service
spec:
  enabled: \${{ $.enabled }}
`,

  // Mixed flat and JSONPath tokens
  mixedTokens: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
  namespace: \${{ $.config.namespace }}
  labels:
    app: \${{ appLabel }}
    version: \${{ $.deployment.version }}
`,
};

export const TOKEN_MAPS = {
  // Basic token maps
  simpleService: {
    serviceName: "my-service",
  },

  multipleValues: {
    serviceName: "my-service",
    namespace: "default",
  },

  // JSON Path token maps
  deploymentSimple: {
    deployment: {
      name: "my-deployment",
    },
  },

  deploymentNested: {
    deployment: {
      metadata: {
        name: "nested-deployment",
      },
    },
  },

  deploymentsArray: {
    deployments: [{ name: "first-deployment" }, { name: "second-deployment" }],
  },

  // Recursive token maps
  routeResources: {
    uniqueName: "my-route",
    resources: {
      Route: {
        "my-route": {
          host: "my-route.example.com",
        },
      },
    },
  },

  dynamicResources: {
    kind: "Route",
    name: "my-route",
    resources: {
      Route: {
        "my-route": {
          host: "deeply-nested.example.com",
        },
      },
    },
  },

  circularReference: {
    a: "${{ $.b }}",
    b: "${{ $.c }}",
    c: "${{ $.a }}",
  },

  // Edge case token maps
  empty: {},

  unused: {
    unused: "value",
  },

  nonexistent: {
    other: "value",
  },

  numeric: {
    replicas: 3,
  },

  boolean: {
    enabled: true,
  },

  mixed: {
    serviceName: "mixed-service",
    appLabel: "my-app",
    config: {
      namespace: "production",
    },
    deployment: {
      version: "v1.2.3",
    },
  },
};

export const EXPECTED_RESULTS = {
  simpleService: "name: my-service",
  multipleTokensName: "name: my-service",
  multipleTokensNamespace: "namespace: default",
  jsonPathSimple: "name: my-deployment",
  jsonPathNested: "name: nested-deployment",
  jsonPathArray: "name: first-deployment",
  recursiveSimple: "name: my-route.example.com",
  recursiveDeeplyNested: "name: deeply-nested.example.com",
  noTokensName: "name: static-name",
  noTokensNamespace: "namespace: default",
  numericValue: "replicas: 3",
  booleanValue: "enabled: true",
  mixedTokensName: "name: mixed-service",
  mixedTokensNamespace: "namespace: production",
  mixedTokensApp: "app: my-app",
  mixedTokensVersion: "version: v1.2.3",
};
