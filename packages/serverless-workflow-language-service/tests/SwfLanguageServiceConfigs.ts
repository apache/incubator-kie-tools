/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { SwfLanguageServiceConfig } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export const testRelativeFunction1: SwfServiceCatalogFunction = {
  name: "testRelativeFunction1",
  type: SwfServiceCatalogFunctionType.rest,
  source: {
    type: SwfServiceCatalogFunctionSourceType.LOCAL_FS,
    serviceFileAbsolutePath: "/Users/tiago/Desktop/testRelativeService1.yml",
  },
  arguments: {
    argString: SwfServiceCatalogFunctionArgumentType.string,
    argNumber: SwfServiceCatalogFunctionArgumentType.number,
    argBoolean: SwfServiceCatalogFunctionArgumentType.boolean,
  },
};

export const testRelativeService1: SwfServiceCatalogService = {
  name: "testRelativeService1",
  source: {
    type: SwfServiceCatalogServiceSourceType.LOCAL_FS,
    absoluteFilePath: "/Users/tiago/Desktop/testRelativeService1.yml",
  },
  type: SwfServiceCatalogServiceType.rest,
  rawContent: "",
  functions: [testRelativeFunction1],
};

export const defaultServiceCatalogConfig = {
  relative: { getServices: async () => [] },
  global: { getServices: async () => [] },
  getServiceFileNameFromSwfServiceCatalogServiceId: async (registryName: string, serviceId: string) =>
    `${serviceId}.yaml`,
};

export const defaultConfig: SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => false,
  shouldServiceRegistriesLogIn: () => false,
  canRefreshServices: () => false,
  getSpecsDirPosixPaths: async () => ({ specsDirRelativePosixPath: "specs", specsDirAbsolutePosixPath: "" }),
  shouldDisplayServiceRegistriesIntegration: async () => true,
  shouldReferenceServiceRegistryFunctionsWithUrls: async () => false,
  shouldIncludeJsonSchemaDiagnostics: async () => true,
};
