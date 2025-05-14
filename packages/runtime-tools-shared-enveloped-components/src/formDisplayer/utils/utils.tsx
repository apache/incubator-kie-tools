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

export const sourceHandler = (
  source: string
):
  | {
      reactElements: string;
      patternflyElements: string;
      patternflyIconElements: string | undefined;
      patternflyDeprecatedElements: string | undefined;
      formName: string;
      trimmedSource: string;
    }
  | undefined => {
  const importsReg = /\{([^)]+)\}/;
  const reactImportsRegExp = /import React, {[^}]*}.*(?=["']react['"]).*/gim;

  const reactImports = new RegExp(reactImportsRegExp).exec(source);
  if (!reactImports) {
    return;
  }
  const reactElements = importsReg.exec(reactImports[0])?.[1];

  const patternflyImportsRegExp = /import {[^}]*}.*(?=['"]@patternfly\/react-core['"]).*/gim;
  const patternflyImports = new RegExp(patternflyImportsRegExp).exec(source);
  if (!patternflyImports) {
    return;
  }
  const patternflyElements = importsReg.exec(patternflyImports[0])?.[1];

  const patternflyIconImportsRegExp = /import {[^}]*}.*(?=['"]@patternfly\/react-icons['"]).*/gim;
  const patternflyIconImports = new RegExp(patternflyIconImportsRegExp).exec(source);
  const patternflyIconElements = importsReg.exec(patternflyIconImports?.[0] ?? "")?.[1];

  const patternflyDeprecatedImportsRegExp = /import {[^}]*}.*(?=['"]@patternfly\/react-core\/deprecated['"]).*/gim;
  const patternflyDeprecatedImports = new RegExp(patternflyDeprecatedImportsRegExp).exec(source);
  const patternflyDeprecatedElements = importsReg.exec(patternflyDeprecatedImports?.[0] ?? "")?.[1];

  const trimmedSource = source
    .split(reactImportsRegExp)
    .join("")
    .trim()
    .split(patternflyImportsRegExp)
    .join("")
    .trim()
    .split(patternflyIconImportsRegExp)
    .join("")
    .trim()
    .split(patternflyDeprecatedImportsRegExp)
    .join("")
    .trim();
  const formName = trimmedSource.split(": React.FC")[0].split("const ")[1];

  return {
    reactElements: reactElements!,
    patternflyElements: patternflyElements!,
    patternflyIconElements: patternflyIconElements,
    patternflyDeprecatedElements: patternflyDeprecatedElements,
    formName,
    trimmedSource,
  };
};
