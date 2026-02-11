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

import { TokenMap } from "../../src/interpolateK8sResourceYaml";

type BaseTestCase = {
  name: string;
  given: {
    yaml: string;
    tokenMap: TokenMap;
  };
};

type TestCase = BaseTestCase & {
  expected: string;
};

type ErrorTestCase = BaseTestCase & {
  shouldThrow: true;
};

export const BASIC_TOKEN_TEST_CASES: TestCase[] = [
  {
    name: "should replace simple tokens",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
`,
      tokenMap: {
        serviceName: "my-service",
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: my-service
`,
  },
  {
    name: "should replace multiple tokens",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
  namespace: \${{ namespace }}
`,
      tokenMap: {
        serviceName: "my-service",
        namespace: "default",
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: my-service
  namespace: default
`,
  },
];

export const JSON_PATH_TEST_CASES: TestCase[] = [
  {
    name: "should resolve JSON Path expressions",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployment.name }}
`,
      tokenMap: {
        deployment: {
          name: "my-deployment",
        },
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: my-deployment
`,
  },
  {
    name: "should resolve nested JSON Path expressions",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployment.metadata.name }}
`,
      tokenMap: {
        deployment: {
          metadata: {
            name: "nested-deployment",
          },
        },
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: nested-deployment
`,
  },
  {
    name: "should resolve array access in JSON Path",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.deployments[0].name }}
`,
      tokenMap: {
        deployments: [{ name: "first-deployment" }, { name: "second-deployment" }],
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: first-deployment
`,
  },
];

export const RECURSIVE_TOKEN_TEST_CASES: TestCase[] = [
  {
    name: "should resolve nested tokens",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.resources.Route['\${{ $.uniqueName }}'].host }}
`,
      tokenMap: {
        uniqueName: "my-route",
        resources: {
          Route: {
            "my-route": {
              host: "my-route.example.com",
            },
          },
        },
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: my-route.example.com
`,
  },
  {
    name: "should resolve deeply nested tokens",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.resources['\${{ $.kind }}']['\${{ $.name }}'].host }}
`,
      tokenMap: {
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
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: deeply-nested.example.com
`,
  },
];

export const MAX_DEPTH_TEST_CASE: ErrorTestCase = {
  name: "should throw error when max depth is exceeded",
  given: {
    yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.a }}
`,
    tokenMap: {
      a: "${{ $.b }}",
      b: "${{ $.c }}",
      c: "${{ $.a }}",
    },
  },
  shouldThrow: true,
};

export const EDGE_CASE_TEST_CASES: TestCase[] = [
  {
    name: "should handle empty token map",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: static-name
  namespace: default
`,
      tokenMap: {},
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: static-name
  namespace: default
`,
  },
  {
    name: "should handle YAML without tokens",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: static-name
  namespace: default
`,
      tokenMap: {
        unused: "value",
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: static-name
  namespace: default
`,
  },
  {
    name: "should handle numeric values",
    given: {
      yaml: `
apiVersion: v1
kind: Service
spec:
  replicas: \${{ $.replicas }}
`,
      tokenMap: {
        replicas: 3,
      },
    },
    expected: `
apiVersion: v1
kind: Service
spec:
  replicas: 3
`,
  },
  {
    name: "should handle boolean values",
    given: {
      yaml: `
apiVersion: v1
kind: Service
spec:
  enabled: \${{ $.enabled }}
`,
      tokenMap: {
        enabled: true,
      },
    },
    expected: `
apiVersion: v1
kind: Service
spec:
  enabled: true
`,
  },
  {
    name: "should handle mixed flat and JSONPath tokens in same YAML",
    given: {
      yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ serviceName }}
  namespace: \${{ $.config.namespace }}
  labels:
    app: \${{ appLabel }}
    version: \${{ $.deployment.version }}
`,
      tokenMap: {
        serviceName: "mixed-service",
        appLabel: "my-app",
        config: {
          namespace: "production",
        },
        deployment: {
          version: "v1.2.3",
        },
      },
    },
    expected: `
apiVersion: v1
kind: Service
metadata:
  name: mixed-service
  namespace: production
  labels:
    app: my-app
    version: v1.2.3
`,
  },
];

export const UNRESOLVABLE_TOKEN_TEST_CASE: ErrorTestCase = {
  name: "should throw error for unresolvable tokens",
  given: {
    yaml: `
apiVersion: v1
kind: Service
metadata:
  name: \${{ $.nonexistent }}
`,
    tokenMap: {
      other: "value",
    },
  },
  shouldThrow: true,
};
