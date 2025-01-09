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

  // Append origin to schema $refs
  Object.keys(openApiSpec.paths).forEach((modelPath) => {
    const inputSetSchemaRef = openApiSpec.paths[modelPath]?.post.requestBody.content["application/json"].schema.$ref;
    const outputSetSchemaRef =
      openApiSpec.paths[modelPath]?.post.responses.default?.content["application/json"].schema.$ref;

    if (inputSetSchemaRef) {
      openApiSpec.paths[modelPath].post.requestBody.content["application/json"].schema.$ref =
        `${args.quarkusAppOrigin}${inputSetSchemaRef}`;
    }

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
