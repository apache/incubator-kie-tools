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
import { useCallback, useMemo, useState } from "react";
import { PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { Gallery, GalleryItem } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { EmptyStateNoModels } from "../organisms";
import { v4 as uuid } from "uuid";
import { Model, PMML } from "@kie-tools/pmml-editor-marshaller";
import { useSelector } from "react-redux";
import { getModelName, getModelType, isSupportedModelType, ModelType } from "../../..";
import { LandingPageHeader, LandingPageToolbar, ModelCard } from "../molecules";
import { Actions } from "../../../reducers";
import { useNavigate } from "react-router-dom";
import { useBatchDispatch, useHistoryService } from "../../../history";

interface LandingPageProps {
  path: string;
}

export const LandingPage = (props: LandingPageProps) => {
  const navigate = useNavigate();
  const { service, getCurrentState } = useHistoryService();
  const dispatch = useBatchDispatch(service, getCurrentState);

  const [filter, setFilter] = useState("");
  const [showUnsupportedModels, setShowUnsupportedModels] = useState(true);

  const models: Model[] | undefined = useSelector<PMML, Model[] | undefined>((state: PMML) => state.models);
  const hasUnsupportedModels = useMemo(
    () => (models ?? []).find((model) => !isSupportedModelType(model)) !== undefined,
    [models]
  );

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

  const goToModel = useCallback(
    (index: number) => {
      navigate({
        pathname: "editor/" + index,
      });
    },
    [navigate]
  );

  const onDelete = useCallback((index: number, modelName: string) => {
    //See https://issues.redhat.com/browse/FAI-443
    //if (window.confirm(`Delete Model "${modelName}"?`)) {
    dispatch({
      type: Actions.DeleteModel,
      payload: {
        modelIndex: index,
      },
    });
    //}
  }, []);

  return (
    <div data-testid="landing-page">
      <PageSection variant={PageSectionVariants.light}>
        <LandingPageHeader title={props.path} />
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
              {filteredModels.map((model) => {
                //model should always be a member of models at this point.
                const index: number | undefined = models?.indexOf(model);
                const modelName: string = getModelName(model);
                const modelType: ModelType = getModelType(model);

                return (
                  <GalleryItem key={uuid()} data-testid="landing-page__model-card">
                    <ModelCard
                      index={index}
                      modelName={modelName}
                      modelType={modelType}
                      onClick={goToModel}
                      onDelete={(_index) => onDelete(_index, modelName)}
                    />
                  </GalleryItem>
                );
              })}
            </Gallery>
          )}
          {filteredModels.length === 0 && (
            <EmptyStateNoModels
              createModel={() => {
                /*NOP*/
              }}
            />
          )}
        </section>
      </PageSection>
    </div>
  );
};
