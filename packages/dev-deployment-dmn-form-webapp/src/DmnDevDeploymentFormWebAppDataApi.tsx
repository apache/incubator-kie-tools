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

import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import OpenAPIParser from "@readme/openapi-parser";
import { routes } from "./Routes";
import { DmnFormAppProps } from "./DmnFormApp";
import * as path from "path";

export interface FormData {
  modelName: string;
  schema: ExtendedServicesDmnJsonSchema;
}

export interface AppData {
  forms: FormData[];
  baseOrigin: string;
  basePath: string;
}

export type DmnDefinitionsJson = FormData;

export async function fetchAppData(args: DmnFormAppProps): Promise<AppData> {
  const openApiSpec = await (
    await fetch(routes.quarkusApp.openApiJson.path({}, args.baseOrigin, args.basePath))
  ).json();
  const fixedRefOpenApiSpec = JSON.parse(
    JSON.stringify(openApiSpec).replace(
      new RegExp(`${args.basePath ? `/${args.basePath}` : ""}/dmnDefinitions.json`, "g"),
      routes.quarkusApp.dmnDefinitionsJson.path({}, args.baseOrigin, args.basePath)
    )
  );

  const dereferencedSpec = await OpenAPIParser.dereference(fixedRefOpenApiSpec, {
    dereference: { circular: "ignore" },
  });

  const models = Object.keys(dereferencedSpec.paths)
    .filter((path: string) => path.includes("/dmnresult"))
    .map((path) => path.replace("/dmnresult", ""));

  const forms = models.map((modelPath: string) => {
    const inputSetSchema = dereferencedSpec.paths[modelPath]?.post.requestBody.content["application/json"].schema;
    const outputSetSchema =
      dereferencedSpec.paths[modelPath]?.post.responses.default.content["application/json"].schema;

    return {
      modelName: modelPath.replace(args.basePath ? `/${args.basePath}` : "", "").replace("/", ""),
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
    baseOrigin: args.baseOrigin,
    basePath: args.basePath,
  };
}
