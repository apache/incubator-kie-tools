import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import * as React from "react";
import { useSettings } from "./SettingsContext";

export function SettingsButton() {
  const settings = useSettings();
  return (
    <>
      <Button variant="plain" onClick={() => settings.open()} aria-label="Settings">
        <CogIcon />
      </Button>
    </>
  );
}
