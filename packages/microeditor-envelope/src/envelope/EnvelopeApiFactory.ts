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

import { ApiDefinition } from "@kogito-tooling/microeditor-envelope-protocol";
import { EnvelopeBusController } from "./EnvelopeBusController";

export interface EnvelopeApiFactoryArgs<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  ViewType,
  ContextType
> {
  view: ViewType;
  envelopeContext: ContextType;
  envelopeBusController: EnvelopeBusController<ApiToProvide, ApiToConsume>;
}

export interface EnvelopeApiFactory<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  ViewType,
  ContextType
> {
  create(args: EnvelopeApiFactoryArgs<ApiToProvide, ApiToConsume, ViewType, ContextType>): ApiToProvide;
}
