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

import { getTextWidth } from "@kie-tools/boxed-expression-component/dist/resizing/WidthsToFitData";
import { DEFAULT_MIN_WIDTH } from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

export function getDefaultColumnWidth({ name, typeRef }: { name: string; typeRef: string | undefined }): number {
  return (
    8 * 2 + // Copied from ContextEntry variable `getWidthToFit`
    2 + // Copied from ContextEntry variable `getWidthToFit`
    Math.max(
      DEFAULT_MIN_WIDTH,
      getTextWidth(name, "700 11.2px Menlo, monospace"),
      getTextWidth(
        `(${typeRef ?? DmnBuiltInDataType.Undefined})`,
        "700 11.6667px RedHatText, Overpass, overpass, helvetica, arial, sans-serif"
      )
    )
  );
}
