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

export const singleImport = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/singleImport.dmn" });
};

export const doubleImport = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/doubleImport.dmn" });
};

export const deepNested = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/deep/nested.dmn" });
};

export const deepRecursive = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/deep/recursive.dmn" });
};

export const example1 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example1.dmn" });
};

export const example2 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example2.dmn" });
};

export const example3 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example3.dmn" });
};

export const example4 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example4.dmn" });
};

export const example5 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example5.dmn" });
};

export const decisions = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/decisions.dmn" });
};

export const simple15 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5.dmn" });
};

export const simple152 = () => {
  return getModelXmlForTestFixtures({ normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5-2.dmn" });
};
