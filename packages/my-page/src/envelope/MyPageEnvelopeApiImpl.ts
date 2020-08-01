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

import { Association, MyPageChannelApi, MyPageEnvelopeApi, MyPageInitArgs } from "../api";
import { MyPage } from "./MyPage";
import { EnvelopeApiFactoryArgs } from "@kogito-tooling/envelope";
import { MyPageEnvelopeViewApi } from "./MyPageEnvelopeView";
import { MyPageEnvelopeContext } from "./MyPageContext";
import { MyPageFactory } from "./MyPageFactory";

export class MyPageEnvelopeApiImpl implements MyPageEnvelopeApi {
  private myPage: MyPage;

  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      MyPageEnvelopeApi,
      MyPageChannelApi,
      MyPageEnvelopeViewApi,
      MyPageEnvelopeContext
    >,
    private readonly myPageFactory: MyPageFactory
  ) {}

  public async myPage__init(association: Association, initArgs: MyPageInitArgs) {
    this.args.envelopeBusController.associate(association.origin, association.busId);
    this.myPage = this.myPageFactory.create(initArgs, this.args.envelopeBusController.client);
    await this.args.view.setPage(this.myPage);

    this.myPage.setText("Hello from the Envelope!");
  }

  public myPage__setText(text: string) {
    this.myPage.setText(text);
  }
}
