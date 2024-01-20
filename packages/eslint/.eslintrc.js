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

module.exports = {
  root: true,
  parser: "@typescript-eslint/parser",
  overrides: [
    {
      files: ["*.ts", "*.tsx", "*.jsx"],
      plugins: ["@typescript-eslint"],
      extends: [
        "eslint:recommended",
        "plugin:@typescript-eslint/recommended",
        "plugin:react/recommended",
        "plugin:react-hooks/recommended",
      ],
      rules: {
        "prefer-spread": "off",
        "@typescript-eslint/no-unused-vars": "off",
        "@typescript-eslint/no-explicit-any": "off",
        "@typescript-eslint/explicit-module-boundary-types": "off",
        "@typescript-eslint/no-non-null-assertion": "off",
        "@typescript-eslint/ban-types": "off",
        "@typescript-eslint/no-inferrable-types": "off",
        "@typescript-eslint/no-empty-interface": ["error", { allowSingleExtends: true }],
        "@typescript-eslint/no-empty-function": "off",
        "@typescript-eslint/no-unnecessary-type-constraint": "off",
        "no-fallthrough": "off",
        "no-case-declarations": "off",
        "react/prop-types": "off",
        "react/display-name": "off",
        "react/jsx-no-target-blank": "off",
      },
      settings: {
        react: {
          version: "detect",
        },
      },
      env: {
        browser: true,
        node: false,
      },
    },
    {
      files: ["**/*.js"],
      rules: {
        "@typescript-eslint/no-var-requires": 0,
      },
      extends: ["eslint:recommended"],
      env: {
        browser: false,
        node: true,
        amd: true,
      },
    },
    {
      files: ["**/tests/**/*.js"],
      extends: ["eslint:recommended"],
      env: {
        browser: false,
        node: true,
        amd: true,
        jest: true,
      },
    },
    {
      files: ["*.ts", "*.tsx"],
      rules: {
        "no-restricted-imports": [
          "error",
          {
            paths: [
              {
                name: "@patternfly/react-core",
                message: "Please use specific imports from @patternfly/react-core/dist/js/components/**)",
              },
              {
                name: "@patternfly/react-icons",
                message: "Please use specific imports from @patternfly/react-icons/dist/js/icons/**)",
              },
            ],
            patterns: ["!@patternfly/react-core/dist/*", "!@patternfly/react-icons/dist/*"],
          },
        ],
      },
    },
  ],
};
