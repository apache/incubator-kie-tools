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
import { Button, Card, CardBody, CardFooter, CardHeader, CardHeaderMain } from "@patternfly/react-core";
import { coalesce, getModelType } from "../../../utils";
import * as React from "react";
import { Model } from "@kogito-tooling/pmml-editor-marshaller";
import "./ModelCard.scss";
import { ModelCardIcon, ModelCardTitle } from "../atoms";

interface ModelCardProps {
  model: Model;
  onDelete: (model: Model) => void;
}

export const ModelCard = (props: ModelCardProps) => {
  const { model } = props;
  const modelType: string = coalesce(getModelType(model), "<Unknown>");

  return (
    <Card data-testid="model-card" isHoverable={true} className="model-card">
      <CardHeader>
        <CardHeaderMain>
          <ModelCardIcon model={model} />
        </CardHeaderMain>
      </CardHeader>
      <ModelCardTitle model={model} />
      <CardBody>
        <div data-testid="model-card__model-type">{modelType}</div>
      </CardBody>
      <CardFooter>
        <Button data-testid="model-card__delete" variant="primary" onClick={e => props.onDelete(model)}>
          Delete
        </Button>
      </CardFooter>
    </Card>
  );
};
