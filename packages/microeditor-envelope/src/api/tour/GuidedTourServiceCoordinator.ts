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

import { GuidedTourApi } from "./GuidedTourApi";
import { EnvelopeBusInnerMessageHandler } from "../../EnvelopeBusInnerMessageHandler";
import { UserInteraction, Tutorial, KogitoGuidedTour } from "@kogito-tooling/guided-tour";

export class GuidedTourServiceCoordinator {
  public exposeApi(messageBus: EnvelopeBusInnerMessageHandler): GuidedTourApi {
    return {
      refresh(userInteraction: UserInteraction): void {
        messageBus.notify_guidedTourRefresh(userInteraction);
      },
      registerTutorial(tutorial: Tutorial): void {
        messageBus.notify_guidedTourRegisterTutorial(tutorial);
      },
      isEnabled(): boolean {
        return KogitoGuidedTour.getInstance().isEnabled();
      }
    };
  }
}
