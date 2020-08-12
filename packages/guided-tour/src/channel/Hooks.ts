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

import { useEffect } from "react";
import { KogitoGuidedTour } from "..";
import { GuidedTourEnvelopeApi } from "../api";
import { MessageBusClient } from "@kogito-tooling/envelope-bus/dist/api";

export function useGuidedTourPositionProvider(
  messageBusClient: MessageBusClient<GuidedTourEnvelopeApi>,
  iframeRef: React.RefObject<HTMLIFrameElement>
) {
  useEffect(() => {
    KogitoGuidedTour.getInstance().registerPositionProvider((selector: string) =>
      messageBusClient.request("receive_guidedTourElementPositionRequest", selector).then(position => {
        const parentRect = iframeRef.current?.getBoundingClientRect();
        KogitoGuidedTour.getInstance().onPositionReceived(position, parentRect);
      })
    );
  }, [messageBusClient]);
}
