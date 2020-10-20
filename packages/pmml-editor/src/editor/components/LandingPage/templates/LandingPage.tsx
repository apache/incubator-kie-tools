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
import { useCallback, useMemo, useState } from "react";
import {
  Gallery,
  GalleryItem,
  PageSection,
  PageSectionVariants,
  Split,
  SplitItem,
  TextContent,
  Title
} from "@patternfly/react-core";
import { EmptyStateNoModels, LandingPageToolbar } from "../organisms";
import { v4 as uuid } from "uuid";
import { Model, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { useDispatch, useSelector } from "react-redux";
import { getModelName, isSupportedModelType } from "../../..";
import { ActionSelector, ModelCard } from "../molecules";
import { Actions } from "../../../reducers";

interface LandingPageProps {
  path: string;
}

export const LandingPage = (props: LandingPageProps) => {
  const models: Model[] | undefined = useSelector<PMML, Model[] | undefined>((state: PMML) => state.models);

  const dispatch = useDispatch();
  const [filter, setFilter] = useState("");
  const [showUnsupportedModels, setShowUnsupportedModels] = useState(true);
  const hasUnsupportedModels = useMemo(() => (models ?? []).find(model => !isSupportedModelType(model)) !== undefined, [
    models
  ]);

  const filterModels = useCallback((): Model[] => {
    const _lowerCaseFilter = filter.toLowerCase();
    const _filteredModels = models?.filter((_model: Model) => {
      const _modelName = getModelName(_model);
      const _isSupportedModelType = isSupportedModelType(_model);
      const _nameMatch = _modelName === undefined || _modelName.toLowerCase().includes(_lowerCaseFilter);
      const _supportMatch = showUnsupportedModels || _isSupportedModelType;
      return _nameMatch && _supportMatch;
    });
    return _filteredModels ?? [];
  }, [filter, showUnsupportedModels, models]);

  const filteredModels: Model[] = useMemo(() => filterModels(), [filter, showUnsupportedModels, models]);

  const onDelete = useCallback((_model: Model) => {
    dispatch({
      type: Actions.DeleteModel,
      payload: {
        model: _model
      }
    });
  }, []);

  return (
    <div data-testid="landing-page">
      <PageSection variant={PageSectionVariants.light}>
        <Split>
          <SplitItem isFilled={true}>
            <TextContent>
              <Title size="3xl" headingLevel="h2">
                {props.path}
              </Title>
            </TextContent>
          </SplitItem>
          <SplitItem>
            <ActionSelector />
          </SplitItem>
        </Split>
        <LandingPageToolbar
          onFilter={setFilter}
          hasUnsupportedModels={hasUnsupportedModels}
          showUnsupportedModels={showUnsupportedModels}
          onShowUnsupportedModels={setShowUnsupportedModels}
        />
      </PageSection>

      <PageSection isFilled={true}>
        <section>
          {filteredModels.length > 0 && (
            <Gallery hasGutter={true}>
              {filteredModels.map(model => (
                <GalleryItem key={uuid()} data-testid="landing-page__model-card">
                  <ModelCard model={model} onDelete={onDelete} />
                </GalleryItem>
              ))}
            </Gallery>
          )}
          {filteredModels.length === 0 && (
            <EmptyStateNoModels
              createModel={() => {
                window.alert("TODO: Create Model");
              }}
            />
          )}
        </section>
      </PageSection>
    </div>
  );
};
