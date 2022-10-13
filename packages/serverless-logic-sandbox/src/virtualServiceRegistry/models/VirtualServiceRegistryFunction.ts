/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { isJson, isSpec } from "../../extension";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { generateOpenApiSpec } from "./BaseOpenApiSpec";
import * as yaml from "yaml";
import { decoder } from "../../workspace/encoderdecoder/EncoderDecoder";

export class VirtualServiceRegistryFunction {
  public name: string;
  public isSpec: boolean;

  constructor(private readonly file: WorkspaceFile) {
    this.name = file.relativePath;
    this.isSpec = isSpec(file.relativePath);
  }

  public async getOpenApiSpec(): Promise<string> {
    const content = await this.file.getFileContents();
    if (this.isSpec) {
      return decoder.decode(content);
    }

    const decodedContent = decoder.decode(content);
    try {
      const parsedContent = isJson(this.file.relativePath) ? JSON.parse(decodedContent) : yaml.parse(decodedContent);
      if (!parsedContent["id"]) {
        throw new Error("No workflow ID!");
      }
      return generateOpenApiSpec(parsedContent["id"]);
    } catch (e) {
      console.error(e);
      return "";
    }
  }
}
