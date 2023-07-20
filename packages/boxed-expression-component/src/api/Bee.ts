/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import _ from "lodash";
import { ExpressionDefinition } from "./ExpressionDefinition";
import { v4 as uuid } from "uuid";

export {};

declare global {
  // Set of Functions used to interact with the GWT Layer. Must be synchronized with ExpressionEditorService.java
  interface BeeApi {
    // It requests to the GWT layer the default ExpressionDefinition given a selected logic type and a data type.
    getDefaultExpressionDefinition: (logicType: string, dataType: string) => ExpressionDefinition;

    // It Navigates to "Data Type" tab page
    openDataTypePage: () => void;

    // Notifies that an object was selected.
    selectObject: (uuid?: string) => void;

    // It propagates to the GWT layer the modified expression. It MUTATES the GWT layer status.
    updateExpression: (expressionDefinition: ExpressionDefinition) => void;
  }

  // API that the containing component of BoxedExpressionEditor (BEE) expects to be defined in the Window namespace
  interface Window {
    beeApiWrapper: BeeApi;
  }
}

/**
 * Generates an UUID with a format similar to _6EFDBCB4-F4AF-4E9A-9A66-2A9F24185674
 */
export const generateUuid = () => {
  return `_${uuid()}`.toLocaleUpperCase();
};
