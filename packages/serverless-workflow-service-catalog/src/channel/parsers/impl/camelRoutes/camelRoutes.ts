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
import * as CamelRoutes from "./types";

export function parseCamelRoutes(
  args: {
    source: SwfServiceCatalogServiceSource;
    serviceFileName: string;
    serviceFileContent: string;
  },
  serviceCamelRoutesDocument: CamelRoutes.CamelRouteDocument
): SwfServiceCatalogService {
  const swfServiceCatalogFunctions = extractFunctions(serviceCamelRoutesDocument, convertSource(args.source));
  const serviceName = args.serviceFileName.split(".")[0];
  return {
    name: serviceName,
    type: SwfServiceCatalogServiceType.camelroute,
    source: args.source,
    functions: swfServiceCatalogFunctions,
    rawContent: args.serviceFileContent,
  } as SwfServiceCatalogService;
}

export function extractFunctions(
  serviceCamelRoutesDocument: CamelRoutes.CamelRouteDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions = serviceCamelRoutesDocument.map((routeItem: CamelRoutes.RouteItemType) => {
    return extractRouteItemFunctions(routeItem, source);
  });
  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractRouteItemFunctions(
  routeItem: CamelRoutes.RouteItemType,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];

  let routeFrom;
  if ("from" in routeItem) {
    routeFrom = routeItem.from;
  } else if ("route" in routeItem) {
    routeFrom = routeItem.route?.from;
  }

  const name = `camel:${routeFrom?.uri}`;

  const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {
    body: SwfServiceCatalogFunctionArgumentType.string,
    header: SwfServiceCatalogFunctionArgumentType.object,
  };

  const endpoint = routeFrom?.uri.split(":")[0];

  if (endpoint !== "direct") {
    console.log(`'Only routes with direct endpoints are supported`);
    return [] as SwfServiceCatalogFunction[];
  }

  const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
    source,
    name,
    type: SwfServiceCatalogFunctionType.custom,
    arguments: functionArguments,
  };
  swfServiceCatalogFunctions.push(swfServiceCatalogFunction);

  return swfServiceCatalogFunctions;
}
