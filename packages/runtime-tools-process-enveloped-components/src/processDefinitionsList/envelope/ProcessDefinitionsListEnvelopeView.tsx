/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useImperativeHandle, useState, useMemo } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ProcessDefinitionsListChannelApi, ProcessDefinitionsListInitArgs } from "../api";
import ProcessDefinitionsList from "./components/ProcessDefinitionsList";
import ProcessDefinitionsListEnvelopeViewDriver from "./ProcessDefinitionsListEnvelopeViewDriver";
import "@patternfly/patternfly/patternfly.css";
import { ProcessDefinitionsListState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export interface ProcessDefinitionsListEnvelopeViewApi {
  initialize: (initArgs: ProcessDefinitionsListInitArgs) => void;
}
interface Props {
  channelApi: MessageBusClientApi<ProcessDefinitionsListChannelApi>;
}

const defaultFilters = {
  processNames: [],
};

export const ProcessDefinitionsListEnvelopeView = React.forwardRef<ProcessDefinitionsListEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const [processInitialArgs, setProcessInitialArgs] = useState<ProcessDefinitionsListInitArgs>({
      initialState: {
        filters: defaultFilters,
      } as ProcessDefinitionsListState,
      singularProcessLabel: "Process",
    });
    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: (initArgs) => {
          setEnvelopeConnectedToChannel(false);
          setProcessInitialArgs(initArgs);
          setEnvelopeConnectedToChannel(true);
        },
      }),
      []
    );

    const driver = useMemo(() => new ProcessDefinitionsListEnvelopeViewDriver(props.channelApi), [props.channelApi]);

    return (
      <React.Fragment>
        <ProcessDefinitionsList
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          driver={driver}
          initialState={processInitialArgs.initialState}
          singularProcessLabel={processInitialArgs?.singularProcessLabel}
        />
      </React.Fragment>
    );
  }
);

export default ProcessDefinitionsListEnvelopeView;
