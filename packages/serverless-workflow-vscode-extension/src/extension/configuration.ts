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

import {
  configurationTokenKeys,
  getInterpolatedConfigurationValue,
  definitelyPosixPath,
} from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";
import * as vscode from "vscode";
import * as path from "path";
import { SwfServiceRegistriesSettings } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export const WEBVIEW_EDITOR_VIEW_TYPE = "kieKogitoWebviewEditorsServerlessWorkflow";

export const CONFIGURATION_SECTIONS = {
  serviceRegistriesSettings: "kogito.swf.serviceRegistries",
  specsStoragePath: "kogito.swf.specsStoragePath",
  routesStoragePath: "kogito.swf.routesStoragePath",
  shouldReferenceServiceRegistryFunctionsWithUrls: "kogito.swf.shouldReferenceServiceRegistryFunctionsWithUrls",
  automaticallyOpenDiagramEditorAlongsideTextEditor: "kogito.swf.automaticallyOpenDiagramEditorAlongsideTextEditor",
};

type ShouldOpenDiagramEditorAutomaticallyType = "Ask next time" | "Do not open" | "Open automatically";
export enum ShouldOpenDiagramEditorAutomaticallyConfiguration {
  ASK = "Ask next time",
  DO_NOT_OPEN = "Do not open",
  OPEN_AUTOMATICALLY = "Open automatically",
}

export class SwfVsCodeExtensionConfiguration {
  public getServiceRegistrySettings(): SwfServiceRegistriesSettings {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.serviceRegistriesSettings, { registries: [] });
  }

  public shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor(): ShouldOpenDiagramEditorAutomaticallyConfiguration {
    const configString = vscode.workspace
      .getConfiguration()
      .get(
        CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK
      ) as ShouldOpenDiagramEditorAutomaticallyType;

    return this.stringToShouldOpenDiagramEditorAutomaticallyConfiguration(configString);
  }

  public stringToShouldOpenDiagramEditorAutomaticallyConfiguration(
    configString: ShouldOpenDiagramEditorAutomaticallyType
  ) {
    switch (configString) {
      case "Ask next time":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK;
      case "Do not open":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN;
      case "Open automatically":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY;
      default:
        throw new Error("Unknown config " + configString);
    }
  }

  public async configureAutomaticallyOpenDiagramEditorAlongsideTextEditor() {
    const picked = await vscode.window.showQuickPick(
      [
        ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN,
      ],
      {
        canPickMany: false,
        title: "Open Serverless Workflow Diagram Editor automatically when opening Serverless Workflow files?",
      }
    );

    await vscode.workspace
      .getConfiguration()
      .update(CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor, picked, true);
  }

  public getConfiguredSpecsDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.specsStoragePath, path.join(configurationTokenKeys["${fileDirname}"], "/specs"));
  }

  public getConfiguredRoutesDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.routesStoragePath, path.join(configurationTokenKeys["${fileDirname}"], "/routes"));
  }

  public getConfiguredFlagShouldReferenceServiceRegistryFunctionsWithUrls() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.shouldReferenceServiceRegistryFunctionsWithUrls, false);
  }

  public getInterpolatedSpecsDirAbsolutePosixPath(args: { baseFileAbsolutePosixPath: string }) {
    return getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: args.baseFileAbsolutePosixPath,
      value: definitelyPosixPath(this.getConfiguredSpecsDirPath()),
    });
  }

  public getInterpolatedSchemaDirAbsolutePosixPath(args: {
    baseFileAbsolutePosixPath: string;
    dataInputSchemaPath: string;
  }) {
    return getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: args.baseFileAbsolutePosixPath,
      value: definitelyPosixPath(args.dataInputSchemaPath),
    });
  }

  public getInterpolatedRoutesDirAbsolutePosixPath(args: { baseFileAbsolutePosixPath: string }) {
    return getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: args.baseFileAbsolutePosixPath,
      value: definitelyPosixPath(this.getConfiguredRoutesDirPath()),
    });
  }
}
