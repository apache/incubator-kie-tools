import * as React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { useSettingsDispatch } from "./SettingsContext";

export function SettingsButton() {
  const settingsDispatch = useSettingsDispatch();
  return (
    <Button
      variant={ButtonVariant.plain}
      onClick={() => settingsDispatch.open()}
      aria-label="Settings"
      className={"kogito-tooling--masthead-hoverable-dark"}
    >
      <CogIcon />
    </Button>
  );
}
