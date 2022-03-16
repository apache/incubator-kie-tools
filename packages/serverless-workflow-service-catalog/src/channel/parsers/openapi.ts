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

import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceType,
} from "../../api";
import * as yaml from "js-yaml";
import { OpenAPIV3 } from "openapi-types";

const APPLICATION_JSON = "application/json";

type OpenapiPathOperations = Pick<
  OpenAPIV3.PathItemObject,
  "get" | "put" | "post" | "delete" | "options" | "head" | "patch" | "trace"
>;

export function parseOpenApi(args: {
  fileName: string;
  storagePath: string;
  content: string;
}): SwfServiceCatalogService {
  const servicePath = `${args.storagePath}/${args.fileName}`;
  const contentDoc = readOpenapiDoc(args.content);

  const functionDefinitions = extractFunctions(contentDoc, servicePath);

  return {
    name: contentDoc.info.title ?? servicePath,
    type: SwfServiceCatalogServiceType.rest,
    id: servicePath,
    functions: functionDefinitions,
    rawContent: args.content,
  };
}

function readOpenapiDoc(content: string): OpenAPIV3.Document {
  const contentDoc = yaml.load(content) as OpenAPIV3.Document;

  if (!contentDoc.openapi || !contentDoc.info || !contentDoc.paths) {
    throw new Error("Invalid format");
  }
  return contentDoc;
}

function extractFunctions(contentDoc: OpenAPIV3.Document, servicePath: string): SwfServiceCatalogFunction[] {
  const swfFunctions = Object.entries(contentDoc.paths).map(
    ([endpoint, pathItem]: [string, OpenAPIV3.PathItemObject]) => {
      return extractPathItemFunctions(pathItem, servicePath, endpoint, contentDoc);
    }
  );
  return [].concat.apply([], swfFunctions);
}

function extractPathItemFunctions(
  pathItem: OpenapiPathOperations,
  servicePath: string,
  endpoint: string,
  contentDoc: OpenAPIV3.Document
): SwfServiceCatalogFunction[] {
  const swfFunctions: SwfServiceCatalogFunction[] = [];

  Object.values(pathItem).forEach((pathOperation: OpenAPIV3.OperationObject) => {
    const body: OpenAPIV3.RequestBodyObject = pathOperation.requestBody as OpenAPIV3.RequestBodyObject;

    console.log(pathOperation.operationId);
    // Looking only at application/json mime types, we might consider others.
    if (body && body.content && body.content[APPLICATION_JSON] && body.content[APPLICATION_JSON].schema) {
      const name: string = pathOperation.operationId ?? endpoint.replace(/^\/+/, "");
      const operation = `${servicePath}#${name}`;

      const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = extractFunctionArguments(
        body.content[APPLICATION_JSON].schema ?? {},
        contentDoc
      );

      const functionDef: SwfServiceCatalogFunction = {
        name,
        operation,
        type: SwfServiceCatalogFunctionType.rest,
        arguments: functionArguments,
      };
      swfFunctions.push(functionDef);
    }
  });

  return swfFunctions;
}

function extractFunctionArguments(
  schema: OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject,
  doc: OpenAPIV3.Document
): Record<string, SwfServiceCatalogFunctionArgumentType> {
  const schemaObject: OpenAPIV3.SchemaObject = extractSchemaObject(schema, doc);
  const functionArgs: Record<string, SwfServiceCatalogFunctionArgumentType> = {};

  if (schemaObject.properties) {
    Object.entries(schemaObject.properties).forEach(
      ([propertyName, propertySchema]: [string, OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject]) => {
        const asReference = propertySchema as OpenAPIV3.ReferenceObject;
        if (asReference.$ref) {
          functionArgs[propertyName] = SwfServiceCatalogFunctionArgumentType.object;
        } else {
          const asSchema = propertySchema as OpenAPIV3.SchemaObject;
          if (asSchema.type) {
            functionArgs[propertyName] = resolveArgumentType(asSchema.type);
          }
        }
      }
    );
  }

  return functionArgs;
}

function extractSchemaObject(
  schema: OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject,
  doc: OpenAPIV3.Document
): OpenAPIV3.SchemaObject {
  const asReference = schema as OpenAPIV3.ReferenceObject;
  if (asReference.$ref) {
    const schemaRef = asReference.$ref.split("/").pop() ?? "";
    const resolvedSchema = doc.components?.schemas ? doc.components?.schemas[schemaRef] : {};
    return extractSchemaObject(resolvedSchema, doc);
  }
  return schema as OpenAPIV3.SchemaObject;
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
