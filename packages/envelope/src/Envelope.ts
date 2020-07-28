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

import { ApiDefinition, EnvelopeBus } from "@kogito-tooling/envelope-bus";
import { EnvelopeBusController } from "./EnvelopeBusController";
import { EnvelopeApiFactory } from "./EnvelopeApiFactory";

export class Envelope<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  ViewType,
  ContextType
> {
  constructor(
    bus: EnvelopeBus,
    private readonly envelopeBusController = new EnvelopeBusController<ApiToProvide, ApiToConsume>(bus)
  ) {}

  public get busClient() {
    return this.envelopeBusController.client;
  }

  public async start(
    viewDelegate: () => Promise<ViewType>,
    context: ContextType,
    apiFactory: EnvelopeApiFactory<ApiToProvide, ApiToConsume, ViewType, ContextType>
  ) {
    const view = await viewDelegate();

    const api = apiFactory.create({
      view: view,
      envelopeContext: context,
      envelopeBusController: this.envelopeBusController
    });

    this.envelopeBusController.startListening(api);
    return this.envelopeBusController;
  }
}
