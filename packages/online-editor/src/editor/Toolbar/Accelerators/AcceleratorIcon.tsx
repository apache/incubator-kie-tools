import React from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

type Props = {
  iconUrl?: string;
};

export function AcceleratorIcon(props: Props) {
  return props.iconUrl ? (
    <Icon isInline style={{ verticalAlign: "middle" }}>
      <img src={props.iconUrl} />
    </Icon>
  ) : (
    <span>🚀</span>
  );
}
