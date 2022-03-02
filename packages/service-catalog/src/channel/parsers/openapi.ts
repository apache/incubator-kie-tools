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

import { FunctionArgumentType, Function, FunctionType, Service, ServiceType } from "../../api";
import * as yaml from "js-yaml";
import { OpenAPIV3 } from "openapi-types";

const APPLICATION_JSON = "application/json";

export function parseOpenAPI(args: { fileName: string; storagePath: string; content: string }): Service {
  const servicePath = `${args.storagePath}/${args.fileName}`;
  const contentDoc = readOpenapiDoc(args.content);

  const functionDefinitions = extractFunctions(contentDoc, servicePath);

  return {
    name: contentDoc.info.title ?? servicePath,
    type: ServiceType.rest,
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

function extractFunctions(contentDoc: OpenAPIV3.Document, servicePath: string): Function[] {
  const functionDefinitions: Function[] = [];

  Object.entries(contentDoc.paths).forEach(([endpoint, pathItem]: [string, OpenAPIV3.PathItemObject]) => {
    if (pathItem.post) {
      const postOperation = pathItem.post;
      const body: OpenAPIV3.RequestBodyObject = postOperation.requestBody as OpenAPIV3.RequestBodyObject;

      // Looking only at application/json mime types, we might consider others.
      if (body && body.content && body.content[APPLICATION_JSON] && body.content[APPLICATION_JSON].schema) {
        const name: string = postOperation.operationId ? postOperation.operationId : endpoint.replace(/^\/+/, "");
        const operation = `${servicePath}#${name}`;

        const functionArguments: Record<string, FunctionArgumentType> = extractFunctionArguments(
          body.content[APPLICATION_JSON].schema ?? {},
          contentDoc
        );

        const functionDef: Function = {
          name,
          operation,
          type: FunctionType.rest,
          arguments: functionArguments,
        };
        functionDefinitions.push(functionDef);
      }
    }
  });

  return functionDefinitions;
}

function extractFunctionArguments(
  schema: OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject,
  doc: OpenAPIV3.Document
): Record<string, FunctionArgumentType> {
  const schemaObject: OpenAPIV3.SchemaObject = extractSchemaObject(schema, doc);
  const functionArgs: Record<string, FunctionArgumentType> = {};

  if (schemaObject.properties) {
    Object.entries(schemaObject.properties).forEach(
      ([propertyName, propertySchema]: [string, OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject]) => {
        const asReference = propertySchema as OpenAPIV3.ReferenceObject;
        if (asReference.$ref) {
          functionArgs[propertyName] = FunctionArgumentType.object;
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

function resolveArgumentType(type: string): FunctionArgumentType {
  switch (type) {
    case "boolean":
      return FunctionArgumentType.boolean;
    case "object":
      return FunctionArgumentType.object;
    case "number":
      return FunctionArgumentType.number;
    case "string":
      return FunctionArgumentType.string;
    case "integer":
      return FunctionArgumentType.integer;
    case "array":
      return FunctionArgumentType.array;
  }
  return FunctionArgumentType.object;
}
