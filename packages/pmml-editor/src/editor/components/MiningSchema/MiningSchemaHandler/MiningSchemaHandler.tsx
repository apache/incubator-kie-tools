import * as React from "react";
import {
  Button,
  ButtonVariant,
  Modal,
  ModalVariant,
  Split,
  SplitItem,
  Title,
  TitleSizes
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import { Operation } from "../../EditorScorecard";
import MiningSchemaContainer from "../MiningSchemaContainer/MiningSchemaContainer";
import { useState } from "react";

interface MiningSchemaHandlerProps {
  activeOperation: Operation;
  setActiveOperation?: (operation: Operation) => void;
}

const MiningSchemaHandler = ({ activeOperation }: MiningSchemaHandlerProps) => {
  const [isMiningSchemaOpen, setIsMiningSchemaOpen] = useState(false);
  const handleMiningSchemaToggle = () => {
    setIsMiningSchemaOpen(!isMiningSchemaOpen);
  };
  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Mining Schema
        </Title>
      </SplitItem>
      <SplitItem>
        <Button type="button" variant={ButtonVariant.plain} onClick={handleMiningSchemaToggle}>
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      <Button variant="secondary" isDisabled={activeOperation !== Operation.NONE} onClick={handleMiningSchemaToggle}>
        Set Mining Schema
      </Button>
      <Modal
        aria-label="data-dictionary"
        title="Data Dictionary"
        header={header}
        isOpen={isMiningSchemaOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
      >
        <MiningSchemaContainer />
      </Modal>
    </>
  );
};

export default MiningSchemaHandler;
