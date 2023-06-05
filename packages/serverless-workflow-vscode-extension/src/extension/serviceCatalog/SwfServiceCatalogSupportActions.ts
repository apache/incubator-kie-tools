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

import {
  SwfServiceCatalogService,
  SwfCatalogSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as vscode from "vscode";
import { posix as posixPath } from "path";
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogStore } from "./SwfServiceCatalogStore";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "./serviceRegistry";

const encoder = new TextEncoder();

export class SwfServiceCatalogSupportActions {
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {}

  public importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    if (args.containingService.source.type === SwfCatalogSourceType.LOCAL_FS) {
      return;
    }

    const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(
      args.containingService.source.registry,
      args.containingService.source.id
    );

    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
      baseFileAbsolutePosixPath: vscode.Uri.parse(args.documentUri).path,
    });

    const routesDirAbsolutePosixPath = this.args.configuration.getInterpolatedRoutesDirAbsolutePosixPath({
      baseFileAbsolutePosixPath: vscode.Uri.parse(args.documentUri).path,
    });

    const serviceFileAbsolutePosixPath = posixPath.join(specsDirAbsolutePosixPath, serviceFileName);

    const routesServiceFileAbsolutePosixPath = posixPath.join(routesDirAbsolutePosixPath, serviceFileName);
    vscode.workspace.fs.writeFile(
      vscode.Uri.parse(serviceFileAbsolutePosixPath),
      encoder.encode(args.containingService.rawContent)
    );

    vscode.workspace.fs.writeFile(
      vscode.Uri.parse(routesServiceFileAbsolutePosixPath),
      encoder.encode(args.containingService.rawContent)
    );
    vscode.window.showInformationMessage(
      `Wrote ${serviceFileAbsolutePosixPath} and ${routesServiceFileAbsolutePosixPath}.`
    );
  }

  public importEventFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    if (args.containingService.source.type === SwfCatalogSourceType.LOCAL_FS) {
      return;
    }

    const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(
      args.containingService.source.registry,
      args.containingService.source.id
    );

    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
      baseFileAbsolutePosixPath: vscode.Uri.parse(args.documentUri).path,
    });

    const serviceFileAbsolutePosixPath = posixPath.join(specsDirAbsolutePosixPath, serviceFileName);

    vscode.workspace.fs.writeFile(
      vscode.Uri.parse(serviceFileAbsolutePosixPath),
      encoder.encode(args.containingService.rawContent)
    );

    vscode.window.showInformationMessage(`Wrote ${serviceFileAbsolutePosixPath}`);
  }
}
