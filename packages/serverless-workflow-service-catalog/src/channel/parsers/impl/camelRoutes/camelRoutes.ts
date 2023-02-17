/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionSource,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSource,
  SwfServiceCatalogServiceType,
} from "../../../../api";
import { convertSource } from "../convertSource";

export function parseCamelRoutes(
  args: {
    source: SwfServiceCatalogServiceSource;
    serviceFileName: string;
    serviceFileContent: string;
  },
  serviceCamelRoutesDocument: any
): SwfServiceCatalogService {
  const swfServiceCatalogFunctions = extractFunctions(serviceCamelRoutesDocument, convertSource(args.source));
  return {
    name: args.serviceFileName,
    type: SwfServiceCatalogServiceType.camelroutes,
    source: args.source,
    functions: swfServiceCatalogFunctions,
    rawContent: args.serviceFileContent,
  } as SwfServiceCatalogService;
}

export function extractFunctions(
  serviceCamelRoutesDocument: any,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions = serviceCamelRoutesDocument.map((routeItem: any) => {
    return extractRouteItemFunctions(routeItem, source);
  });
  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractRouteItemFunctions(
  routeItem: any,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];
  if (routeItem.from) {
    const routeFrom = routeItem.from as any;

    const name = `camel:${routeFrom.uri}` as string;

    const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {
      body: SwfServiceCatalogFunctionArgumentType.string,
      header: SwfServiceCatalogFunctionArgumentType.object,
    };

    const endpoint = routeFrom.uri.split(":")[0];

    if (endpoint !== "direct") {
      throw new Error(`'Only routes with direct endpoints are supported`);
    }

    const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
      source,
      name,
      type: SwfServiceCatalogFunctionType.custom,
      arguments: functionArguments,
    };
    swfServiceCatalogFunctions.push(swfServiceCatalogFunction);
  }

  return swfServiceCatalogFunctions;
}
