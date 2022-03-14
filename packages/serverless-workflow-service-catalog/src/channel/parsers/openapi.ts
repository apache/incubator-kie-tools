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

import { SwfFunction, SwfFunctionArgumentType, SwfFunctionType, SwfService, SwfServiceType } from "../../api";
import * as yaml from "js-yaml";
import { OpenAPIV3 } from "openapi-types";

const APPLICATION_JSON = "application/json";

type OpenapiPathOperations = Pick<
  OpenAPIV3.PathItemObject,
  "get" | "put" | "post" | "delete" | "options" | "head" | "patch" | "trace"
>;

export function parseOpenApi(args: { fileName: string; storagePath: string; content: string }): SwfService {
  const servicePath = `${args.storagePath}/${args.fileName}`;
  const contentDoc = readOpenapiDoc(args.content);

  const functionDefinitions = extractFunctions(contentDoc, servicePath);

  return {
    name: contentDoc.info.title ?? servicePath,
    type: SwfServiceType.rest,
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

function extractFunctions(contentDoc: OpenAPIV3.Document, servicePath: string): SwfFunction[] {
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
): SwfFunction[] {
  const swfFunctions: SwfFunction[] = [];

  Object.values(pathItem).forEach((pathOperation: OpenAPIV3.OperationObject) => {
    const body: OpenAPIV3.RequestBodyObject = pathOperation.requestBody as OpenAPIV3.RequestBodyObject;

    console.log(pathOperation.operationId);
    // Looking only at application/json mime types, we might consider others.
    if (body && body.content && body.content[APPLICATION_JSON] && body.content[APPLICATION_JSON].schema) {
      const name: string = pathOperation.operationId ?? endpoint.replace(/^\/+/, "");
      const operation = `${servicePath}#${name}`;

      const functionArguments: Record<string, SwfFunctionArgumentType> = extractFunctionArguments(
        body.content[APPLICATION_JSON].schema ?? {},
        contentDoc
      );

      const functionDef: SwfFunction = {
        name,
        operation,
        type: SwfFunctionType.rest,
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
): Record<string, SwfFunctionArgumentType> {
  const schemaObject: OpenAPIV3.SchemaObject = extractSchemaObject(schema, doc);
  const functionArgs: Record<string, SwfFunctionArgumentType> = {};

  if (schemaObject.properties) {
    Object.entries(schemaObject.properties).forEach(
      ([propertyName, propertySchema]: [string, OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject]) => {
        const asReference = propertySchema as OpenAPIV3.ReferenceObject;
        if (asReference.$ref) {
          functionArgs[propertyName] = SwfFunctionArgumentType.object;
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

function resolveArgumentType(type: string): SwfFunctionArgumentType {
  switch (type) {
    case "boolean":
      return SwfFunctionArgumentType.boolean;
    case "object":
      return SwfFunctionArgumentType.object;
    case "number":
      return SwfFunctionArgumentType.number;
    case "string":
      return SwfFunctionArgumentType.string;
    case "integer":
      return SwfFunctionArgumentType.integer;
    case "array":
      return SwfFunctionArgumentType.array;
  }
  return SwfFunctionArgumentType.object;
}
