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
import { useParams } from "react-router-dom";
import { ScorecardEditorPage } from "../../EditorScorecard/templates";
import { getModelType, isSupportedModelType } from "../../../utils";
import { Model, PMML, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { useSelector } from "react-redux";
import { EmptyStateModelNotFound } from ".";
import { UnsupportedModelPage } from "../templates";

interface ModelParams {
  index?: string;
}

interface SingleEditorRouterProps {
  path: string;
}

export const SingleEditorRouter = (props: SingleEditorRouterProps) => {
  const { index } = useParams<ModelParams>();
  const models: Model[] | undefined = useSelector<PMML, Model[] | undefined>((state: PMML) => state.models);
  if (!models) {
    return <EmptyStateModelNotFound />;
  }
  const _index: number = index ? +index : -1;
  if (isNaN(_index) || _index < 0 || _index > models.length - 1) {
    return <EmptyStateModelNotFound />;
  }

  const model: Model = models[_index];
  const modelType: string | undefined = getModelType(model);
  const _isSupportedModelType: boolean = isSupportedModelType(model);

  return (
    <div>
      {!_isSupportedModelType && <UnsupportedModelPage path={props.path} model={model} />}
      {_isSupportedModelType && modelType === "Scorecard" && (
        <ScorecardEditorPage path={props.path} modelIndex={_index} model={model as Scorecard} />
      )}
    </div>
  );
};
