import * as React from "react";
import { Modal, Button } from "@patternfly/react-core";
import { useHistory } from "react-router";

export function DownloadHubModal(props: any) {
  const history = useHistory();

  const back = e => {
    e.stopPropagation();
    history.goBack();
  };

  return (
    <div>
      <Modal
        title="The Kogito end-to-end hub allows you to access:"
        isOpen={true}
        isLarge={true}
        actions={[
          <Button key="confirm" variant="primary" onClick={back}>
            Download Installer
          </Button>,
          <Button key="cancel" variant="link" onClick={back}>
            Cancel
          </Button>
        ]}
      >
        <h1>VSCode</h1>
        <h1>Github</h1>
        <h1>Desktop app</h1>
        <h1>Business Modeler Preview</h1>
      </Modal>
    </div>
  );
}
