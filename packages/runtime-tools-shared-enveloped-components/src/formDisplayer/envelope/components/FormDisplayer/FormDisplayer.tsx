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

import React, { useCallback, useEffect, useImperativeHandle, useState } from "react";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { BallBeat } from "react-pure-loaders";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/src/types";
import { FormOpened, FormOpenedState } from "../../../api";
import ReactFormRenderer from "../ReactFormRenderer/ReactFormRenderer";
import HtmlFormRenderer from "../HtmlFormRenderer/HtmlFormRenderer";
import "../styles.css";
import { FormConfig, EmbeddedFormApi, InternalFormDisplayerApi, InternalFormDisplayerApiImpl } from "./apis";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface FormDisplayerProps {
  isEnvelopeConnectedToChannel: boolean;
  content: Form;
  data: any;
  onOpenForm: (opened: FormOpened) => void;
  context: Record<string, string>;
}

export const FormDisplayer = React.forwardRef<EmbeddedFormApi, FormDisplayerProps & OUIAProps>(
  ({ isEnvelopeConnectedToChannel, content, data, context, onOpenForm, ouiaId, ouiaSafe }, forwardedRef) => {
    const [source, setSource] = useState<string>();
    const [resources, setResources] = useState<any>();
    const [formData, setFormData] = useState<string>();
    const [formApi, setFormApi] = useState<InternalFormDisplayerApi>({} as InternalFormDisplayerApi);
    const [isExecuting, setIsExecuting] = useState<boolean>(false);

    const doOpenForm = (config: FormConfig): EmbeddedFormApi => {
      const api: EmbeddedFormApi = {};
      setFormApi(new InternalFormDisplayerApiImpl(api, config.onOpen!));
      return api;
    };

    const canDisplayForm = useCallback(() => {
      return isEnvelopeConnectedToChannel && !isExecuting && source;
    }, [isEnvelopeConnectedToChannel, isExecuting, source]);

    useEffect(() => {
      window.Form = {
        openForm: doOpenForm,
      };
    }, []);

    useEffect(() => {
      /* istanbul ignore else */
      if (isEnvelopeConnectedToChannel) {
        setSource(content.source);
        setResources(content.configuration.resources);
        setFormData(data);
      }
    }, [isEnvelopeConnectedToChannel, content, data]);

    useEffect(() => {
      if (isEnvelopeConnectedToChannel && formApi) {
        formApi.onOpen({
          data: formData,
          context: context,
        });
        setTimeout(() => {
          onOpenForm({
            state: FormOpenedState.OPENED,
            size: {
              height: document.body.scrollHeight,
              width: document.body.scrollWidth,
            },
          });
        }, 500);
      }
    }, [formApi]);

    useImperativeHandle(forwardedRef, () => formApi, [formApi]);

    return (
      <div {...componentOuiaProps(ouiaId, "form-displayer", ouiaSafe)}>
        {canDisplayForm() ? (
          <div id={"inner-form-container"}>
            {content.formInfo && content.formInfo.type === "TSX" ? (
              <ReactFormRenderer source={source!} resources={resources} setIsExecuting={setIsExecuting} />
            ) : (
              <HtmlFormRenderer source={source!} resources={resources} />
            )}
          </div>
        ) : (
          <Bullseye className="kogito-form-displayer__ball-beats">
            <BallBeat color={"#000000"} loading={!isEnvelopeConnectedToChannel} />
          </Bullseye>
        )}
      </div>
    );
  }
);

export default FormDisplayer;
