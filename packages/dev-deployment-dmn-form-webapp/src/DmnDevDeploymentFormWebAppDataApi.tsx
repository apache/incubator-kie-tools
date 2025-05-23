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

import { ExtendedServicesFormSchema } from "@kie-tools/extended-services-api";
import OpenAPIParser from "@readme/openapi-parser";
import { routes } from "./Routes";
import path from "path";

export interface FormData {
  modelName: string;
  schema: ExtendedServicesFormSchema;
}

export interface AppData {
  forms: FormData[];
}

export type DmnDefinitionsJson = FormData;

export async function fetchAppData(args: { quarkusAppOrigin: string; quarkusAppPath: string }): Promise<AppData> {
  const openApiSpec = await (
    await fetch(routes.quarkusApp.openApiJson.path({}, args.quarkusAppOrigin, args.quarkusAppPath))
  ).json();

  // Save all `/dmnresult` routes.
  const dmnResultPaths = new Set(
    Object.keys(openApiSpec.paths).filter((modelPath) => modelPath.endsWith("/dmnresult"))
  );

  // Append origin to schema $refs, but only on DMN paths
  //
  // It's important to skip paths not ending on `/dmnresult` (or paths that don't
  // have a matching `/dmnresult` path) because the application being
  // deployed may have other paths that we don't control, and not all of them
  // will have valid JSON Schemas.
  // Beyond that, we want to delete these paths from the openApiSpec to avoid trying to
  // dereferencing them.
  Object.keys(openApiSpec.paths).forEach((modelPath) => {
    if (
      !modelPath.endsWith("/dmnresult") &&
      !Object.keys(openApiSpec.paths).find((path) => path === `${modelPath}/dmnresult`)
    ) {
      delete openApiSpec.paths?.[modelPath];
      return;
    }

    const inputSetSchemaRef =
      openApiSpec.paths?.[modelPath]?.post?.requestBody?.content?.["application/json"]?.schema?.$ref;
    if (inputSetSchemaRef) {
      openApiSpec.paths[modelPath].post.requestBody.content["application/json"].schema.$ref =
        `${args.quarkusAppOrigin}${inputSetSchemaRef}`;
    }

    const outputSetSchemaRef =
      openApiSpec.paths?.[modelPath]?.post?.responses?.default?.content?.["application/json"]?.schema?.$ref;
    if (outputSetSchemaRef) {
      openApiSpec.paths[modelPath].post.responses.default.content["application/json"].schema.$ref =
        `${args.quarkusAppOrigin}${outputSetSchemaRef}`;
    }
  });

  // Dereference schema (replace $refs with their values fetched from the <model>.json files)
  const dereferencedSpec = await OpenAPIParser.dereference(openApiSpec, {
    dereference: { circular: "ignore" },
  });

  // Filter models with dmnresult endpoints
  const models = Object.keys(dereferencedSpec.paths)
    .filter((path: string) => path.includes("/dmnresult"))
    .map((path) => path.replace("/dmnresult", ""));

  // Generate form objects from the models
  const forms = models.map((modelPath: string) => {
    const inputSetSchema = dereferencedSpec.paths[modelPath]?.post.requestBody.content["application/json"].schema;
    const outputSetSchema =
      dereferencedSpec.paths[modelPath]?.post.responses.default.content["application/json"].schema;

    return {
      modelName: modelPath.replace(args.quarkusAppPath ? `/${args.quarkusAppPath}` : "", "").replace("/", ""),
      schema: {
        $ref: "#/definitions/InputSet",
        definitions: {
          InputSet: {
            ...inputSetSchema,
          },
          OutputSet: {
            ...outputSetSchema,
          },
        },
      },
    };
  });

  return {
    forms,
  };
}
