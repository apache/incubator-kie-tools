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

import { ApiDefinition, EnvelopeBus } from "@kie-tools-core/envelope-bus/dist/api";
import { EnvelopeClient } from "@kie-tools-core/envelope-bus/dist/envelope";
import { EnvelopeApiFactory } from "./EnvelopeApiFactory";
import { ContainerType } from "./api";

export interface EnvelopeDivConfig {
  containerType: ContainerType.DIV;
  envelopeId: string;
}

export interface EnvelopeIFrameConfig {
  containerType: ContainerType.IFRAME;
}

export class Envelope<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  ViewType,
  ContextType,
> {
  constructor(
    bus: EnvelopeBus,
    config: EnvelopeDivConfig | EnvelopeIFrameConfig = { containerType: ContainerType.IFRAME },
    private readonly envelopeClient = new EnvelopeClient<ApiToProvide, ApiToConsume>(
      bus,
      config.containerType === ContainerType.DIV ? config.envelopeId : undefined
    )
  ) {}

  public get channelApi() {
    return this.envelopeClient.channelApi;
  }

  public get shared() {
    return this.envelopeClient.shared;
  }

  public async start(
    viewDelegate: () => Promise<() => ViewType>,
    envelopeContext: ContextType,
    apiFactory: EnvelopeApiFactory<ApiToProvide, ApiToConsume, ViewType, ContextType>
  ) {
    const apiImpl = apiFactory.create({
      viewDelegate,
      envelopeContext,
      envelopeClient: this.envelopeClient,
    });

    this.envelopeClient.startListening(apiImpl);

    return this.envelopeClient;
  }
}
