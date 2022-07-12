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
import { posix as posixPath } from "path";
import { get } from "lodash";

const APPLICATION_JSON = "application/json";

type OpenapiPathOperations = Pick<
  OpenAPIV3.PathItemObject,
  "get" | "put" | "post" | "delete" | "options" | "head" | "patch" | "trace"
>;

export function parseOpenApi(args: {
  specsDirAbsolutePosixPath: string;
  serviceFileName: string;
  serviceFileContent: string;
}): SwfServiceCatalogService {
  const serviceFileAbsolutePosixPath = posixPath.join(args.specsDirAbsolutePosixPath, args.serviceFileName);
  const serviceOpenApiDocument = serviceFileContentToOpenApiDocument(args.serviceFileName, args.serviceFileContent);

  const swfServiceCatalogFunctions = extractFunctions(serviceOpenApiDocument, {
    type: SwfServiceCatalogFunctionSourceType.LOCAL_FS,
    serviceFileAbsolutePath: serviceFileAbsolutePosixPath,
  });

  return {
    name: serviceOpenApiDocument.info.title ?? serviceFileAbsolutePosixPath,
    type: SwfServiceCatalogServiceType.rest,
    source: { type: SwfServiceCatalogServiceSourceType.LOCAL_FS, absoluteFilePath: serviceFileAbsolutePosixPath },
    functions: swfServiceCatalogFunctions,
    rawContent: args.serviceFileContent,
  };
}

function serviceFileContentToOpenApiDocument(serviceFileName: string, serviceFileContent: string): OpenAPIV3.Document {
  let serviceOpenApiDocument;
  if (posixPath.extname(serviceFileName) === ".json") {
    serviceOpenApiDocument = JSON.parse(serviceFileContent) as OpenAPIV3.Document;
  } else {
    serviceOpenApiDocument = yaml.load(serviceFileContent) as OpenAPIV3.Document;
  }

  if (!serviceOpenApiDocument.openapi || !serviceOpenApiDocument.info || !serviceOpenApiDocument.paths) {
    throw new Error(`'${serviceFileName}' is not an OpenAPI file`);
  }

  return serviceOpenApiDocument;
}

export function extractFunctions(
  serviceOpenApiDocument: OpenAPIV3.Document,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions = Object.entries(serviceOpenApiDocument.paths).map(
    ([endpoint, pathItem]: [string, OpenAPIV3.PathItemObject]) => {
      return extractPathItemFunctions(pathItem, endpoint, serviceOpenApiDocument, source);
    }
  );
  return [].concat.apply([], swfServiceCatalogFunctions);
}

function extractPathItemFunctions(
  pathItem: OpenapiPathOperations,
  endpoint: string,
  serviceOpenApiDocument: OpenAPIV3.Document,
  source: SwfServiceCatalogFunctionSource
): SwfServiceCatalogFunction[] {
  const swfServiceCatalogFunctions: SwfServiceCatalogFunction[] = [];

  Object.values(pathItem)
    .filter((pathOperation) => pathOperation.operationId)
    .forEach((pathOperation: OpenAPIV3.OperationObject) => {
      const body = pathOperation.requestBody as OpenAPIV3.RequestBodyObject;

      const name = pathOperation.operationId as string;

      const functionArguments: Record<string, SwfServiceCatalogFunctionArgumentType> = {};

      // Looking at operation params
      if (pathOperation.parameters) {
        extractFunctionArgumentsFromParams(pathOperation.parameters, functionArguments);
      }

      // Looking only at application/json mime types, we might consider others.
      if (body && body.content && body.content[APPLICATION_JSON] && body.content[APPLICATION_JSON].schema) {
        extractFunctionArgumentsFromRequestBody(
          body.content[APPLICATION_JSON].schema ?? {},
          serviceOpenApiDocument,
          functionArguments
        );
      }

      const swfServiceCatalogFunction: SwfServiceCatalogFunction = {
        source,
        name,
        type: SwfServiceCatalogFunctionType.rest,
        arguments: functionArguments,
      };
      swfServiceCatalogFunctions.push(swfServiceCatalogFunction);
    });

  return swfServiceCatalogFunctions;
}

function extractFunctionArgumentsFromParams(
  pathParams: (OpenAPIV3.ReferenceObject | OpenAPIV3.ParameterObject)[],
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  pathParams.forEach((pathParam) => {
    const name = get(pathParam, "name");
    const type = get(pathParam, "schema.type");
    if (name && type) {
      functionParams[name] = resolveArgumentType(type);
    }
  });
}

function extractFunctionArgumentsFromRequestBody(
  schema: OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject,
  doc: OpenAPIV3.Document,
  functionParams: Record<string, SwfServiceCatalogFunctionArgumentType>
) {
  const schemaObject: OpenAPIV3.SchemaObject = extractSchemaObject(schema, doc);

  if (schemaObject.properties) {
    Object.entries(schemaObject.properties).forEach(
      ([propertyName, propertySchema]: [string, OpenAPIV3.ReferenceObject | OpenAPIV3.SchemaObject]) => {
        const asReference = propertySchema as OpenAPIV3.ReferenceObject;
        if (asReference.$ref) {
          functionParams[propertyName] = SwfServiceCatalogFunctionArgumentType.object;
        } else {
          const asSchema = propertySchema as OpenAPIV3.SchemaObject;
          if (asSchema.type) {
            functionParams[propertyName] = resolveArgumentType(asSchema.type);
          }
        }
      }
    );
  }
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
