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

import * as jsonParse from "json-to-ast";
import { SwfOffsetsApi } from "../api/SwfOffsetsApi";

const astTransformQuery = `children[key.value='states'].value.children {
    "states":{
        children[key.value='name'].value.value:{
            "stateNameOffset": children[key.value='name'].loc.start.offset,
            "offset": $.loc{
                "start":start.offset,
                "end":end.offset
            }
        }
    }
}
`;

export class SwfJsonOffsets extends SwfOffsetsApi {
  constructor(documentUri?: string) {
    super(astTransformQuery, documentUri);
  }

  getFullAST(): any {
    if (!this.fullText) {
      return null;
    }

    try {
      return jsonParse(this.fullText);
    } catch (e) {
      console.error(`Received an exeption parsing the content: ${e.message}`);
      return null;
    }
  }
}
