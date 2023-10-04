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

import React, { useEffect, useImperativeHandle, useMemo, useState } from "react";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { CloudEventFormChannelApi, CloudEventFormDefaultValues, CloudEventFormInitArgs } from "../api";
import CloudEventForm from "./components/CloudEventForm/CloudEventForm";
import { CloudEventFormEnvelopeViewDriver } from "./CloudEventFormEnvelopeViewDriver";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";

export interface CloudEventFormEnvelopeViewApi {
  initialize: (args: CloudEventFormInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<CloudEventFormChannelApi>;
}

export const CloudEventFormEnvelopeView = React.forwardRef<CloudEventFormEnvelopeViewApi, Props & OUIAProps>(
  ({ channelApi, ouiaId }, forwardedRef) => {
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const [isNewInstanceEvent, setIsNewInstanceEvent] = useState<boolean>(false);
    const [defaultValues, setDefaultValues] = useState<CloudEventFormDefaultValues>();

    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: (args) => {
          setEnvelopeConnectedToChannel(true);
          setIsNewInstanceEvent(args.isNewInstanceEvent);
          setDefaultValues(args.defaultValues);
        },
      }),
      []
    );

    useEffect(() => {
      setIsLoading(false);
    }, [isEnvelopeConnectedToChannel]);

    const driver = useMemo(() => new CloudEventFormEnvelopeViewDriver(channelApi), [channelApi]);

    if (isLoading) {
      return (
        <Bullseye
          {...componentOuiaProps(
            /* istanbul ignore next */
            (ouiaId ? ouiaId : "cloud-event-form-envelope-view") + "-loading-spinner",
            "cloud-event-form",
            true
          )}
        >
          <KogitoSpinner spinnerText={`Loading cloud event form...`} />
        </Bullseye>
      );
    }

    return (
      <Card>
        <CardBody>
          <CloudEventForm driver={driver} isNewInstanceEvent={isNewInstanceEvent} defaultValues={defaultValues} />
        </CardBody>
      </Card>
    );
  }
);

export default CloudEventFormEnvelopeView;
