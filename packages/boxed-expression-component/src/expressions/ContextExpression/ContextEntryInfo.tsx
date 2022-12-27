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
import { useCallback, useMemo } from "react";
import { ExpressionDefinitionHeaderMenu } from "../ExpressionDefinitionHeaderMenu";
import { DmnBuiltInDataType, ExpressionDefinition } from "../../api";

export interface ContextEntryInfoProps {
  /** Context entry info id */
  id: string;
  /** Context Entry info name */
  name: string;
  /** Context Entry info dataType */
  dataType: DmnBuiltInDataType;
  /** Callback to be executed when name or dataType get updated */
  onContextEntryUpdate: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void;
  /** Label used for the popover triggered when editing info section */
  editInfoPopoverLabel?: string;
  isPopoverOpen?: boolean;
}

export const ContextEntryInfo: React.FunctionComponent<ContextEntryInfoProps> = ({
  id,
  name,
  dataType,
  onContextEntryUpdate,
  editInfoPopoverLabel,
  isPopoverOpen,
}) => {
  const onExpressionHeaderUpdated = useCallback(
    (args: Pick<ExpressionDefinition, "name" | "dataType">) => {
      onContextEntryUpdate(args);
    },
    [onContextEntryUpdate]
  );

  const renderEntryDefinition = useCallback(
    (args: { additionalCssClass?: string }) => (
      <div className={`entry-definition ${args.additionalCssClass}`}>
        <p className="entry-name pf-u-text-truncate" title={name}>
          {name}
        </p>
        <p className="entry-data-type pf-u-text-truncate" title={dataType}>
          ({dataType})
        </p>
      </div>
    ),
    [dataType, name]
  );

  const ref = React.useRef<HTMLDivElement>(null);

  const renderEntryDefinitionWithPopoverMenu = useMemo(
    () => (
      <ExpressionDefinitionHeaderMenu
        isPopoverOpen={isPopoverOpen}
        title={editInfoPopoverLabel}
        selectedExpressionName={name}
        selectedDataType={dataType}
        onExpressionHeaderUpdated={onExpressionHeaderUpdated}
      >
        {renderEntryDefinition({ additionalCssClass: "with-popover-menu" })}
      </ExpressionDefinitionHeaderMenu>
    ),
    [isPopoverOpen, editInfoPopoverLabel, name, dataType, onExpressionHeaderUpdated, renderEntryDefinition]
  );

  return (
    <div className={`${id} entry-info`} ref={ref}>
      {editInfoPopoverLabel ? renderEntryDefinitionWithPopoverMenu : renderEntryDefinition({})}
    </div>
  );
};
