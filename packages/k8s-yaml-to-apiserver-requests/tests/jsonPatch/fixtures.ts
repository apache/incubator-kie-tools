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
import { ResourcePatch } from "../../src/patchK8sResourceYaml";

type BaseTestCase = {
  name: string;
  given: {
    yaml: string;
    patches: ResourcePatch[];
    tokenMap?: TokenMap;
  };
};

type TestCase = BaseTestCase & {
  expected: string | string[];
};

type NegativeTestCase = BaseTestCase & {
  notExpected: string | string[];
};

// ADD OPERATION TEST CASES

export const ADD_OPERATION_TEST_CASES: TestCase[] = [
  {
    name: "should add a label to existing labels",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels/environment",
              value: "production",
            },
          ],
        },
      ],
    },
    expected: "environment: production",
  },
  {
    name: "should add multiple labels",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
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
          ],
        },
      ],
    },
    expected: ["environment: production", "team: platform"],
  },
  {
    name: "should add an annotation to existing annotations",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  annotations:
    description: "My application"
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/annotations/version",
              value: "1.0.0",
            },
          ],
        },
      ],
    },
    expected: "version: 1.0.0",
  },
];

// REPLACE OPERATION TEST CASES

export const REPLACE_OPERATION_TEST_CASES: TestCase[] = [
  {
    name: "should replace replicas value",
    given: {
      yaml: `
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
      patches: [
        {
          jsonPatches: [
            {
              op: "replace",
              path: "/spec/replicas",
              value: 3,
            },
          ],
        },
      ],
    },
    expected: "replicas: 3",
  },
  {
    name: "should replace metadata name",
    given: {
      yaml: `
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
      patches: [
        {
          jsonPatches: [
            {
              op: "replace",
              path: "/metadata/name",
              value: "new-app-name",
            },
          ],
        },
      ],
    },
    expected: "name: new-app-name",
  },
];

// REMOVE OPERATION TEST CASES

export const REMOVE_OPERATION_TEST_CASES: NegativeTestCase[] = [
  {
    name: "should remove a label",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "remove",
              path: "/metadata/labels/app",
            },
          ],
        },
      ],
    },
    notExpected: "app: my-app",
  },
  {
    name: "should remove an annotation",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  annotations:
    description: "My application"
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "remove",
              path: "/metadata/annotations/description",
            },
          ],
        },
      ],
    },
    notExpected: 'description: "My application"',
  },
];

// TEST OPERATION WITH RESOURCE PATCH TEST CASES

export const TEST_OPERATION_TEST_CASES: TestCase[] = [
  {
    name: "should apply patch when labels are undefined",
    given: {
      yaml: `
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
      patches: [
        {
          testFilters: [
            {
              op: "test",
              path: "/metadata/labels",
              value: undefined,
            },
          ],
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels",
              value: {},
            },
          ],
        },
      ],
    },
    expected: "labels: {}",
  },
  {
    name: "should apply patch when labels are null",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels: null
spec:
  replicas: 1
`,
      patches: [
        {
          testFilters: [
            {
              op: "test",
              path: "/metadata/labels",
              value: null,
            },
          ],
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels",
              value: {},
            },
          ],
        },
      ],
    },
    expected: "labels: {}",
  },
  {
    name: "should not apply patch when test filter fails",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          testFilters: [
            {
              op: "test",
              path: "/metadata/labels",
              value: undefined,
            },
          ],
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels",
              value: {},
            },
          ],
        },
      ],
    },
    expected: "app: my-app",
  },
  {
    name: "should apply patch without test filters",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels/environment",
              value: "production",
            },
          ],
        },
      ],
    },
    expected: "environment: production",
  },
];

// MULTIPLE RESOURCE PATCHES TEST CASES

export const MULTIPLE_PATCHES_TEST_CASES: TestCase[] = [
  {
    name: "should apply multiple resource patches in order",
    given: {
      yaml: `
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
      patches: [
        {
          testFilters: [
            {
              op: "test",
              path: "/metadata/labels",
              value: undefined,
            },
          ],
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels",
              value: {},
            },
          ],
        },
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels/environment",
              value: "production",
            },
          ],
        },
        {
          jsonPatches: [
            {
              op: "replace",
              path: "/spec/replicas",
              value: 5,
            },
          ],
        },
      ],
    },
    expected: ["environment: production", "replicas: 5", "labels:"],
  },
  {
    name: "should skip patches when test filters fail",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          testFilters: [
            {
              op: "test",
              path: "/metadata/labels",
              value: undefined,
            },
          ],
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels",
              value: {},
            },
          ],
        },
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels/environment",
              value: "production",
            },
          ],
        },
      ],
    },
    expected: ["app: my-app", "environment: production"],
  },
];

// EDGE CASE TEST CASES

export const EDGE_CASE_TEST_CASES: TestCase[] = [
  {
    name: "should handle empty patches array",
    given: {
      yaml: `
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
      patches: [],
    },
    expected: ["name: my-app", "replicas: 1"],
  },
  {
    name: "should silently skip invalid patch paths",
    given: {
      yaml: `
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
      patches: [
        {
          jsonPatches: [
            {
              op: "add",
              path: "/nonexistent/path/to/field",
              value: "test",
            },
          ],
        },
      ],
    },
    expected: ["name: my-app", "replicas: 1"],
  },
];

// TOKEN INTERPOLATION TEST CASES

export const TOKEN_INTERPOLATION_TEST_CASES: TestCase[] = [
  {
    name: "should interpolate tokens in patch values",
    given: {
      yaml: `
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 1
`,
      patches: [
        {
          jsonPatches: [
            {
              op: "add",
              path: "/metadata/labels/partOf",
              value: "${{ $.devDeployment.uniqueName }}",
            },
          ],
        },
      ],
      tokenMap: {
        devDeployment: {
          uniqueName: "my-unique-name",
        },
      },
    },
    expected: "partOf: my-unique-name",
  },
];
