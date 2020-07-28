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

import { EnvelopeBus, MessageBusClient } from "@kogito-tooling/envelope-bus";
import { Envelope, EnvelopeApiFactory, EnvelopeApiFactoryArgs } from "@kogito-tooling/envelope";
import * as React from "react";
import { useImperativeHandle, useState } from "react";
import * as ReactDOM from "react-dom";
import { KogitoPageChannelApi, KogitoPageEnvelopeApi, PageInitArgs, Association } from "@kogito-tooling/page-envelope-protocol";

// tslint:disable-next-line:no-empty-interface
export interface KogitoPageContext {}

export interface PageFactory {
  create(initArgs: PageInitArgs, channelApi: MessageBusClient<KogitoPageChannelApi>): Page;
}

export class KogitoPageEnvelopeApiFactory
  implements EnvelopeApiFactory<KogitoPageEnvelopeApi, KogitoPageChannelApi, PageEnvelopeViewRef, KogitoPageContext> {
  constructor(private readonly pageFactory: PageFactory) {}

  public create(
    args: EnvelopeApiFactoryArgs<KogitoPageEnvelopeApi, KogitoPageChannelApi, PageEnvelopeViewRef, KogitoPageContext>
  ): KogitoPageEnvelopeApi {
    return {
      init: async (association: Association, initArgs: PageInitArgs) => {
        args.envelopeBusController.associate(association.origin, association.busId);
        const page = this.pageFactory.create(initArgs, args.envelopeBusController.client);
        await args.view.setPage(page);

        page.setText("Hello from the Envelope!");
      }
    };
  }
}

export interface PageEnvelopeViewRef {
  setPage(page: Page): Promise<void>;
}

export const PageEnvelopeView = React.forwardRef((props, forwardedRef) => {
  const [page, setPage] = useState<Page>();

  useImperativeHandle(forwardedRef, () => ({ setPage }), []);

  return (
    <>
      {page && (
        <div style={{ margin: "10px" }}>
          <h1>This is a Kogito Page.</h1>
          <br />
          <h4>Page contents are inside the red border:</h4>
          <div style={{ border: "2px solid red" }}>{page.af_componentRoot()}</div>
        </div>
      )}
    </>
  );
});

//

export interface PageApi {
  setText(text: string): void;
}

export interface Page extends PageApi {
  af_componentRoot(): React.ReactNode;
}

export function init(args: { container: HTMLElement; bus: EnvelopeBus; pageFactory: PageFactory }) {
  const pageContext = {};

  const apiFactory = new KogitoPageEnvelopeApiFactory(args.pageFactory);

  const envelope = new Envelope<KogitoPageEnvelopeApi, KogitoPageChannelApi, PageEnvelopeViewRef, KogitoPageContext>(
    args.bus
  );

  const pageEnvelopeViewDelegate = async () => {
    const ref = React.createRef<PageEnvelopeViewRef>();
    return new Promise<PageEnvelopeViewRef>(res =>
      ReactDOM.render(<PageEnvelopeView ref={ref} />, args.container, () => res(ref.current!))
    );
  };

  return envelope.start(pageEnvelopeViewDelegate, pageContext, apiFactory);
}
