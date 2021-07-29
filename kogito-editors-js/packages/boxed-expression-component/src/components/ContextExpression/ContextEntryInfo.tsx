/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./ContextEntryInfo.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { DataType } from "../../api";

export interface ContextEntryInfoProps {
  /** Context Entry info name */
  name: string;
  /** Context Entry info dataType */
  dataType: DataType;
  /** Callback to be executed when name or dataType get updated */
  onContextEntryUpdate: (name: string, dataType: DataType) => void;
  /** Label used for the popover triggered when editing info section */
  editInfoPopoverLabel?: string;
}

export const ContextEntryInfo: React.FunctionComponent<ContextEntryInfoProps> = ({
  name,
  dataType,
  onContextEntryUpdate,
  editInfoPopoverLabel,
}) => {
  const [entryName, setEntryName] = useState(name);

  const [entryDataType, setEntryDataType] = useState(dataType);

  useEffect(() => {
    setEntryName(name);
  }, [name]);

  useEffect(() => {
    setEntryDataType(dataType);
  }, [dataType]);

  const onEntryNameOrDataTypeUpdate = useCallback(
    ({ name, dataType }) => {
      setEntryName(name);
      setEntryDataType(dataType);
      onContextEntryUpdate(name, dataType);
    },
    [onContextEntryUpdate]
  );

  const renderEntryDefinition = useCallback(
    (additionalCssClass?: string) => (
      <div className={`entry-definition ${additionalCssClass}`}>
        <p className="entry-name pf-u-text-truncate" title={entryName}>
          {entryName}
        </p>
        <p className="entry-data-type pf-u-text-truncate" title={entryDataType}>
          ({entryDataType})
        </p>
      </div>
    ),
    [entryDataType, entryName]
  );

  const renderEntryDefinitionWithPopoverMenu = useMemo(
    () => (
      <EditExpressionMenu
        title={editInfoPopoverLabel}
        selectedExpressionName={entryName}
        selectedDataType={entryDataType}
        onExpressionUpdate={onEntryNameOrDataTypeUpdate}
      >
        {renderEntryDefinition("with-popover-menu")}
      </EditExpressionMenu>
    ),
    [editInfoPopoverLabel, entryDataType, entryName, onEntryNameOrDataTypeUpdate, renderEntryDefinition]
  );

  return (
    <div className="entry-info">
      {editInfoPopoverLabel ? renderEntryDefinitionWithPopoverMenu : renderEntryDefinition()}
    </div>
  );
};
