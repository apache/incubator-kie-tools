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
import { CardTitle, Tooltip } from "@patternfly/react-core";
import { coalesce, getModelName } from "../../../utils";
import * as React from "react";
import { Model } from "@kogito-tooling/pmml-editor-marshaller";
import "./ModelCardTitle.scss";

interface ModelTitleProps {
  model: Model;
}

export const ModelCardTitle = (props: ModelTitleProps) => {
  const { model } = props;
  const modelName: string = coalesce(getModelName(model), "<Undefined>");

  return (
    <Tooltip content={<div>{modelName}</div>}>
      <CardTitle className="model-card__title">
        <span data-testid="model-card__title">{modelName}</span>
      </CardTitle>
    </Tooltip>
  );
};
