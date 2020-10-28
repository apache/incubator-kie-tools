/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useCallback } from "react";
import {
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Tooltip,
  TooltipPosition
} from "@patternfly/react-core";
import { Attribute } from "@kogito-tooling/pmml-editor-marshaller";
import { EmptyStateNoAttributes } from ".";
import "./CharacteristicAttributesForm.scss";

import { InfoCircleIcon, TrashIcon } from "@patternfly/react-icons";

interface CharacteristicAttributesFormProps {
  index: number | undefined;
  attributes: Attribute[];
  onRowDelete: (index: number) => void;
  onAddAttribute: () => void;
}

export const CharacteristicAttributesForm = (props: CharacteristicAttributesFormProps) => {
  const { attributes, onAddAttribute, onRowDelete } = props;

  const onDeleteAttribute = useCallback(
    (event, index) => {
      event.stopPropagation();
      onRowDelete(index);
    },
    [attributes]
  );

  return (
    <div>
      <DataList className="attributes__header" aria-label="attributes header">
        <DataListItem className="attributes__header__row" key={"none"} aria-labelledby="attributes-header">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={[
                <DataListCell key="0" width={4}>
                  <div>Attribute</div>
                </DataListCell>,
                <DataListCell key="1" width={2}>
                  <div>
                    <span className={"attributes__header__text"}>Partial Score</span>
                    <Tooltip position={TooltipPosition.top} content={<div>Score points awarded to the Attribute</div>}>
                      <InfoCircleIcon className={"attributes__header__icon"} />
                    </Tooltip>
                  </div>
                </DataListCell>,
                <DataListCell key="2" width={2}>
                  <div>
                    <span className={"attributes__header__text"}>Reason Code</span>
                    <Tooltip
                      position={TooltipPosition.top}
                      content={
                        <div>
                          Attribute's reason code. If the reason code is used at this level, it takes precedence over
                          the reason code attribute associated with the Characteristic
                        </div>
                      }
                    >
                      <InfoCircleIcon className={"attributes__header__icon"} />
                    </Tooltip>
                  </div>
                </DataListCell>,
                <DataListAction
                  id="delete-attribute-header"
                  aria-label="delete header"
                  aria-labelledby="delete-attribute-header"
                  key="3"
                  width={1}
                >
                  {/*This is a hack to ensure the column layout is correct*/}
                  <Button variant="link" icon={<TrashIcon />} isDisabled={true} style={{ visibility: "hidden" }}>
                    &nbsp;
                  </Button>
                </DataListAction>
              ]}
            />
          </DataListItemRow>
        </DataListItem>
      </DataList>
      <DataList aria-label="attributes list">
        {attributes.map((attribute, index) => {
          return (
            <DataListItem
              key={index}
              id={index.toString()}
              className="attributes__list-item"
              aria-labelledby={"attribute-" + index}
            >
              <DataListItemRow>
                <DataListItemCells
                  dataListCells={[
                    <DataListCell key="0" width={4}>
                      <div>{JSON.stringify(attribute.predicate, undefined, 2)}</div>
                    </DataListCell>,
                    <DataListCell key="1" width={2}>
                      <div>{attribute.partialScore}</div>
                    </DataListCell>,
                    <DataListCell key="2" width={2}>
                      <div>{attribute.reasonCode}</div>
                    </DataListCell>,
                    <DataListAction
                      id="delete-attribute"
                      aria-label="delete"
                      aria-labelledby="delete-attribute"
                      key="4"
                      width={1}
                    >
                      <Button variant="link" icon={<TrashIcon />} onClick={e => onDeleteAttribute(e, index)}>
                        &nbsp;
                      </Button>
                    </DataListAction>
                  ]}
                />
              </DataListItemRow>
            </DataListItem>
          );
        })}
      </DataList>
      {attributes.length === 0 && <EmptyStateNoAttributes createAttribute={onAddAttribute} />}
    </div>
  );
};
