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

import { EnvelopeBus } from "@kogito-tooling/envelope-bus";
import { Envelope } from "@kogito-tooling/envelope";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { MyPageChannelApi, MyPageEnvelopeApi } from "../api";
import { MyPageFactory } from "./MyPageFactory";
import { MyPageEnvelopeContext } from "./MyPageContext";
import { MyPageEnvelopeView, MyPageEnvelopeViewApi } from "./MyPageEnvelopeView";
import { MyPageEnvelopeApiImpl } from "./MyPageEnvelopeApiImpl";

export function init(args: { container: HTMLElement; bus: EnvelopeBus; myPageFactory: MyPageFactory }) {
  const pageContext = {};

  const envelope = new Envelope<MyPageEnvelopeApi, MyPageChannelApi, MyPageEnvelopeViewApi, MyPageEnvelopeContext>(
    args.bus
  );

  const pageEnvelopeViewDelegate = async () => {
    const ref = React.createRef<MyPageEnvelopeViewApi>();
    return new Promise<MyPageEnvelopeViewApi>(res =>
      ReactDOM.render(<MyPageEnvelopeView ref={ref} />, args.container, () => res(ref.current!))
    );
  };

  return envelope.start(pageEnvelopeViewDelegate, pageContext, {
    create(apiFactoryArgs) {
      return new MyPageEnvelopeApiImpl(apiFactoryArgs, args.myPageFactory);
    }
  });
}
