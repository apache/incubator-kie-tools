/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useMemo, useState } from "react";
import { useSelector } from "react-redux";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { CloseIcon } from "@patternfly/react-icons/dist/js/icons/close-icon";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/js/icons/warning-triangle-icon";
import { DataDictionary, PMML } from "@kie-tools/pmml-editor-marshaller";
import { Actions } from "../../../reducers";
import DataDictionaryContainer, { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { convertPMML2DD, convertToDataField } from "../dataDictionaryUtils";
import { Operation, useOperation } from "../../EditorScorecard";
import { useBatchDispatch, useHistoryService } from "../../../history";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicatorTooltip } from "../../EditorCore/atoms";

const DataDictionaryHandler = () => {
  const [isDataDictionaryOpen, setIsDataDictionaryOpen] = useState(false);
  const pmmlDataDictionary = useSelector<PMML, DataDictionary | undefined>((state: PMML) => state.DataDictionary);
  const dictionary = useMemo(() => convertPMML2DD(pmmlDataDictionary), [pmmlDataDictionary]);
  const { setActiveOperation } = useOperation();

  const { service, getCurrentState } = useHistoryService();
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
        optype: optype,
      },
    });
  };

  const addBatchFields = (fields: string[]) => {
    dispatch({
      type: Actions.AddBatchDataDictionaryFields,
      payload: {
        dataDictionaryFields: fields,
      },
    });
  };

  const deleteField = (index: number) => {
    //See https://issues.redhat.com/browse/FAI-443
    //if (window.confirm(`Delete Output "${dictionary[index].name}"?`)) {
    dispatch({
      type: Actions.DeleteDataDictionaryField,
      payload: {
        index,
      },
    });
    // }
  };

  const reorderFields = (oldIndex: number, newIndex: number) => {
    dispatch({
      type: Actions.ReorderDataDictionaryFields,
      payload: {
        oldIndex,
        newIndex,
      },
    });
  };

  const updateField = (index: number, originalName: string, field: DDDataField) => {
    dispatch({
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: index,
        dataField: convertToDataField(field),
        originalName: originalName,
      },
    });
  };

  const handleEditingPhase = (status: boolean) => {
    setActiveOperation(status ? Operation.UPDATE_DATA_DICTIONARY : Operation.NONE);
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(() => validationRegistry.get(Builder().forDataDictionary().build()), [dictionary]);

  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Data Dictionary
        </Title>
      </SplitItem>
      <SplitItem>
        <Button
          type="button"
          variant={ButtonVariant.plain}
          onClick={handleDataDictionaryToggle}
          data-title="DataDictionaryModalClose"
        >
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      {validations.length === 0 && (
        <Button variant="secondary" onClick={handleDataDictionaryToggle} data-title="DataDictionary">
          Set Data Dictionary
        </Button>
      )}
      {validations.length > 0 && (
        <ValidationIndicatorTooltip validations={validations}>
          <Button
            variant="secondary"
            icon={<WarningTriangleIcon size={"sm"} color={"orange"} />}
            onClick={handleDataDictionaryToggle}
            data-title="DataDictionary"
          >
            Set Data Dictionary
          </Button>
        </ValidationIndicatorTooltip>
      )}
      <Modal
        aria-label="data-dictionary"
        title="Data Dictionary"
        header={header}
        isOpen={isDataDictionaryOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
        data-title="DataDictionaryModal"
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
