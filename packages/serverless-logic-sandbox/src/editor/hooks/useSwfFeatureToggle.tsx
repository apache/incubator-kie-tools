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

import { KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { useStateAsSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import {
  ServerlessWorkflowCombinedEditorChannelApi,
  SwfFeatureToggle,
} from "@kie-tools/serverless-workflow-combined-editor/dist/api";
import { useEffect, useMemo, useState } from "react";
import { useSettings } from "../../settings/SettingsContext";

export function useSwfFeatureToggle(editor?: EmbeddedEditorRef): SwfFeatureToggle {
  const settings = useSettings();

  const [featureToggle, setFeatureToggle] = useState<SwfFeatureToggle>({
    stunnerEnabled: settings.featurePreview.config.stunnerEnabled,
  });

  const envelopeServer = useMemo(
    () =>
      editor?.getEnvelopeServer() as unknown as EnvelopeServer<
        ServerlessWorkflowCombinedEditorChannelApi,
        KogitoEditorEnvelopeApi
      >,
    [editor]
  );

  useEffect(() => {
    setFeatureToggle((prevState) => ({ ...prevState, stunnerEnabled: settings.featurePreview.config.stunnerEnabled }));
  }, [settings.featurePreview.config.stunnerEnabled]);

  useStateAsSharedValue(featureToggle, setFeatureToggle, envelopeServer?.shared?.kogitoSwfFeatureToggle_get);

  return featureToggle;
}
