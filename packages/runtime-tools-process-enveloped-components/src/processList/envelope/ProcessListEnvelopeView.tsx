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
import { ProcessListChannelApi, ProcessListInitArgs, ProcessListState } from "../api";
import ProcessList from "./components/ProcessList/ProcessList";
import "@patternfly/patternfly/patternfly.css";
import { ProcessInstanceState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface ProcessListEnvelopeViewApi {
  initialize: (initArgs: ProcessListInitArgs) => void;
}
interface Props {
  channelApi: MessageBusClientApi<ProcessListChannelApi>;
}

const defaultFilters = {
  status: [ProcessInstanceState.Active],
  businessKey: [],
};

const defaultSortBy = {
  lastUpdate: OrderBy.DESC,
};

export const ProcessListEnvelopeView = React.forwardRef<ProcessListEnvelopeViewApi, Props>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
  const [processInitialArgs, setProcessInitialArgs] = useState<ProcessListInitArgs>({
    initialState: {
      filters: defaultFilters,
      sortBy: defaultSortBy,
    } as ProcessListState,
    singularProcessLabel: "Process",
    pluralProcessLabel: "Processes",
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

  return (
    <React.Fragment>
      <ProcessList
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        channelApi={props.channelApi}
        initialState={processInitialArgs.initialState}
        singularProcessLabel={processInitialArgs?.singularProcessLabel}
        pluralProcessLabel={processInitialArgs?.pluralProcessLabel}
      />
    </React.Fragment>
  );
});

export default ProcessListEnvelopeView;
