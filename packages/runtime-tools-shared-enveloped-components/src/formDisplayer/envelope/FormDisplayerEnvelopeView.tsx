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

import * as React from "react";
import { useImperativeHandle, useRef, useState } from "react";
import isEmpty from "lodash/isEmpty";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/src/types";
import {
  FormDisplayerChannelApi,
  FormSubmitContext,
  FormSubmitResponse,
  FormDisplayerInitArgs,
  FormOpened,
} from "../api";
import { EmbeddedFormApi } from "./components/FormDisplayer/apis";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import FormDisplayer from "./components/FormDisplayer/FormDisplayer";
import ErrorBoundary from "./components/ErrorBoundary/ErrorBoundary";

export interface FormDisplayerEnvelopeViewApi {
  initForm: (args: FormDisplayerInitArgs) => void;
  startSubmit: (context: FormSubmitContext) => Promise<any>;
  notifySubmitResponse: (response: FormSubmitResponse) => void;
}

interface Props {
  channelApi: MessageBusClientApi<FormDisplayerChannelApi>;
}

export const FormDisplayerEnvelopeView = React.forwardRef<FormDisplayerEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [content, setContent] = useState<Form>();
    const [data, setData] = useState<any>();
    const [context, setContext] = useState<Record<string, any>>();
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);

    const formDisplayerApiRef = useRef<EmbeddedFormApi>({} as EmbeddedFormApi);

    useImperativeHandle(
      forwardedRef,
      () => {
        return {
          startSubmit: (context: FormSubmitContext): Promise<any> => {
            return new Promise<any>((resolve, reject) => {
              try {
                formDisplayerApiRef.current.beforeSubmit?.(context);
                resolve(formDisplayerApiRef.current.getFormData?.());
              } catch (err) {
                reject(err.message);
              }
            });
          },
          notifySubmitResponse: (response: FormSubmitResponse) => {
            formDisplayerApiRef.current.afterSubmit?.(response);
          },
          initForm: (args: FormDisplayerInitArgs) => {
            if (!isEmpty(args.form)) {
              setEnvelopeConnectedToChannel(false);
              setContent(args.form);
              setData(args.data ?? {});
              setContext(args.context ?? {});
              setEnvelopeConnectedToChannel(true);
            }
          },
        };
      },
      []
    );

    const onOpen = (opened: FormOpened) => {
      props.channelApi.notifications.notifyOnOpenForm.send(opened);
    };

    return (
      <ErrorBoundary notifyOnError={onOpen}>
        <FormDisplayer
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          content={content!}
          data={data}
          context={context!}
          onOpenForm={onOpen}
          ref={formDisplayerApiRef}
        />
      </ErrorBoundary>
    );
  }
);
