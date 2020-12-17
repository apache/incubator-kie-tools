import * as React from "react";
import { useMemo, useState } from "react";
import { useSelector } from "react-redux";
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
import { DataDictionary, FieldName, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions } from "../../../reducers";
import DataDictionaryContainer, { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { convertPMML2DD, convertToDataField } from "../dataDictionaryUtils";
import { HistoryContext, OperationContext } from "../../../PMMLEditor";
import { Operation } from "../../EditorScorecard";
import { useBatchDispatch } from "../../../history";

const DataDictionaryHandler = () => {
  const [isDataDictionaryOpen, setIsDataDictionaryOpen] = useState(false);
  const pmmlDataDictionary = useSelector<PMML, DataDictionary | undefined>((state: PMML) => state.DataDictionary);
  const dictionary = useMemo(() => convertPMML2DD(pmmlDataDictionary), [pmmlDataDictionary]);
  const { setActiveOperation } = React.useContext(OperationContext);

  const { service, getCurrentState } = React.useContext(HistoryContext);
  const dispatch = useBatchDispatch(service, getCurrentState);

  const handleDataDictionaryToggle = () => {
    setActiveOperation(Operation.NONE);
    setIsDataDictionaryOpen(!isDataDictionaryOpen);
  };

  const addField = (name: string, type: DDDataField["type"], optype: DDDataField["optype"]) => {
    dispatch({
      type: Actions.AddDataDictionaryField,
      payload: {
        name: name,
        type: type,
        optype: optype
      }
    });
  };

  const addBatchFields = (fields: string[]) => {
    dispatch({
      type: Actions.AddBatchDataDictionaryFields,
      payload: {
        dataDictionaryFields: fields
      }
    });
  };

  const deleteField = (index: number) => {
    dispatch({
      type: Actions.DeleteDataDictionaryField,
      payload: {
        index
      }
    });
  };

  const reorderFields = (oldIndex: number, newIndex: number) => {
    dispatch({
      type: Actions.ReorderDataDictionaryFields,
      payload: {
        oldIndex,
        newIndex
      }
    });
  };

  const updateField = (index: number, originalName: string, field: DDDataField) => {
    dispatch({
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: index,
        dataField: convertToDataField(field),
        originalName: originalName as FieldName
      }
    });
  };

  const handleEditingPhase = (status: boolean) => {
    setActiveOperation(status ? Operation.UPDATE_DATA_DICTIONARY : Operation.NONE);
  };

  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Data Dictionary
        </Title>
      </SplitItem>
      <SplitItem>
        <Button type="button" variant={ButtonVariant.plain} onClick={handleDataDictionaryToggle}>
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      <Button variant="secondary" onClick={handleDataDictionaryToggle}>
        Set Data Dictionary
      </Button>
      <Modal
        aria-label="data-dictionary"
        title="Data Dictionary"
        header={header}
        isOpen={isDataDictionaryOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
      >
        <DataDictionaryContainer
          dataDictionary={dictionary}
          onAdd={addField}
          onEdit={updateField}
          onDelete={deleteField}
          onReorder={reorderFields}
          onBatchAdd={addBatchFields}
          onEditingPhaseChange={handleEditingPhase}
        />
      </Modal>
    </>
  );
};

export default DataDictionaryHandler;
