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
  BoxedExpressionEditorGWTService,
  BoxedExpressionEditorProps,
  ContextProps,
  DataTypeProps,
  DecisionTableProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  PMMLParams,
  RelationProps,
} from "@kie-tools/boxed-expression-component";
import { ImportJavaClasses, ImportJavaClassGWTService, JavaClass } from "@kie-tools/import-java-classes-component";
import * as React from "react";
import { useEffect, useState } from "react";
import * as ReactDOM from "react-dom";

export interface BoxedExpressionEditorWrapperProps {
  /** Identifier of the decision node, where the expression will be hold */
  decisionNodeId: string;
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /** The data type elements that can be used in the editor */
  dataTypes: DataTypeProps[];
  /**
   * A boolean used for making (or not) the clear button available on the root expression
   * Note that this parameter will be used only for the root expression.
   *
   * Each expression (internally) has a `noClearAction` property (ExpressionProps interface).
   * You can set directly it for enabling or not the clear button for such expression.
   * */
  clearSupportedOnRootExpression?: boolean;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

const BoxedExpressionWrapper: React.FunctionComponent<BoxedExpressionEditorWrapperProps> = ({
  decisionNodeId,
  expressionDefinition,
  dataTypes,
  clearSupportedOnRootExpression,
  pmmlParams,
}: BoxedExpressionEditorProps) => {
  const [updatedDefinition, setExpressionDefinition] = useState<ExpressionProps>(expressionDefinition);

  useEffect(() => {
    setExpressionDefinition({ logicType: LogicType.Undefined });
    setTimeout(() => {
      setExpressionDefinition(expressionDefinition);
    }, 0);
  }, [expressionDefinition]);

  //The wrapper defines these function in order to keep expression definition state updated,
  //And to propagate such definition to DMN Editor (GWT world), by calling beeApiWrapper APIs
  const boxedExpressionEditorGWTService: BoxedExpressionEditorGWTService = {
    notifyUserAction(): void {
      window.beeApiWrapper?.notifyUserAction();
    },
    openManageDataType(): void {
      window.beeApiWrapper?.openManageDataType();
    },
    onLogicTypeSelect(selectedLogicType: string): void {
      window.beeApiWrapper?.onLogicTypeSelect(selectedLogicType);
    },
    resetExpressionDefinition: (definition: ExpressionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.resetExpressionDefinition?.(definition);
    },
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastLiteralExpressionDefinition?.(definition);
    },
    broadcastRelationExpressionDefinition: (definition: RelationProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastRelationExpressionDefinition?.(definition);
    },
    broadcastContextExpressionDefinition: (definition: ContextProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastContextExpressionDefinition?.(definition);
    },
    broadcastListExpressionDefinition: (definition: ListProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastListExpressionDefinition?.(definition);
    },
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastInvocationExpressionDefinition?.(definition);
    },
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastFunctionExpressionDefinition?.(definition);
    },
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastDecisionTableExpressionDefinition?.(definition);
    },
  };

  return (
    <BoxedExpressionEditor
      boxedExpressionEditorGWTService={boxedExpressionEditorGWTService}
      decisionNodeId={decisionNodeId}
      expressionDefinition={updatedDefinition}
      dataTypes={dataTypes}
      clearSupportedOnRootExpression={clearSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />
  );
};

const renderBoxedExpressionEditor = (
  selector: string,
  decisionNodeId: string,
  expressionDefinition: ExpressionProps,
  dataTypes: DataTypeProps[],
  clearSupportedOnRootExpression: boolean,
  pmmlParams: PMMLParams
) => {
  ReactDOM.render(
    <BoxedExpressionWrapper
      decisionNodeId={decisionNodeId}
      expressionDefinition={expressionDefinition}
      dataTypes={dataTypes}
      clearSupportedOnRootExpression={clearSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />,
    document.querySelector(selector)
  );
};

export interface ImportJavaClassesWrapperProps {
  /** Button disabled status */
  buttonDisabledStatus: boolean;
  /** Button tooltip message */
  buttonTooltipMessage?: string;
}

const ImportJavaClassesWrapper = ({ buttonDisabledStatus, buttonTooltipMessage }: ImportJavaClassesWrapperProps) => {
  window.ImportJavaClassesAPI = {
    importJavaClasses: (javaClasses: JavaClass[]) => {
      window.ImportJavaClassesAPIWrapper?.importJavaClasses?.(javaClasses);
    },
  };
  /** BEGIN - TEMPORARY MOCK CODE TO TEST COMPONENT INSIDE THE EDITOR - TO BE REMOVED */
  window.envelopeMock = {
    lspGetClassServiceMocked: (value: string) => lspGetClassServiceMocked(value),
    lspGetClassFieldsServiceMocked: (className: string) => lspGetClassFieldsServiceMocked(className),
  };

  const lspGetClassServiceMocked = (value: string) => {
    /* Mocked data retrieved from LSP Service */
    const booClassesList = ["org.kie.test.kogito.Book", "org.kie.test.kogito.Boom"];
    const bookClassesList = ["org.kie.test.kogito.Book"];
    const boomClassesList = ["org.kie.test.kogito.Boom"];

    /* Temporary mocks managing */
    if (value === "Boo") {
      return booClassesList;
    } else if (value === "Book") {
      return bookClassesList;
    } else if (value === "Boom") {
      return boomClassesList;
    } else {
      return [];
    }
  };
  const lspGetClassFieldsServiceMocked = async (className: string) => {
    /* Mocked data retrieved from LSP Service */
    const bookClassFieldsList = new Map<string, string>();
    bookClassFieldsList.set("author", "org.kie.test.kogito.Author");
    bookClassFieldsList.set("title", "java.lang.String");
    bookClassFieldsList.set("year", "java.lang.Integer");
    bookClassFieldsList.set("boom", "org.kie.test.kogito.Boom");
    const boomClassFieldsList = new Map<string, string>();
    boomClassFieldsList.set("startTime", "java.util.Date");
    boomClassFieldsList.set("big", "java.lang.Boolean");
    boomClassFieldsList.set("color", "java.lang.String");
    boomClassFieldsList.set("countdown", "java.time.Duration");
    const authorClassFieldsList = new Map<string, string>();
    authorClassFieldsList.set("age", "int");
    authorClassFieldsList.set("name", "java.lang.String");

    await delay();

    /* Temporary mocks managing */
    if (className === "org.kie.test.kogito.Book") {
      return bookClassFieldsList;
    } else if (className === "org.kie.test.kogito.Boom") {
      return boomClassFieldsList;
    } else if (className === "org.kie.test.kogito.Author") {
      return authorClassFieldsList;
    } else {
      return new Map<string, string>();
    }
  };

  const delay = () => new Promise((res) => setTimeout(res, Math.random() * (4000 - 750) + 1000));
  /** END TEMPORARY MOCK CODE TO TEST COMPONENT INSIDE THE EDITOR - TO BE REMOVED */

  const importJavaClassesGWTService: ImportJavaClassGWTService = {
    handleOnWizardImportButtonClick: (javaClasses) => window.ImportJavaClassesAPI?.importJavaClasses?.(javaClasses),
  };

  return (
    <ImportJavaClasses
      buttonDisabledStatus={buttonDisabledStatus}
      buttonTooltipMessage={buttonTooltipMessage}
      importJavaClassesGWTService={importJavaClassesGWTService}
    />
  );
};

const renderImportJavaClasses = (selector: string, buttonDisabledStatus: boolean, buttonTooltipMessage: string) => {
  ReactDOM.render(
    <ImportJavaClassesWrapper
      buttonDisabledStatus={buttonDisabledStatus}
      buttonTooltipMessage={buttonTooltipMessage}
    />,
    document.querySelector(selector)
  );
};

export { renderBoxedExpressionEditor, renderImportJavaClasses };
