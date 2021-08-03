/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import "@patternfly/react-core/dist/styles/base-no-reset.css";
import "@patternfly/react-styles/css/components/Drawer/drawer.css";
import "./BoxedExpressionEditor.css";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import { ExpressionContainer } from "../ExpressionContainer";
import { hashfy, ResizerSupervisor } from "../Resizer";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { BoxedExpressionGlobalContext } from "../../context";
import * as _ from "lodash";
import { ExpressionProps, PMMLParams } from "../../api";

export interface BoxedExpressionEditorProps {
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

export const BoxedExpressionEditor: (props: BoxedExpressionEditorProps) => JSX.Element = (
  props: BoxedExpressionEditorProps
) => {
  const [currentlyOpenedHandlerCallback, setCurrentlyOpenedHandlerCallback] = useState(() => _.identity);
  const boxedExpressionEditorRef = useRef<HTMLDivElement>(null);
  const [expressionDefinition, setExpressionDefinition] = useState(props.expressionDefinition);
  const [supervisorHash, setSupervisorHash] = useState(hashfy(props.expressionDefinition));

  useEffect(() => {
    setExpressionDefinition(props.expressionDefinition);
    setSupervisorHash(hashfy(props.expressionDefinition));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.expressionDefinition]);

  const onExpressionChange = useCallback(
    (updatedExpression: ExpressionProps) => setExpressionDefinition(updatedExpression),
    []
  );

  return useMemo(
    () => (
      <I18nDictionariesProvider
        defaults={boxedExpressionEditorI18nDefaults}
        dictionaries={boxedExpressionEditorDictionaries}
        initialLocale={navigator.language}
        ctx={BoxedExpressionEditorI18nContext}
      >
        <BoxedExpressionGlobalContext.Provider
          value={{
            pmmlParams: props.pmmlParams,
            supervisorHash,
            setSupervisorHash,
            boxedExpressionEditorRef,
            currentlyOpenedHandlerCallback,
            setCurrentlyOpenedHandlerCallback,
          }}
        >
          <ResizerSupervisor>
            <div className="boxed-expression-editor" ref={boxedExpressionEditorRef}>
              <ExpressionContainer selectedExpression={expressionDefinition} onExpressionChange={onExpressionChange} />
            </div>
          </ResizerSupervisor>
        </BoxedExpressionGlobalContext.Provider>
      </I18nDictionariesProvider>
    ),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [expressionDefinition]
  );
};
