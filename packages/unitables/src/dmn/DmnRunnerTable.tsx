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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { NotificationSeverity } from "@kie-tools-core/notifications/dist/api";
import { dmnUnitablesDictionaries, dmnUnitablesI18n, DmnUnitablesI18nContext, dmnUnitablesI18nDefaults } from "../i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import "./style.css";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { DmnValidator } from "./DmnValidator";
import { useAnchoredUnitablesDrawerPanel } from "../core/DmnRunnerDrawerHooks";
import { Unitables, UnitablesApi } from "../core/Unitables";
import { DmnTableResults } from "../core/DmnTableResults";
import { TableOperation } from "@kie-tools/boxed-expression-component";

const PROPERTIES_ENTRY_PATH = "definitions.InputSet";

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DecisionResultMessage {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | object[] | string;

export type DmnSchemaProperties = { "x-dmn-type": string; type: string; $ref: string };

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: Result;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

interface Props {
  jsonSchema?: DmnSchema;
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  results?: Array<DecisionResult[] | undefined>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
}

export function DmnRunnerTable(props: Props) {
  const [rowCount, setRowCount] = useState<number>(props.inputRows?.length ?? 1);
  const [dmnRunnerTableError, setDmnRunnerTableError] = useState<boolean>(false);
  const dmnRunnerTableErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const unitablesRef = useRef<UnitablesApi>(null);
  const i18n = useMemo(() => {
    dmnUnitablesI18n.setLocale(dmnUnitablesI18nDefaults.locale ?? navigator.language);
    return dmnUnitablesI18n.getCurrent();
  }, []);
  const jsonSchemaBridge = useMemo(
    () => new DmnValidator(i18n).getBridge(props.jsonSchema ?? {}),
    [i18n, props.jsonSchema]
  );

  useEffect(() => {
    dmnRunnerTableErrorBoundaryRef.current?.reset();
  }, [props.jsonSchema]);

  const inputsContainerRef = useRef<HTMLDivElement>(null);
  const outputsContainerRef = useRef<HTMLDivElement>(null);
  const { drawerPanelDefaultSize, drawerPanelMinSize, drawerPanelMaxSize, forceDrawerPanelRefresh } =
    useAnchoredUnitablesDrawerPanel({
      inputsContainerRef,
      outputsContainerRef,
    });

  useEffect(() => {
    forceDrawerPanelRefresh();
  }, [forceDrawerPanelRefresh, props.inputRows, props.results]);

  const onRowNumberUpdate = useCallback((rowQtt: number, operation?: TableOperation, rowIndex?: number) => {
    setRowCount(rowQtt);
    if (unitablesRef.current && operation !== undefined && rowIndex !== undefined) {
      unitablesRef.current.operationHandler(operation, rowIndex);
    }
  }, []);

  return (
    <>
      {props.jsonSchema && (
        <I18nDictionariesProvider
          defaults={dmnUnitablesI18nDefaults}
          dictionaries={dmnUnitablesDictionaries}
          initialLocale={navigator.language}
          ctx={DmnUnitablesI18nContext}
        >
          {dmnRunnerTableError ? (
            dmnRunnerTableError
          ) : (
            <ErrorBoundary
              ref={dmnRunnerTableErrorBoundaryRef}
              setHasError={setDmnRunnerTableError}
              error={<DmnAutoTableError />}
            >
              <Drawer isInline={true} isExpanded={true} className={"unitables--drawer"}>
                <DrawerContent
                  panelContent={
                    <>
                      <DrawerPanelContent
                        isResizable={true}
                        minSize={rowCount > 0 ? drawerPanelMinSize : "30%"}
                        maxSize={drawerPanelMaxSize}
                        defaultSize={drawerPanelDefaultSize}
                      >
                        <div ref={outputsContainerRef}>
                          <DmnTableResults
                            jsonSchemaBridge={jsonSchemaBridge}
                            rowCount={rowCount}
                            results={props.results}
                            onRowNumberUpdate={onRowNumberUpdate}
                          />
                        </div>
                      </DrawerPanelContent>
                    </>
                  }
                >
                  <div ref={inputsContainerRef}>
                    <Unitables
                      ref={unitablesRef}
                      name={"DMN Runner Table"}
                      i18n={i18n}
                      jsonSchema={props.jsonSchema}
                      rowCount={rowCount}
                      openRow={props.openRow}
                      inputRows={props.inputRows}
                      setInputRows={props.setInputRows}
                      error={props.error}
                      setError={props.setError}
                      jsonSchemaBridge={jsonSchemaBridge}
                      propertiesEntryPath={PROPERTIES_ENTRY_PATH}
                      onRowNumberUpdate={onRowNumberUpdate}
                    />
                  </div>
                </DrawerContent>
              </Drawer>
            </ErrorBoundary>
          )}
        </I18nDictionariesProvider>
      )}
    </>
  );
}

function DmnAutoTableError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened on the DMN Runner Table</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
