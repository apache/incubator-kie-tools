/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as vscode from "vscode";
import { FileType } from "vscode";
import { parseOpenAPI, ServiceCatalogRegistry } from "@kie-tools/service-catalog/dist/channel";
import { FunctionDefinition, ServiceDefinition } from "@kie-tools/service-catalog/dist/api";

const OPENAPI_EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

export class FileSystemServiceCatalogRegistry implements ServiceCatalogRegistry {
  private registry: Map<ServiceDefinition, FunctionDefinition[]> = new Map<ServiceDefinition, FunctionDefinition[]>();

  constructor(private readonly specsFolder: string, private readonly fullSpecsStoragePath: string) {
    this.load();
  }

  getFunctionDefinitions(serviceId?: string): Promise<FunctionDefinition[]> {
    const result: FunctionDefinition[] = [];

    this.registry.forEach((functions, service) => {
      if (!serviceId || (serviceId && service.id === serviceId)) {
        result.push(...functions);
      }
    });
    return Promise.resolve(result);
  }

  public getServiceDefinitions(): Promise<ServiceDefinition[]> {
    return Promise.resolve(Array.from(this.registry.keys()));
  }

  public persistService(serviceId: string): void {}

  getFunctionDefinition(operationId?: string): Promise<FunctionDefinition | undefined> {
    if (this.registry && operationId) {
      for (const functionDefs of this.registry.values()) {
        for (const functionDef of functionDefs) {
          if (functionDef.operation === operationId) {
            return Promise.resolve(functionDef);
          }
        }
      }
    }
    return Promise.resolve(undefined);
  }

  private async load() {
    try {
      const fullSpecsStorageUri = vscode.Uri.parse(this.fullSpecsStoragePath);

      const stats = await vscode.workspace.fs.stat(fullSpecsStorageUri);

      if (!stats || stats.type !== FileType.Directory) {
        throw new Error(`Invalid path: ${this.fullSpecsStoragePath}`);
      }

      const files = await vscode.workspace.fs.readDirectory(fullSpecsStorageUri);

      files.forEach(([fileName, type]) => {
        if (type === FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase())) {
          vscode.workspace.fs
            .readFile(
              fullSpecsStorageUri.with({
                path: this.fullSpecsStoragePath + "/" + fileName,
              })
            )
            .then(async (data) => {
              const content = Buffer.from(data).toString("utf-8");
              const result = parseOpenAPI({
                fileName,
                storagePath: this.specsFolder,
                content,
              });
              this.registry.set(result.serviceDefinition, result.functionDefinitions);
            });
        }
      });
    } catch (error) {
      console.log(`Error loading catalog: `, error);
    }
  }
}
