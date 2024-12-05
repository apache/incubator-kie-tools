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

import { getModelXmlForTestFixtures } from "./getModelXml";

export const dmn12A = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/a.dmn" });
};

export const dmn12B = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/bImportsA.dmn" });
};

export const dmn12C = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/cImportsB.dmn" });
};

export const dmn12D = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/dImportsAB.dmn" });
};

export const dmn12E = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/eImportsXB.dmn" });
};

export const dmn12X = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/xImportsY.dmn" });
};

export const dmn12Y = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/y.dmn" });
};

export const dmn15A = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/aImportsDmn12C.dmn",
  });
};

export const dmn15B = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/bImportsDmn12D.dmn",
  });
};

export const immediateRecursionA = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/aImportsB.dmn",
  });
};

export const immediateRecursionB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/bImportsA.dmn",
  });
};

export const threeLevelRecursionA = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/aImportsB.dmn",
  });
};

export const threeLevelRecursionB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/bImportsC.dmn",
  });
};

export const threeLevelRecursionC = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/cImportsA.dmn",
  });
};

export const sampleLoanDmnModel = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/refactor/sampleLoan.dmn",
  });
};

export const mathDmnModel = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/refactor/math.dmn",
  });
};

export const includeMathModelDmn = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/refactor/includeMathModel.dmn",
  });
};

export const decisions = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/decisions.dmn" });
};
