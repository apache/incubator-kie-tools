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
import { Disposable, FileType } from "vscode";
import { parseOpenAPI, ServiceCatalogRegistry } from "@kie-tools/service-catalog/dist/channel";
import { Service } from "@kie-tools/service-catalog/dist/api";

const OPENAPI_EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

export class FileSystemServiceCatalogRegistry implements ServiceCatalogRegistry {
  private registryConsumer: (services: Service[]) => void;
  private readonly onDispose: () => void;

  constructor(private readonly specsFolder: string, private readonly fullSpecsStoragePath: string) {
    const fsWatcher = vscode.workspace.createFileSystemWatcher(
      `${fullSpecsStoragePath}/*.{json,yaml,yml}`,
      false,
      false,
      false
    );
    const onDidCreate: Disposable = fsWatcher.onDidCreate((e) => this.load());
    const onDidChange: Disposable = fsWatcher.onDidChange((e) => this.load());
    const onDidDelete: Disposable = fsWatcher.onDidDelete((e) => this.load());

    this.onDispose = () => {
      onDidCreate.dispose();
      onDidChange.dispose();
      onDidDelete.dispose();
      fsWatcher.dispose();
    };
  }

  public init(registryConsumer: (services: Service[]) => void): void {
    this.registryConsumer = registryConsumer;
  }

  public load() {
    this.readServices()
      .then((services) => this.pushServices(services))
      .catch((err) => {
        console.error("Cannot load services", err);
        this.pushServices([]);
      });
  }

  public dispose(): void {
    this.onDispose();
  }

  private pushServices(services: Service[]) {
    if (this.registryConsumer) {
      this.registryConsumer(services);
    }
  }

  private readServices(): Promise<Service[]> {
    return new Promise<Service[]>((resolve, reject) => {
      try {
        const fullSpecsStorageUri = vscode.Uri.parse(this.fullSpecsStoragePath);

        vscode.workspace.fs.stat(fullSpecsStorageUri).then((stats) => {
          if (!stats || stats.type !== FileType.Directory) {
            reject(`Invalid path: ${this.fullSpecsStoragePath}`);
          }

          vscode.workspace.fs.readDirectory(fullSpecsStorageUri).then((files) => {
            if (files && files.length > 0) {
              const promises: Promise<Service | undefined>[] = [];
              files.forEach(([fileName, type]) => {
                if (type === FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase())) {
                  const fileUrl = fullSpecsStorageUri.with({
                    path: this.fullSpecsStoragePath + "/" + fileName,
                  });
                  promises.push(this.readServiceFile(fileUrl, fileName));
                }
              });
              if (promises.length > 0) {
                Promise.all(promises).then((services) => {
                  const filteredServices: Service[] = [];
                  services.forEach((service) => {
                    if (service) {
                      filteredServices.push(service);
                    }
                  });
                  resolve(filteredServices);
                });
              } else {
                resolve([]);
              }
            }
          });
        });
      } catch (error) {
        reject(`Error loading catalog: ${error}`);
      }
    });
  }

  private readServiceFile(fileUrl: vscode.Uri, fileName: string): Promise<Service | undefined> {
    return new Promise<Service | undefined>((resolve) => {
      vscode.workspace.fs.readFile(fileUrl).then((rawData) => {
        const content = Buffer.from(rawData).toString("utf-8");
        try {
          const Service = parseOpenAPI({
            fileName,
            storagePath: this.specsFolder,
            content,
          });
          resolve(Service);
        } catch (err) {
          resolve(undefined);
        }
      });
    });
  }
}
