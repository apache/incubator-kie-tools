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
  SwfServiceCatalogFunctionSource,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "../../api";
import * as yaml from "js-yaml";
import { OpenAPIV3 } from "openapi-types";
import * as path from "path";

const APPLICATION_JSON = "application/json";

type OpenapiPathOperations = Pick<
  OpenAPIV3.PathItemObject,
  "get" | "put" | "post" | "delete" | "options" | "head" | "patch" | "trace"
>;

export function parseOpenApi(args: {
  baseFileAbsolutePath: string;
  specsDirAbsolutePath: string;
  serviceFileName: string;
  serviceFileContent: string;
}): SwfServiceCatalogService {
  const specsDirRelativePath = path.relative(path.dirname(args.baseFileAbsolutePath), args.specsDirAbsolutePath);
  const serviceFileRelativePath = path.join(specsDirRelativePath, args.serviceFileName);
  const serviceFileAbsolutePath = path.join(args.specsDirAbsolutePath, args.serviceFileName);
  const serviceOpenApiDocument = serviceFileContentToOpenApiDocument(serviceFileRelativePath, args.serviceFileContent);

  const swfServiceCatalogFunctions = extractFunctions(serviceOpenApiDocument, serviceFileRelativePath, {
    type: SwfServiceCatalogFunctionSourceType.LOCAL_FS,
    serviceFileAbsolutePath,
  });

  return {
    name: serviceOpenApiDocument.info.title ?? serviceFileAbsolutePath,
    type: SwfServiceCatalogServiceType.rest,
    source: { type: SwfServiceCatalogServiceSourceType.LOCAL_FS, absoluteFilePath: serviceFileAbsolutePath },
    functions: swfServiceCatalogFunctions,
    rawContent: args.serviceFileContent,
  };
}

function serviceFileContentToOpenApiDocument(
  serviceFileRelativePath: string,
  serviceFileContent: string
): OpenAPIV3.Document {
  let serviceOpenApiDocument;
  if (path.extname(serviceFileRelativePath) === ".json") {
    serviceOpenApiDocument = JSON.parse(serviceFileContent) as OpenAPIV3.Document;
  } else {
    serviceOpenApiDocument = yaml.load(serviceFileContent) as OpenAPIV3.Document;
  }

  if (!serviceOpenApiDocument.openapi || !serviceOpenApiDocument.info || !serviceOpenApiDocument.paths) {
    throw new Error(`'${serviceFileRelativePath}' is not an OpenAPI file`);
  }

  return serviceOpenApiDocument;
}

export function extractFunctions(
  serviceOpenApiDocument: OpenAPIV3.Document,
  serviceFileRelativePath: string,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions = Object.entries(serviceOpenApiDocument.paths).map(
    ([endpoint, pathItem]: [string, OpenAPIV3.PathItemObject]) => {
      return extractPathItemFunctions(pathItem, serviceFileRelativePath, endpoint, serviceOpenApiDocument, source);
    }
  );
  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractPathItemFunctions(
  pathItem: OpenapiPathOperations,
  serviceFileRelativePath: string,
  endpoint: string,
  serviceOpenApiDocument: OpenAPIV3.Document,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];

  Object.values(pathItem).forEach((pathOperation: OpenAPIV3.OperationObject) => {
    const body = pathOperation.requestBody as OpenAPIV3.RequestBodyObject;

    // Looking only at application/json mime types, we might consider others.
    if (body && body.content && body.content[APPLICATION_JSON] && body.content[APPLICATION_JSON].schema) {
      const name = pathOperation.operationId ?? endpoint.replace(/^\/+/, "");
      const operation = `${serviceFileRelativePath}#${name}`;

      const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = extractFunctionArguments(
        body.content[APPLICATION_JSON].schema ?? {},
        serviceOpenApiDocument
      );

      const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
        source,
        name,
        operation,
        type: SwfServiceCatalogFunctionType.rest,
        arguments: functionArguments,
      };
      swfServiceCatalogFunctions.push(swfServiceCatalogFunction);
    }
  });

  return swfServiceCatalogFunctions;
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
