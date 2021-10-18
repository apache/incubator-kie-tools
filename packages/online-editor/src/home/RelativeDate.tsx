import moment from "moment";
import * as React from "react";
import { useCallback, useState } from "react";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { useCancelableEffect } from "../common/Hooks";

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
