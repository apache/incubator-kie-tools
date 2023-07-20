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
  SwfServiceCatalogEvent,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionSource,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSource,
  SwfServiceCatalogServiceType,
  SwfServiceCatalogEventType,
  SwfServiceCatalogEventKind,
} from "../../../../api";
import { convertSource } from "../convertSource";
import * as AsyncApi from "./types";

export function parseAsyncApi(
  args: {
    source: SwfServiceCatalogServiceSource;
    serviceFileName: string;
    serviceFileContent: string;
  },
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument
): SwfServiceCatalogService {
  const swfServiceCatalogFunctions = extractFunctions(serviceAsyncApiDocument, convertSource(args.source));
  const swfServiceCatalogEvents = extractEvents(serviceAsyncApiDocument, convertSource(args.source));

  return {
    name: serviceAsyncApiDocument.info.title ?? args.serviceFileName,
    type: SwfServiceCatalogServiceType.asyncapi,
    source: args.source,
    functions: swfServiceCatalogFunctions,
    events: swfServiceCatalogEvents,
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

  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractChannelItemFunctions(
  channelItem: AsyncApi.ChannelsObject,
  endpoint: string,
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];

  const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {};

  // Looking at operation params
  if (channelItem.parameters) {
    extractFunctionArgumentsFromParams(channelItem?.parameters, functionArguments);
  }
  Object.values(channelItem)
    .filter((channelOperation: AsyncApi.OperationObject) => channelOperation.operationId)
    .forEach((channelOperation: any) => {
      const body = channelOperation.message;
      const name = channelOperation.operationId as string;

      // Looking only at application/json mime types, we might consider others.
      if (body) {
        extractFunctionArgumentsFromRequestBody(body ?? {}, serviceAsyncApiDocument, functionArguments);
      }

      const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
        source,
        name,
        type: SwfServiceCatalogFunctionType.asyncapi,
        arguments: functionArguments,
      };
      swfServiceCatalogFunctions.push(swfServiceCatalogFunction);
    });
  return swfServiceCatalogFunctions;
}

function extractFunctionArgumentsFromParams(
  channelParams: any,
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  const paramNames = Object.keys(channelParams);
  paramNames.forEach((paramName) => {
    if (channelParams && paramName) {
      functionParams[paramName] = resolveArgumentType(channelParams[paramName]?.schema?.type);
    }
  });
}

function extractFunctionArgumentsFromRequestBody(
  message: AsyncApi.MessageObject,
  doc: AsyncApi.AsyncAPIDocument,
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  const schemaObject: AsyncApi.SchemaObject = extractMessageObject(message, doc);
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

export function extractEvents(
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogEvent[] {
  const swfServiceCatalogEvents = Object.entries(serviceAsyncApiDocument.channels).map(
    ([endpoint, channelItem]: [string, AsyncApi.ChannelsObject]) => {
      return extractChannelItemEvents(channelItem, endpoint, serviceAsyncApiDocument, source);
    }
  );

  return [].concat.apply([], swfServiceCatalogEvents);
}

function extractChannelItemEvents(
  channelItem: any,
  endpoint: string,
  serviceAsyncApiDocument: AsyncApi.AsyncAPIDocument,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogEvent[] {
  const swfServiceCatalogEvents: SwfServiceCatalogEvent[] = [];

  const eventArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {};

  // Looking at operation params
  if (channelItem.parameters) {
    extractFunctionArgumentsFromParams(channelItem?.parameters, eventArguments);
  }
  Object.values(channelItem)
    .filter((channelOperation: AsyncApi.OperationObject) => channelOperation.operationId)
    .forEach((channelOperation: any) => {
      const body = channelOperation.message;
      const name = channelOperation.operationId as string;
      let eventKind: SwfServiceCatalogEventKind;
      const operation = Object.keys(channelItem).find((key) => channelItem[key] === channelOperation);
      if (operation === "subscribe") {
        eventKind = SwfServiceCatalogEventKind.CONSUMED;
      } else if (operation === "publish") {
        eventKind = SwfServiceCatalogEventKind.PRODUCED;
      }
      // Looking only at application/json mime types, we might consider others.
      if (body) {
        extractFunctionArgumentsFromRequestBody(body ?? {}, serviceAsyncApiDocument, eventArguments);
      }

      const swfServiceCatalogEvent: SwfServiceCatalogEvent = {
        source,
        name,
        type: SwfServiceCatalogEventType.asyncapi,
        kind: eventKind!,
        eventSource: "",
        eventType: "",
      };
      swfServiceCatalogEvents.push(swfServiceCatalogEvent);
    });
  return swfServiceCatalogEvents;
}
