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

import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import { SwfLanguageServiceChannelApi } from "../../../serverless-workflow-language-service/dist/api/SwfLanguageServiceChannelApi";
import { ServerlessWorkflowDiagramEditorChannelApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { ServerlessWorkflowMermaidViewerChannelApi } from "@kie-tools/serverless-workflow-mermaid-viewer/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { SwfFeatureToggleChannelApi } from "./SwfFeatureToggleChannelApi";

/* TODO: ServerlessWorkflowCombinedEditorChannelApi: Please clean up unused imports. https://github.com/kiegroup/kie-tools/pull/1102#discussion_r935931108  */
export interface ServerlessWorkflowCombinedEditorChannelApi
  extends KogitoEditorChannelApi,
    SwfFeatureToggleChannelApi,
    SwfLanguageServiceChannelApi {}
