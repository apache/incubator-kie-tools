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

import * as yaml from "js-yaml";
import { posix as posixPath } from "path";
import { SupportArtifactTypes, SwfServiceCatalogService, SwfServiceCatalogServiceSource } from "../../api";
import { AsyncApiParser } from "./impl/asyncapi/AsyncApiParser";
import { JsonSchemaParser } from "./impl/jsonschema/JsonSchemaParser";
import { OpenApiParser } from "./impl/openapi/OpenApiParser";
import { ArgsType, SpecParser } from "./impl/SpecParser";

const specParsers: SpecParser<any>[] = [new OpenApiParser(), new AsyncApiParser(), new JsonSchemaParser()];

export const supportArtifactTypes: SupportArtifactTypes[] = [
  SupportArtifactTypes.Openapi,
  SupportArtifactTypes.Asyncapi,
];

export function parseApiContent(args: {
  serviceFileName: string;
  serviceFileContent: string;
  source: SwfServiceCatalogServiceSource;
}): SwfServiceCatalogService {
  return serviceFileContentToApiDocument(args);
}

function serviceFileContentToApiDocument(args: ArgsType) {
  let specContent: any;

  if (posixPath.extname(args.serviceFileName) === ".json") {
    specContent = JSON.parse(args.serviceFileContent);
  } else {
    specContent = yaml.load(args.serviceFileContent);
  }
  const parser = specParsers.find((parser) => parser.canParse(specContent));
  if (!parser) {
    throw new Error(`'${args.serviceFileName}' is not a supported spec file`);
  }

  return parser.parse(specContent, args);
}
