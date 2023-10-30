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

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { BoxedExpressionEditorContextProvider } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionRoot.css";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { ResizingWidthsContextProvider } from "../../resizing/ResizingWidthsContext";
import { BeeTable } from "./BeeTable";

export function StandaloneBeeTable<R extends object>(
  props: BeeTableProps<R> & { scrollableParentRef: React.RefObject<HTMLElement> }
) {
  const dataTypes = useMemo(() => {
    return [];
  }, []);

  const setExpression = useCallback(() => {
    // Empty on purpose.
  }, []);

  const expression = useMemo<ExpressionDefinition>(() => {
    return {
      id: generateUuid(),
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    };
  }, []);

  return (
    <div className="expression-container" data-testid="standalone-bee-table">
      <div className="expression-container-box">
        <div className={`standalone-bee-table ${props.tableId}`}>
          <I18nDictionariesProvider
            defaults={boxedExpressionEditorI18nDefaults}
            dictionaries={boxedExpressionEditorDictionaries}
            initialLocale={navigator.language}
            ctx={BoxedExpressionEditorI18nContext}
          >
            <BoxedExpressionEditorContextProvider
              scrollableParentRef={props.scrollableParentRef}
              dataTypes={dataTypes}
              decisionNodeId={""}
              expressionDefinition={expression}
              setExpressionDefinition={setExpression}
            >
              <ResizingWidthsContextProvider>
                <BeeTable {...props} />
              </ResizingWidthsContextProvider>
            </BoxedExpressionEditorContextProvider>
          </I18nDictionariesProvider>
        </div>
      </div>
    </div>
  );
}
