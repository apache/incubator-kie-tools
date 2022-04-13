/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import moment from "moment";
import * as React from "react";
import { useCallback, useState } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { useCancelableEffect } from "../reactExt/Hooks";

export function RelativeDate(props: { date: Date }) {
  const [dateToDisplay, setDateToDisplay] = useState(moment(props.date).fromNow());

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const interval = setInterval(() => {
          if (canceled.get()) {
            return;
          }
          setDateToDisplay(moment(props.date).fromNow());
        }, 1000);

        return () => {
          clearInterval(interval);
        };
      },
      [props.date]
    )
  );

  return (
    <Tooltip content={props.date.toLocaleString()}>
      <span>{dateToDisplay}</span>
    </Tooltip>
  );
}
