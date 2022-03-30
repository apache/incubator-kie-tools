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

import * as React from "react";
import "./ImportJavaClassesWizardFirstStep.css";
import {
  Bullseye,
  DataList,
  DataListCell,
  DataListCheck,
  DataListItem,
  DataListItemRow,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  SearchInput,
  Spinner,
  Tooltip,
  Title,
} from "@patternfly/react-core";
import CubesIcon from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { useCallback, useState } from "react";
import { JavaClass } from "./model/JavaClass";
import { JavaCodeCompletionService } from "./services";

export interface ImportJavaClassesWizardFirstStepProps {
  /** Service class which contains all API methods to dialog with Java Code Completion Extension*/
  javaCodeCompletionService: JavaCodeCompletionService;
  /** Function to be called when adding a Java Class */
  onAddJavaClass: (fullClassName: string) => void;
  /** Function to be called when removing a Java Class */
  onRemoveJavaClass: (fullClassName: string) => void;
  /** List of the selected classes by user */
  selectedJavaClasses: JavaClass[];
}

export const ImportJavaClassesWizardFirstStep = ({
  javaCodeCompletionService,
  onAddJavaClass,
  onRemoveJavaClass,
  selectedJavaClasses,
}: ImportJavaClassesWizardFirstStepProps) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const [searchValue, setSearchValue] = useState("");
  const [retrievedJavaClassesNames, setRetrievedJavaClassesNames] = useState<string[]>([]);
  const [isRequestLoading, setRequestLoading] = useState(false);
  const [requestTimer, setRequestTimer] = useState<NodeJS.Timeout | undefined>(undefined);

  const retrieveJavaClasses = useCallback(
    (value: string) => {
      setRequestLoading(true);
      javaCodeCompletionService
        .getClasses(value)
        .then((javaCodeCompletionClasses) => {
          const retrievedClasses = javaCodeCompletionClasses.map((item) => item.fqcn);
          setRetrievedJavaClassesNames(retrievedClasses);
          setRequestLoading(false);
        })
        .catch((reason) => {
          setRetrievedJavaClassesNames([]);
          setRequestLoading(false);
          console.error(reason);
        });
    },
    [javaCodeCompletionService]
  );

  const handleSearchValueChange = useCallback(
    (value: string) => {
      setSearchValue(value);
      /* Managing debounce effect */
      if (requestTimer) {
        clearTimeout(requestTimer);
      }
      if (value.length > 2) {
        setRequestTimer(
          global.setTimeout(() => {
            retrieveJavaClasses(value);
          }, 1000)
        );
      } else {
        setRetrievedJavaClassesNames([]);
      }
    },
    [retrieveJavaClasses, requestTimer]
  );

  const handleClearSearch = useCallback(() => {
    setSearchValue("");
    setRetrievedJavaClassesNames([]);
  }, []);

  const handleDataListCheckChange = useCallback(
    (fullClassName: string) => {
      if (!selectedJavaClasses.map((javaClass) => javaClass.name).includes(fullClassName)) {
        onAddJavaClass(fullClassName);
      } else {
        onRemoveJavaClass(fullClassName);
      }
    },
    [selectedJavaClasses, onAddJavaClass, onRemoveJavaClass]
  );

  const isDataListChecked = useCallback(
    (fullClassName: string) => {
      if (selectedJavaClasses.map((javaClass) => javaClass.name).includes(fullClassName)) {
        return true;
      } else {
        return false;
      }
    },
    [selectedJavaClasses]
  );

  const dataListClassesSet = [
    ...new Set(selectedJavaClasses.map((value) => value.name).concat(retrievedJavaClassesNames)),
  ];

  return (
    <>
      <div className="fs-search-input">
        <Tooltip content={i18n.modalWizard.firstStep.input.tooltip} isVisible={searchValue.length < 3}>
          <SearchInput
            autoFocus
            onChange={handleSearchValueChange}
            onClear={handleClearSearch}
            placeholder={i18n.modalWizard.firstStep.input.placeholder}
            value={searchValue}
          />
        </Tooltip>
      </div>
      {isRequestLoading ? (
        <Bullseye>
          <Spinner isSVG={true} />
        </Bullseye>
      ) : retrievedJavaClassesNames.length > 0 || selectedJavaClasses.length > 0 ? (
        <DataList aria-label={"class-data-list"}>
          {dataListClassesSet.map((value) => (
            <DataListItem key={value} name={value}>
              <DataListItemRow>
                <DataListCheck
                  aria-labelledby={value}
                  checked={isDataListChecked(value)}
                  onChange={() => handleDataListCheckChange(value)}
                />
                <DataListCell>
                  <span id={value}>{value}</span>
                </DataListCell>
              </DataListItemRow>
            </DataListItem>
          ))}
        </DataList>
      ) : (
        <EmptyStep
          emptyStateBodyText={i18n.modalWizard.firstStep.emptyState.body}
          emptyStateTitleText={i18n.modalWizard.firstStep.emptyState.title}
        />
      )}
    </>
  );
};

const EmptyStep = ({
  emptyStateBodyText,
  emptyStateTitleText,
}: {
  emptyStateBodyText: string;
  emptyStateTitleText: string;
}) => {
  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel={"h6"} size={"md"}>
        {emptyStateTitleText}
      </Title>
      <EmptyStateBody>{emptyStateBodyText}</EmptyStateBody>
    </EmptyState>
  );
};
