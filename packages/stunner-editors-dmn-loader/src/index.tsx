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

import {
  BoxedExpressionEditor,
  BoxedExpressionEditorProps,
} from "@kie-tools/boxed-expression-component/dist/expressions";
import {
  ImportJavaClasses,
  GWTLayerService,
  JavaClass,
  JavaCodeCompletionService,
} from "@kie-tools/import-java-classes-component";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactDOM from "react-dom";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import {
  BeeGwtService,
  DmnDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  PmmlParam,
} from "@kie-tools/boxed-expression-component/dist/api";

export interface BoxedExpressionEditorWrapperProps {
  /** Identifier of the decision node, where the expression will be hold */
  decisionNodeId: string;
  /** All expression properties used to define it */
  expressionDefinition: ExpressionDefinition;
  /** The data type elements that can be used in the editor */
  dataTypes: DmnDataType[];
  /**
   * A boolean used for making (or not) the clear button available on the root expression
   * Note that this parameter will be used only for the root expression.
   *
   * Each expression (internally) has a `noClearAction` property (ExpressionDefinition interface).
   * You can set directly it for enabling or not the clear button for such expression.
   * */
  isResetSupportedOnRootExpression?: boolean;
  /** PMML parameters */
  pmmlParams?: PmmlParam[];
}

const BoxedExpressionEditorWrapper: React.FunctionComponent<BoxedExpressionEditorWrapperProps> = ({
  decisionNodeId,
  expressionDefinition,
  dataTypes,
  isResetSupportedOnRootExpression,
  pmmlParams,
}: BoxedExpressionEditorProps) => {
  const [expression, setExpression] = useState<ExpressionDefinition>(expressionDefinition);

  useEffect(() => {
    console.info("DMN Editor changed the expression. Updating internal state with:");
    console.info(JSON.stringify(expressionDefinition));
    setExpression(expressionDefinition);
  }, [expressionDefinition]);

  const beeGwtService: BeeGwtService = {
    openManageDataType(): void {
      window.beeApiWrapper?.openManageDataType();
    },
    onLogicTypeSelect(selectedLogicType: string): void {
      window.beeApiWrapper?.onLogicTypeSelect(selectedLogicType);
    },
    selectObject(uuid: string): void {
      window.beeApiWrapper?.selectObject(uuid);
    },
  };

  const setExpressionNotifyingUserAction = useCallback(
    (newExpressionAction: React.SetStateAction<ExpressionDefinition>) => {
      setExpression((prev) => {
        const n = typeof newExpressionAction === "function" ? newExpressionAction(prev) : newExpressionAction;

        console.info("Notifying DMN Editor that expression changed with:");
        console.info(JSON.stringify(n));
        const logicType = n.logicType;
        switch (logicType) {
          case ExpressionDefinitionLogicType.Literal:
            window.beeApiWrapper?.broadcastLiteralExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.Relation:
            window.beeApiWrapper?.broadcastRelationExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.Context:
            window.beeApiWrapper?.broadcastContextExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.DecisionTable:
            window.beeApiWrapper?.broadcastDecisionTableExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.Invocation:
            window.beeApiWrapper?.broadcastInvocationExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.List:
            window.beeApiWrapper?.broadcastListExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.Function:
            window.beeApiWrapper?.broadcastFunctionExpressionDefinition?.(n);
            break;
          case ExpressionDefinitionLogicType.Undefined:
            // Ignore
            break;
          default:
            assertUnreachable(logicType);
        }

        console.info("Notifying user action...");
        window.beeApiWrapper?.notifyUserAction();
        return n;
      });
    },
    []
  );

  // useElementsThatStopKeyboardEventsPropagation(
  //   window,
  //   useMemo(() => [".boxed-expression-provider"], [])
  // );

  const emptyRef = React.useRef<HTMLElement>(null);

  return (
    <BoxedExpressionEditor
      scrollableParentRef={emptyRef}
      beeGwtService={beeGwtService}
      decisionNodeId={decisionNodeId}
      expressionDefinition={expression}
      setExpressionDefinition={setExpressionNotifyingUserAction}
      dataTypes={dataTypes}
      isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />
  );
};

const renderBoxedExpressionEditor = (
  selector: string,
  decisionNodeId: string,
  expressionDefinition: ExpressionDefinition,
  dataTypes: DmnDataType[],
  isResetSupportedOnRootExpression: boolean,
  pmmlParams: PmmlParam[]
) => {
  ReactDOM.render(
    <BoxedExpressionEditorWrapper
      decisionNodeId={decisionNodeId}
      expressionDefinition={expressionDefinition}
      dataTypes={dataTypes}
      isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />,
    document.querySelector(selector)
  );
};

const ImportJavaClassesWrapper = () => {
  window.ImportJavaClassesAPI = {
    importJavaClasses: (javaClasses: JavaClass[]) => {
      window.ImportJavaClassesAPIWrapper?.importJavaClasses?.(javaClasses);
    },
  };

  const gwtLayerService: GWTLayerService = {
    importJavaClassesInDataTypeEditor: (javaClasses) => window.ImportJavaClassesAPI?.importJavaClasses?.(javaClasses),
  };

  const javaCodeCompletionService: JavaCodeCompletionService = {
    getClasses: (query: string) => window.envelope.javaCodeCompletionService.getClasses(query),
    getFields: (fullClassName: string) => window.envelope.javaCodeCompletionService.getAccessors(fullClassName, ""),
    isLanguageServerAvailable: () => window.envelope.javaCodeCompletionService.isLanguageServerAvailable(),
  };

  return <ImportJavaClasses gwtLayerService={gwtLayerService} javaCodeCompletionService={javaCodeCompletionService} />;
};

const renderImportJavaClasses = (selector: string) => {
  ReactDOM.render(<ImportJavaClassesWrapper />, document.querySelector(selector));
};

export { renderBoxedExpressionEditor, renderImportJavaClasses };

function assertUnreachable(logicType: never) {
  throw new Error("Shouldn't reach this point.");
}
