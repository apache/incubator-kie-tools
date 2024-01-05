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
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/b.dmn" });
};

export const dmn12ImportsB = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/importsB.dmn" });
};

export const dmn12ImportsAB = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/importsAB.dmn" });
};

export const dmn15ImportsDmn12ImportsB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12ImportsB.dmn",
  });
};

export const dmn15ImportsDmn12ImportsAB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12ImportsAB.dmn",
  });
};

export const immediateRecursionA = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/a.dmn",
  });
};

export const immediateRecursionB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/b.dmn",
  });
};

export const threeLevelRecursionA = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/a.dmn",
  });
};

export const threeLevelRecursionB = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/b.dmn",
  });
};

export const threeLevelRecursionC = () => {
  return getModelXmlForTestFixtures({
    normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/c.dmn",
  });
};

export const decisions = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/decisions.dmn" });
};
