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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { CloseIcon } from "@patternfly/react-icons/dist/js/icons/close-icon";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/js/icons/warning-triangle-icon";
import MiningSchemaContainer from "../MiningSchemaContainer/MiningSchemaContainer";
import { DataDictionary, MiningField, MiningSchema, PMML } from "@kie-tools/pmml-editor-marshaller";
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
        names: names,
      },
    });
  };

  const deleteMiningField = (index: number) => {
    //See https://issues.redhat.com/browse/FAI-443
    //if (window.confirm(`Delete Mining Field "${miningSchema?.MiningField[index].name}"?`)) {
    dispatch({
      type: Actions.DeleteMiningSchemaField,
      payload: {
        modelIndex: modelIndex,
        miningSchemaIndex: index,
        name: miningSchema?.MiningField[index].name,
      },
    });
    // }
  };

  const updateField = (index: number, originalName: string | undefined, field: MiningField) => {
    dispatch({
      type: Actions.UpdateMiningSchemaField,
      payload: {
        modelIndex: modelIndex,
        miningSchemaIndex: index,
        ...field,
        originalName,
      },
    });
  };

  const handleMiningSchemaToggle = () => {
    setIsMiningSchemaOpen(!isMiningSchemaOpen);
  };

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forModel(modelIndex).forMiningSchema().build()),
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
          ouiaId="editor-close"
        >
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      {validations.length === 0 && (
        <Button
          variant="secondary"
          onClick={handleMiningSchemaToggle}
          data-title="MiningSchema"
          ouiaId="open-mining-schema-editor"
        >
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
            ouiaId="open-mining-schema-editor"
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
        ouiaId="mining-schema-editor"
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
