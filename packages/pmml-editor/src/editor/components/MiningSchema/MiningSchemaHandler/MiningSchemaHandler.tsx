import * as React from "react";
import { useMemo, useState } from "react";
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
import { CloseIcon, WarningTriangleIcon } from "@patternfly/react-icons";
import MiningSchemaContainer from "../MiningSchemaContainer/MiningSchemaContainer";
import { DataDictionary, FieldName, MiningField, MiningSchema, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { useSelector } from "react-redux";
import { Actions } from "../../../reducers";
import { useBatchDispatch, useHistoryService } from "../../../history";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicatorTooltip } from "../../EditorCore/atoms";

interface MiningSchemaHandlerProps {
  miningSchema?: MiningSchema;
  modelIndex: number;
}

const MiningSchemaHandler = (props: MiningSchemaHandlerProps) => {
  const { miningSchema, modelIndex } = props;
  const [isMiningSchemaOpen, setIsMiningSchemaOpen] = useState(false);
  const dataDictionary = useSelector<PMML, DataDictionary | undefined>((state: PMML) => state.DataDictionary);

  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const addMiningField = (names: string[]) => {
    dispatch({
      type: Actions.AddMiningSchemaFields,
      payload: {
        modelIndex: modelIndex,
        names: names
      }
    });
  };

  const deleteMiningField = (index: number) => {
    if (window.confirm(`Delete Mining Field "${miningSchema?.MiningField[index].name}"?`)) {
      dispatch({
        type: Actions.DeleteMiningSchemaField,
        payload: {
          modelIndex: modelIndex,
          miningSchemaIndex: index,
          name: miningSchema?.MiningField[index].name
        }
      });
    }
  };

  const updateField = (index: number, originalName: FieldName | undefined, field: MiningField) => {
    dispatch({
      type: Actions.UpdateMiningSchemaField,
      payload: {
        modelIndex: modelIndex,
        miningSchemaIndex: index,
        ...field,
        originalName
      }
    });
  };

  const handleMiningSchemaToggle = () => {
    setIsMiningSchemaOpen(!isMiningSchemaOpen);
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .build()
      ),
    [modelIndex, miningSchema, dataDictionary]
  );

  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Mining Schema
        </Title>
      </SplitItem>
      <SplitItem>
        <Button
          type="button"
          variant={ButtonVariant.plain}
          onClick={handleMiningSchemaToggle}
          data-title="MiningSchemaModalClose"
        >
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      {validations.length === 0 && (
        <Button variant="secondary" onClick={handleMiningSchemaToggle} data-title="MiningSchema">
          Set Mining Schema
        </Button>
      )}
      {validations.length > 0 && (
        <ValidationIndicatorTooltip validations={validations}>
          <Button
            variant="secondary"
            icon={<WarningTriangleIcon size={"sm"} color={"orange"} />}
            onClick={handleMiningSchemaToggle}
            data-title="MiningSchema"
          >
            Set Mining Schema
          </Button>
        </ValidationIndicatorTooltip>
      )}
      <Modal
        aria-label="mining-schema"
        title="Mining Schema"
        header={header}
        isOpen={isMiningSchemaOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
        data-title="MiningSchemaModal"
      >
        <MiningSchemaContainer
          modelIndex={modelIndex}
          miningSchema={miningSchema}
          dataDictionary={dataDictionary}
          onAddField={addMiningField}
          onDeleteField={deleteMiningField}
          onUpdateField={updateField}
        />
      </Modal>
    </>
  );
};

export default MiningSchemaHandler;
