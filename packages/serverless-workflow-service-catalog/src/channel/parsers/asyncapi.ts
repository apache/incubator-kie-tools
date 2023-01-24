/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Channel } from "@asyncapi/parser";
import { posix as posixPath } from "path";
import * as yaml from "js-yaml";
import * as AsyncApi from "../../api/asyncapitypes";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionSource,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSource,
  SwfServiceCatalogServiceType,
} from "../../api";
import { get } from "lodash";
import { convertSource } from "./util";

type AsyncapiPathOperations = Pick<Channel, "subscribe" | "publish">;

export function parseAsyncApi(
  args: {
    source: SwfServiceCatalogServiceSource;
    serviceFileName: string;
    serviceFileContent: string;
  },
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument
): SwfServiceCatalogService {
  const swfServiceCatalogFunctions = extractFunctions(serviceAsyncApiDocument, convertSource(args.source));

  console.log("in Async api logic", swfServiceCatalogFunctions);

  return {
    name: serviceAsyncApiDocument.info.title ?? args.serviceFileName,
    type: SwfServiceCatalogServiceType.asyncapi,
    source: args.source,
    functions: swfServiceCatalogFunctions,
    rawContent: args.serviceFileContent,
  } as SwfServiceCatalogService;
}

export function extractFunctions(
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions = Object.entries(serviceAsyncApiDocument.channels).map(
    ([endpoint, channelItem]: [string, AsyncApi.ChannelsObject]) => {
      return extractChannelItemFunctions(channelItem, endpoint, serviceAsyncApiDocument, source);
    }
  );
  console.log("extractFunctions", swfServiceCatalogFunctions);
  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractChannelItemFunctions(
  channelItem: AsyncApi.ChannelsObject,
  endpoint: string,
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];
  console.log("extractPathItemFunctions-asyncapi", channelItem, endpoint, serviceAsyncApiDocument, source);
  Object.values(channelItem)
    .filter((channelOperation: AsyncApi.OperationObject) => channelOperation.operationId)
    .forEach((channelOperation: any) => {
      console.log("channelOperation", channelOperation);
      const body = channelOperation.message;

      const name = channelOperation.operationId as string;

      const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {};

      // Looking at operation params
      if (channelOperation.parameters) {
        extractFunctionArgumentsFromParams(channelOperation.parameters, functionArguments);
      }

      // Looking only at application/json mime types, we might consider others.
      if (body) {
        extractFunctionArgumentsFromRequestBody(body ?? {}, serviceAsyncApiDocument, functionArguments);
      }

      const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
        source,
        name,
        type: SwfServiceCatalogFunctionType.rest,
        arguments: functionArguments,
      };
      console.log("async catalog function object", swfServiceCatalogFunction);
      swfServiceCatalogFunctions.push(swfServiceCatalogFunction);
      console.log("async catalog function array", swfServiceCatalogFunctions);
    });
  console.log("extractPathItemFunctions", swfServiceCatalogFunctions);
  return swfServiceCatalogFunctions;
}

function extractFunctionArgumentsFromParams(
  channelParams: (AsyncApi.ReferenceObject | AsyncApi.ParametersObject)[],
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  console.log("extractFunctionArgumentsFromParams", channelParams, functionParams);
  channelParams.forEach((channelParam) => {
    const description = get(channelParam, "description");
    const type = get(channelParam, "schema.type");
    if (description && type) {
      functionParams[description] = resolveArgumentType(type);
    }
  });
}

function extractFunctionArgumentsFromRequestBody(
  message: AsyncApi.MessageObject,
  doc: AsyncApi.AsyncAPIDocument,
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  console.log("extractFunctionArgumentsFromRequestBody", message, doc, functionParams);
  const schemaObject: AsyncApi.SchemaObject = extractMessageObject(message, doc);
  console.log("schemaObject-asyncapi", schemaObject);
  if (schemaObject.properties) {
    Object.entries(schemaObject.properties).forEach(
      ([propertyName, propertySchema]: [string, AsyncApi.ReferenceObject | AsyncApi.SchemaObject]) => {
        const asReference = propertySchema as AsyncApi.ReferenceObject;
        if (asReference.$ref) {
          functionParams[propertyName] = SwfServiceCatalogFunctionArgumentType.object;
        } else {
          const asSchema = propertySchema as AsyncApi.SchemaObject;
          if (asSchema.type) {
            functionParams[propertyName] = resolveArgumentType(asSchema.type);
          }
        }
      }
    );
  }
}

function extractMessageObject(message: AsyncApi.MessageObject, doc: AsyncApi.AsyncAPIDocument): any {
  const asReference = message as AsyncApi.MessageObject;
  if (asReference.$ref) {
    const messageRef: any = asReference.$ref.split("/").pop() ?? "";
    const resolvedMessage = doc.components?.messages
      ? doc.components?.messages[messageRef]
      : ({} as AsyncApi.MessageObject);

    return extractSchemaObject(resolvedMessage?.payload, doc);
  }
  return message?.payload;
}

function extractSchemaObject(payload: any, doc: AsyncApi.AsyncAPIDocument): AsyncApi.SchemaObject {
  const asReference = payload as any;
  if (asReference.$ref) {
    const schemaRef = asReference.$ref.split("/").pop() ?? "";
    const resolvedSchema = doc.components?.schemas ? doc.components?.schemas[schemaRef] : {};
    return extractSchemaObject(resolvedSchema, doc);
  }
  return asReference as any;
}

function resolveArgumentType(type: string): SwfServiceCatalogFunctionArgumentType {
  switch (type) {
    case "boolean":
      return SwfServiceCatalogFunctionArgumentType.boolean;
    case "object":
      return SwfServiceCatalogFunctionArgumentType.object;
    case "number":
      return SwfServiceCatalogFunctionArgumentType.number;
    case "string":
      return SwfServiceCatalogFunctionArgumentType.string;
    case "integer":
      return SwfServiceCatalogFunctionArgumentType.integer;
    case "array":
      return SwfServiceCatalogFunctionArgumentType.array;
  }
  return SwfServiceCatalogFunctionArgumentType.object;
}
