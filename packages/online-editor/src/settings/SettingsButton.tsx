import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SettingsModalBody } from "./SettingsModalBody";
import * as React from "react";
import { useSettings } from "./SettingsContext";

export function SettingsButton() {
  const settings = useSettings();
  return (
    <>
      <Button variant="plain" onClick={() => settings.open()} aria-label="Settings">
        <CogIcon />
      </Button>
      <Modal title="Settings" isOpen={settings.isOpen} onClose={settings.close} variant={ModalVariant.large}>
        <div style={{ height: "calc(100vh * 0.5)" }} className={"kogito-tooling--setings-modal-content"}>
          <SettingsModalBody />
        </div>
      </Modal>
    </>
  );
}
