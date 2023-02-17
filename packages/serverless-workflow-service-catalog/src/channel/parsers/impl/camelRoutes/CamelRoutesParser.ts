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

import { SwfServiceCatalogService } from "../../../../api";
import { parseCamelRoutes } from "./camelRoutes";
import { ArgsType, SpecParser } from "../SpecParser";

export class CamelRoutesParser implements SpecParser<any> {
  canParse(content: any): boolean {
    return Array.isArray(content) && content.some((route: any) => route.from);
  }

  parse(serviceCamelRoutesDocument: any, args: ArgsType): SwfServiceCatalogService {
    return parseCamelRoutes(args, serviceCamelRoutesDocument);
  }
}
