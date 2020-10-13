import * as React from "react";
import { Button, Modal, ModalVariant } from "@patternfly/react-core";
import { useState } from "react";
import DataDictionaryContainer from "../DataDictionaryContainer/DataDictionaryContainer";

const DataDictionaryHandler = () => {
  const [isDataDictionaryOpen, setIsDataDictionaryOpen] = useState(false);
  const handleDataDictionaryToggle = () => {
    setIsDataDictionaryOpen(!isDataDictionaryOpen);
  };
  return (
    <>
      <Button variant="secondary" onClick={handleDataDictionaryToggle}>
        Set Data Dictionary
      </Button>
      <Modal
        aria-label="data-dictionary"
        title="Data Dictionary"
        isOpen={isDataDictionaryOpen}
        onClose={handleDataDictionaryToggle}
        variant={ModalVariant.large}
        onEscapePress={() => false}
        actions={[
          <Button key="confirm" variant="primary" onClick={handleDataDictionaryToggle}>
            Done
          </Button>
        ]}
      >
        <DataDictionaryContainer />
      </Modal>
    </>
  );
};

export default DataDictionaryHandler;
