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

import { NgElement, WithProperties } from "@angular/elements";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import "..";

export const pingPongEnvelopViewRenderDiv = (container: HTMLElement, envelopeId: string) => {
  const element = document.createElement("ping-pong-angular");
  element.setAttribute("containerType", ContainerType.DIV);
  element.setAttribute("envelopeId", envelopeId);
  container.appendChild(element);
  return Promise.resolve();
};

declare global {
  interface HTMLElementTagNameMap {
    "ping-pong-angular": NgElement & WithProperties<{ containerType: ContainerType; envelopeid: string }>;
  }
}
