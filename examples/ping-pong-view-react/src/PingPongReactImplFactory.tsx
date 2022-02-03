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

import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import * as React from "react";
import { PingPongReactImpl } from "./PingPongReactImpl";
import { PingPongFactory } from "@kie-tools-examples/ping-pong-view/dist/envelope";
import { PingPongApi, PingPongChannelApi, PingPongInitArgs } from "@kie-tools-examples/ping-pong-view/dist/api";

export class PingPongReactImplFactory implements PingPongFactory {
  constructor(private setView: React.Dispatch<React.SetStateAction<React.ReactElement>>) {}

  public create(initArgs: PingPongInitArgs, channelApi: MessageBusClientApi<PingPongChannelApi>) {
    const ref = React.createRef<PingPongApi>();

    this.setView(<PingPongReactImpl initArgs={initArgs} channelApi={channelApi} ref={ref} />);

    return () => ref.current;
  }
}
