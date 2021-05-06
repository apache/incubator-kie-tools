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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import {
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  CardHeaderMain,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import { ModelType } from "../../..";
import * as React from "react";
import { useCallback, useMemo, MouseEvent } from "react";
import "./ModelCard.scss";
import { ModelCardIcon } from "../atoms";
import { MODEL_NAME_NOT_SET } from "../../EditorCore/atoms";

interface ModelCardProps {
  index: number | undefined;
  modelName: string;
  modelType: ModelType;
  onDelete: (index: number) => void;
  onClick: (index: number) => void;
}

export const ModelCard = (props: ModelCardProps) => {
  const { index, modelName, modelType } = props;

  const onClickModel = useCallback(
    (e: any) => { //FIXME: tiago MouseEvent
      if (index !== undefined) {
        e.stopPropagation();
        props.onClick(index);
      }
    },
    [index]
  );

  const onDeleteModel = useCallback(
    (e: any) => { //FIXME: tiago MouseEvent
      if (index !== undefined) {
        e.stopPropagation();
        props.onDelete(index);
      }
    },
    [index]
  );

  const _modelName = useMemo(() => (modelName === "" ? MODEL_NAME_NOT_SET : modelName), [modelName]);

  return (
    <Card data-testid="model-card" isHoverable={true} className="model-card" onClick={onClickModel}>
      <CardHeader>
        <CardHeaderMain>
          <ModelCardIcon type={modelType} />
        </CardHeaderMain>
      </CardHeader>
      <Tooltip content={<div>{_modelName}</div>}>
        <CardTitle className="model-card__title">
          <span data-testid="model-card__title">{_modelName}</span>
        </CardTitle>
      </Tooltip>{" "}
      <CardBody>
        <div data-testid="model-card__model-type">{modelType}</div>
      </CardBody>
      <CardFooter>
        <Button data-testid="model-card__delete" variant="primary" onClick={onDeleteModel}>
          Delete
        </Button>
      </CardFooter>
    </Card>
  );
};
