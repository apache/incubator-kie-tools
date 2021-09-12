import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { SettingsModal } from "./SettingsModal";
import * as React from "react";
import { useState } from "react";

export function SettingsButton() {
  const [isSettingsOpen, setSettingsOpen] = useState(false);
  return (
    <>
      <Button
        variant="plain"
        onClick={() => {
          setSettingsOpen(true);
        }}
        aria-label="Settings"
      >
        <CogIcon />
      </Button>
      <Modal
        title="Settings"
        isOpen={isSettingsOpen}
        onClose={() => setSettingsOpen(false)}
        variant={ModalVariant.large}
      >
        <div style={{ height: "calc(100vh * 0.5)" }} className={"kogito-tooling--setings-modal-content"}>
          <SettingsModal />
        </div>
      </Modal>
    </>
  );
}
