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

import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/BoxedExpressionEditor";
import { OnExpressionChange } from "@kie-tools/boxed-expression-component/dist/BoxedExpressionEditorContext";
import {
  GWTLayerService,
  ImportJavaClasses,
  JavaClass,
  JavaCodeCompletionService,
} from "@kie-tools/import-java-classes-component";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactDOM from "react-dom";
import {
  BeeGwtService,
  BoxedExpression,
  DmnBuiltInDataType,
  DmnDataType,
  Normalized,
  PmmlDocument,
} from "@kie-tools/boxed-expression-component/dist/api";

import { GwtExpressionDefinition } from "./types";
import { dmnExpressionToGwtExpression, gwtExpressionToDmnExpression, gwtLogicType } from "./mapping";
import { DmnLatestModel, getMarshaller } from "@kie-tools/dmn-marshaller";
import { updateExpression } from "./tmpDuplicateCode__updateExpression";
import { FeelIdentifiers } from "@kie-tools/dmn-feel-antlr4-parser";

export interface BoxedExpressionEditorWrapperProps {
  /** Identifier of the decision node, where the expression will be hold */
  expressionHolderId: string;
  /** All expression properties used to define it */
  gwtExpression: GwtExpressionDefinition;
  /** The data type elements that can be used in the editor */
  dataTypes: DmnDataType[];
  /**
   * A boolean used for making (or not) the "Reset" button available on the root expression
   * Note that this parameter will be used only for the root expression.
   * */
  isResetSupportedOnRootExpression?: boolean;
  /** PMML parameters */
  pmmlDocuments?: PmmlDocument[];
  /** BoxedExpressionWrapper root node */
  boxedExpressionEditorRootNode: Element | null;
  /** The DMN XML */
  dmnXml: string;
  /** A boolean used to hide DMN 1.4 expressions. */
  hideDmn14BoxedExpressions?: boolean;
}

const BoxedExpressionEditorWrapper: React.FunctionComponent<BoxedExpressionEditorWrapperProps> = ({
  expressionHolderId,
  gwtExpression,
  dataTypes,
  isResetSupportedOnRootExpression,
  pmmlDocuments,
  boxedExpressionEditorRootNode,
  dmnXml,
  hideDmn14BoxedExpressions,
}) => {
  const [expressionWrapper, setExpressionWrapper] = useState<{
    source: "gwt" | "react";
    expression: Normalized<BoxedExpression> | undefined;
    widthsById: Map<string, number[]>;
  }>({ source: "gwt", ...gwtExpressionToDmnExpression(gwtExpression) });

  useEffect(() => {
    setExpressionWrapper({ source: "gwt", ...gwtExpressionToDmnExpression(gwtExpression) });
  }, [gwtExpression]);

  useEffect(() => {
    console.log("Expression is changed. Source is: " + expressionWrapper.source);
    console.log(JSON.stringify(expressionWrapper.expression));

    if (expressionWrapper.source === "react") {
      console.log("Sending expression update to GWT layer.");
      window.beeApiWrapper?.updateExpression(
        dmnExpressionToGwtExpression(expressionWrapper.widthsById, expressionWrapper.expression)
      );
    }
  }, [expressionWrapper]);

  const beeGwtService: BeeGwtService = {
    getDefaultExpressionDefinition(logicType, dataType, isRoot) {
      const defaultExpression = gwtExpressionToDmnExpression(
        window.beeApiWrapper?.getDefaultExpressionDefinition(gwtLogicType(logicType), dataType)
      );
      if (isRoot === false) {
        defaultExpression.expression["@_label"] = undefined;
      }
      return defaultExpression;
    },
    openDataTypePage(): void {
      window.beeApiWrapper?.openDataTypePage();
    },
    selectObject(uuid: string): void {
      window.beeApiWrapper?.selectObject(uuid);
    },
  };

  const setExpressionNotifyingUserAction = useCallback<OnExpressionChange>((onExpressionChange) => {
    setExpressionWrapper((prevState) => {
      return {
        source: "react",
        expression:
          typeof onExpressionChange.setExpressionAction === "function"
            ? onExpressionChange.setExpressionAction(prevState.expression as any)
            : onExpressionChange.setExpressionAction,
        widthsById: prevState.widthsById,
      };
    });
  }, []);

  const setWidthsByIdNotifyingUserAction = useCallback(
    (newWidthsByIdAction: React.SetStateAction<Map<string, number[]>>) => {
      setExpressionWrapper((prevState) => ({
        source: "react",
        expression: prevState.expression,
        widthsById:
          typeof newWidthsByIdAction === "function"
            ? newWidthsByIdAction(prevState.widthsById as any)
            : newWidthsByIdAction,
      }));
    },
    []
  );

  const emptyRef = React.useRef<HTMLElement>(null);

  // Stop propagation to Editor and forward keydown events down the tree;
  useEffect(() => {
    const listener = (ev: KeyboardEvent) => {
      if (!ev.ctrlKey && !ev.metaKey) {
        ev.stopPropagation();
      }
    };

    boxedExpressionEditorRootNode?.addEventListener("keydown", listener);
    boxedExpressionEditorRootNode?.addEventListener("keyup", listener);
    boxedExpressionEditorRootNode?.addEventListener("keypress", listener);

    return () => {
      boxedExpressionEditorRootNode?.removeEventListener("keydown", listener);
      boxedExpressionEditorRootNode?.removeEventListener("keyup", listener);
      boxedExpressionEditorRootNode?.removeEventListener("keypress", listener);
    };
  }, [boxedExpressionEditorRootNode]);

  // BEGIN (feelVariables)
  //
  // These Hooks maintain an up-to-date copy of the DMN JSON to be used for a new instance of
  // FeelVariables when the FeelInput component requests for an updated version of it.
  // This happens when a user enters a FEEL cell on the Boxed Expression Editor.

  const modelWhenRendered = useRef<DmnLatestModel | null>(null);

  useEffect(() => {
    modelWhenRendered.current = getMarshaller(dmnXml, { upgradeTo: "latest" }).parser.parse();
  }, [dmnXml]);

  useEffect(() => {
    const drgElementIndex = (modelWhenRendered.current!.definitions.drgElement ?? []).findIndex((d) => {
      return d["@_id"] === expressionHolderId;
    });

    updateExpression({
      drgElementIndex,
      expression: expressionWrapper.expression!,
      definitions: modelWhenRendered.current!.definitions,
    });
  }, [expressionHolderId, expressionWrapper.expression]);

  const onRequestFeelVariables = useMemo(() => {
    if (!modelWhenRendered) {
      return undefined;
    }

    return () =>
      new FeelIdentifiers({
        _readonly_dmnDefinitions: modelWhenRendered.current!.definitions,
        _readonly_externalDefinitions: new Map(),
      });
  }, []);

  // END (feelVariables)

  return (
    <BoxedExpressionEditor
      scrollableParentRef={emptyRef}
      beeGwtService={beeGwtService}
      expressionHolderId={expressionHolderId}
      expressionHolderName={expressionWrapper.expression?.["@_label"] || ""}
      expressionHolderTypeRef={expressionWrapper.expression?.["@_typeRef"] || DmnBuiltInDataType.Undefined}
      dataTypes={dataTypes}
      isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
      pmmlDocuments={pmmlDocuments}
      onRequestFeelIdentifiers={onRequestFeelVariables}
      expression={expressionWrapper.expression}
      onExpressionChange={setExpressionNotifyingUserAction}
      widthsById={expressionWrapper.widthsById}
      onWidthsChange={setWidthsByIdNotifyingUserAction}
      hideDmn14BoxedExpressions={hideDmn14BoxedExpressions}
    />
  );
};

const renderBoxedExpressionEditor = (
  selector: string,
  expressionHolderId: string,
  gwtExpression: GwtExpressionDefinition,
  dataTypes: DmnDataType[],
  isResetSupportedOnRootExpression: boolean,
  pmmlDocuments: PmmlDocument[],
  dmnXml: string
) => {
  const boxedExpressionEditorRootNode = document.querySelector(selector);
  ReactDOM.render(
    <BoxedExpressionEditorWrapper
      expressionHolderId={expressionHolderId}
      gwtExpression={gwtExpression}
      dataTypes={dataTypes}
      isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
      pmmlDocuments={pmmlDocuments}
      boxedExpressionEditorRootNode={boxedExpressionEditorRootNode}
      dmnXml={dmnXml}
      hideDmn14BoxedExpressions={true}
    />,
    boxedExpressionEditorRootNode
  );
};

const unmountBoxedExpressionEditor = (selector: string) => {
  const boxedExpressionEditorRootNode = document.querySelector(selector);
  ReactDOM.unmountComponentAtNode(boxedExpressionEditorRootNode!);
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

export { renderBoxedExpressionEditor, renderImportJavaClasses, unmountBoxedExpressionEditor };
