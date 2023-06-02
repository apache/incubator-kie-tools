/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

const parser = require("@babel/parser");
const traverse = require("@babel/traverse").default;
const generate = require("@babel/generator").default;

module.exports = function webpackLoader(content, map, meta) {
  const transformations = [
    { from: /@kie-tools\/uniforms-patternfly\/dist\/(esm|cjs)/g, to: `@kie-tools/uniforms-patternfly/src` },
    { from: /@kie-tools-core\/(.*)\/dist/g, to: `@kie-tools-core/$1/src` },
    { from: /@kie-tools\/(.*)\/dist/g, to: `@kie-tools/$1/src` },
    { from: /@kie-tools-examples\/(.*)\/dist/g, to: `@kie-tools-examples/$1/src` },
    { from: /@kie-tools-core\/([\w\d-]*)/g, to: `@kie-tools-core/$1/src` },
    { from: /@kie-tools\/([\w\d-]*)/g, to: `@kie-tools/$1/src` },
    { from: /@kie-tools-examples\/([\w\d-]*)/g, to: `@kie-tools-examples/$1/src` },
  ];

  const excludes = ["@kie-tools-core/monaco-editor", "@kie-tools/kie-editors-standalone"];
  const includes = ["@kie-tools-core", "@kie-tools"];

  const ast = parser.parse(content, { sourceType: "module", plugins: ["typescript", "jsx", "classProperties"] });
  traverse(ast, {
    ImportDeclaration: (path) => {
      const value = path.node.source.value;
      if (excludes.some((e) => value.includes(e))) {
        return;
      }

      if (!includes.some((i) => value.includes(i))) {
        return;
      }

      let newValue = value;
      for (const t of transformations) {
        newValue = newValue.replaceAll(t.from, t.to);
        if (value !== newValue) {
          break;
        }
      }

      path.node.source.value = newValue;

      global.replaced = global.replaced ?? new Set();
      if (!global.replaced.has(value)) {
        console.info(`${value} => ${newValue}`);
        global.replaced.add(value);
      }
    },
  });

  const output = generate(ast, {}, content);
  return output.code;
};
