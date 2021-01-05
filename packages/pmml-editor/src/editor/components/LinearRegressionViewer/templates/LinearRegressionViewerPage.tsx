/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { PageSection, PageSectionVariants } from "@patternfly/react-core";
import { EditorHeader } from "../../EditorCore/molecules";
import { Model, PMML, RegressionModel } from "@kogito-tooling/pmml-editor-marshaller";
import { getModelName } from "../../..";
import { useSelector } from "react-redux";
import "./LinearRegressionViewerPage.scss";
import { EmptyStateModelNotFound } from "../../EditorCore/organisms";
import { LinearRegressionViewAdaptor } from "../molecules";

interface LinearRegressionViewerPageProps {
  path: string;
  modelIndex: number;
}

export const LinearRegressionViewerPage = (props: LinearRegressionViewerPageProps) => {
  const model: RegressionModel | undefined = useSelector<PMML, RegressionModel | undefined>((state: PMML) => {
    const _model: Model | undefined = state.models ? state.models[props.modelIndex] : undefined;
    if (_model && _model instanceof RegressionModel) {
      const _regressionModel = _model as RegressionModel;
      if (_regressionModel.functionName === "regression" && _regressionModel.algorithmName === "linearRegression") {
        return _regressionModel;
      }
    }
    return undefined;
  });

  return (
    <div data-testid="editor-page" className={"editor"}>
      {!model && <EmptyStateModelNotFound />}
      {model && (
        <>
          <PageSection variant={PageSectionVariants.light} isFilled={false}>
            <EditorHeader modelName={getModelName(model)} />
          </PageSection>

          <PageSection isFilled={true} style={{ paddingTop: "0px" }}>
            <LinearRegressionViewAdaptor model={model} />
          </PageSection>
        </>
      )}
    </div>
  );
};
