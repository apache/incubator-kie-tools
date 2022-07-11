/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { load } from "yaml-language-server-parser";
import { SwfOffsetsApi } from "../api/SwfOffsetsApi";

const astTransformQuery = `mappings[key.value="states"].value.items{
    "states":{
        mappings[key.value="name"].value.value:{
            "stateNameOffset":startPosition,
            "offset": {
                "start":startPosition,
                "end":endPosition
            }
        }
    }
}
`;

export class SwfYamlOffsets extends SwfOffsetsApi {
  constructor(documentUri?: string) {
    super(astTransformQuery, documentUri);
  }

  getFullAST(): any {
    const errorLogMsg = (message: string) => `Received an exeption parsing the content: ${message}`;

    if (!this.fullText) {
      return null;
    }

    try {
      const ast = load(this.fullText);

      // check if the yaml is not valid
      if (ast.errors && ast.errors.length) {
        console.error(errorLogMsg(ast.errors[0].message));
        return null;
      }

      return ast;
    } catch (e) {
      console.error(errorLogMsg(e.message));
      return null;
    }
  }
}
