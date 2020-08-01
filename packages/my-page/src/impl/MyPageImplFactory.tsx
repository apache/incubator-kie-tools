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

import { MyPageApi, MyPageChannelApi, MyPageInitArgs } from "../api";
import { MessageBusClient } from "@kogito-tooling/envelope-bus";
import * as React from "react";
import { MyPageImpl } from "./MyPageImpl";
import { MyPageFactory } from "../envelope";

export class MyPageImplFactory implements MyPageFactory {
  public create(initArgs: MyPageInitArgs, channelApi: MessageBusClient<MyPageChannelApi>) {
    const ref = React.createRef<MyPageApi>();
    return {
      setText: (text: string) => ref.current!.setText(text),
      af_componentRoot: () => {
        return <MyPageImpl initArgs={initArgs} channelApi={channelApi} ref={ref} />;
      }
    };
  }
}
