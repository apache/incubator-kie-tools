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

import {
  Action,
  BeeGwtService,
  BoxedExpression,
  DmnDataType,
  generateUuid,
  PmmlDocument,
} from "@kie-tools/boxed-expression-component/dist/api";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/BoxedExpressionEditor";
import { FeelIdentifiers } from "@kie-tools/dmn-feel-antlr4-parser";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { PMMLDocumentData } from "@kie-tools/pmml-editor-marshaller/dist/api";
import { PMMLFieldData } from "@kie-tools/pmml-editor-marshaller/dist/api/PMMLFieldData";
import { PMMLModelData } from "@kie-tools/pmml-editor-marshaller/dist/api/PMMLModelData";
import {
  AnomalyDetectionModel,
  AssociationModel,
  BaselineModel,
  BayesianNetworkModel,
  ClusteringModel,
  GaussianProcessModel,
  GeneralRegressionModel,
  MiningModel,
  Model,
  NaiveBayesModel,
  NearestNeighborModel,
  NeuralNetwork,
  PMML,
  RegressionModel,
  RuleSetModel,
  Scorecard,
  SequenceModel,
  SupportVectorMachineModel,
  TextModel,
  TimeSeriesModel,
  TreeModel,
} from "@kie-tools/pmml-editor-marshaller/dist/marshaller/model/pmml4_4";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as RF from "reactflow";
import { builtInFeelTypes } from "../dataTypes/BuiltInFeelTypes";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { isStruct } from "../dataTypes/DataTypeSpec";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { NodeIcon } from "../icons/Icons";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { updateExpression } from "../mutations/updateExpression";
import { updateExpressionWidths } from "../mutations/updateExpressionWidths";
import { DmnEditorTab } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { getDefaultColumnWidth } from "@kie-tools/boxed-expression-component/dist/resizing/WidthsToFitData";
import { getDefaultBoxedExpression } from "./getDefaultBoxedExpression";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { renameDrgElement } from "../mutations/renameNode";
import { OnExpressionChange } from "@kie-tools/boxed-expression-component/dist/BoxedExpressionEditorContext";
import { updateDrgElementType } from "../mutations/updateDrgElementType";
import { VariableChangedArgs } from "@kie-tools/boxed-expression-component/dist/api/ExpressionChange";
import {
  isIdentifierReferencedInSomeExpression,
  RefactorConfirmationDialog,
} from "../refactor/RefactorConfirmationDialog";
import { EvaluationHighlightsBadge } from "../evaluationHighlights/EvaluationHighlightsBadge";
import { useDmnEditor } from "../DmnEditorContext";

export function BoxedExpressionScreen({ container }: { container: React.RefObject<HTMLElement> }) {
  const { externalModelsByNamespace } = useExternalModels();

  const settings = useSettings();
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const thisDmn = useDmnEditorStore((s) => s.dmn);

  const activeDrgElementId = useDmnEditorStore((s) => s.boxedExpressionEditor.activeDrgElementId);
  const isPropertiesPanelOpen = useDmnEditorStore((s) => s.boxedExpressionEditor.propertiesPanel.isOpen);

  const externalDmnsByNamespace = useDmnEditorStore(
    (s) => s.computed(s).getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns
  );
  const dataTypesTree = useDmnEditorStore((s) => s.computed(s).getDataTypes(externalModelsByNamespace).dataTypesTree);
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const externalPmmlsByNamespace = useDmnEditorStore(
    (s) => s.computed(s).getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).pmmls
  );
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());
  const drdIndex = useDmnEditorStore((s) => s.computed(s).getDrdIndex());

  const externalDmnModelsByNamespaceMap = useDmnEditorStore((s) =>
    s.computed(s).getExternalDmnModelsByNamespaceMap(externalModelsByNamespace)
  );

  const { evaluationResultsByNodeId } = useDmnEditor();
  const isEvaluationHighlightsEnabled = useDmnEditorStore((s) => s.diagram.overlays.enableEvaluationHighlights);

  const onRequestFeelIdentifiers = useCallback(() => {
    return new FeelIdentifiers({
      _readonly_dmnDefinitions: dmnEditorStoreApi.getState().dmn.model.definitions,
      _readonly_externalDefinitions: externalDmnModelsByNamespaceMap,
    });
  }, [dmnEditorStoreApi, externalDmnModelsByNamespaceMap]);

  const drgElementIndex = useMemo(() => {
    if (!activeDrgElementId) {
      return undefined;
    }

    return (thisDmn.model.definitions.drgElement ?? []).findIndex((e) => e["@_id"] === activeDrgElementId);
  }, [activeDrgElementId, thisDmn.model.definitions.drgElement]);

  const drgElement = useMemo(() => {
    if (drgElementIndex === undefined) {
      return undefined;
    }

    const drgElement = thisDmn.model.definitions.drgElement?.[drgElementIndex];
    if (!(drgElement?.__$$element === "decision" || drgElement?.__$$element === "businessKnowledgeModel")) {
      return undefined;
    }

    return drgElement;
  }, [drgElementIndex, thisDmn.model.definitions.drgElement]);

  // BEGIN (setState batching for `expression` and `widthsById`)
  //
  // These hooks are responsible for building the `expression` and `widthsById` values, passed
  // to the BoxedExpressionEditor component.
  // More than that, they are responsible for maintaining an up-to-date ref for each one of
  // those values, so that batching works normally without having the `onChange` handlers be
  // recalculated, breaking batching.
  const widthsById = useMemo(() => {
    return (
      thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[drdIndex]["di:extension"]?.[
        "kie:ComponentsWidthsExtension"
      ]?.["kie:ComponentWidths"] ?? []
    ).reduce((acc, c) => {
      if (c["@_dmnElementRef"] === undefined) {
        return acc;
      } else {
        return acc.set(
          c["@_dmnElementRef"],
          (c["kie:width"] ?? []).map((vv) => vv.__$$text)
        );
      }
    }, new Map<string, number[]>());
  }, [drdIndex, thisDmn.model.definitions]);

  const expression = useMemo(() => {
    if (!drgElement) {
      return undefined;
    }

    return {
      boxedExpression: drgElementToBoxedExpression(drgElement),
      drgElementIndex,
      drgElement,
      drgElementType: drgElement.__$$element,
    };
  }, [drgElement, drgElementIndex]);

  const widthsByIdRef = useRef<Map<string, number[]>>(widthsById);
  const boxedExpressionRef = useRef<Normalized<BoxedExpression> | undefined>(expression?.boxedExpression);

  useEffect(() => {
    widthsByIdRef.current = widthsById;
  }, [widthsById]);

  useEffect(() => {
    boxedExpressionRef.current = expression?.boxedExpression;
  }, [expression?.boxedExpression]);

  const onWidthsChange: React.Dispatch<React.SetStateAction<Map<string, number[]>>> = useCallback(
    (newWidthsByIdAction) => {
      dmnEditorStoreApi.setState((state) => {
        const newWidthsById =
          typeof newWidthsByIdAction === "function"
            ? newWidthsByIdAction(widthsByIdRef.current ?? new Map())
            : newWidthsByIdAction;

        widthsByIdRef.current = newWidthsById;

        updateExpressionWidths({
          definitions: state.dmn.model.definitions,
          drdIndex: state.computed(state).getDrdIndex(),
          widthsById: newWidthsById,
        });
      });
    },
    [dmnEditorStoreApi]
  );

  const setExpression = useCallback(
    (args: { definitions: Normalized<DMN15__tDefinitions>; expression: Normalized<BoxedExpression | undefined> }) => {
      boxedExpressionRef.current = args.expression;
      updateExpression({
        definitions: args.definitions,
        expression: args.expression!,
        drgElementIndex: expression?.drgElementIndex ?? 0,
        externalDmnModelsByNamespaceMap,
      });
    },
    [expression?.drgElementIndex, externalDmnModelsByNamespaceMap]
  );

  const [isRefactorModalOpen, setIsRefactorModalOpen] = useState(false);
  const [variableChangedArgs, setVariableChangedArgs] = useState<VariableChangedArgs>();
  const [newExpression, setNewExpression] = useState<Normalized<BoxedExpression | undefined>>();

  const refactor = useCallback(() => {
    if (!variableChangedArgs) {
      throw new Error("Can not refactor because `variableChangedArgs` are not set in BoxedExpressionScreen.");
    }

    dmnEditorStoreApi.setState((state) => {
      const drgElement = state.dmn.model.definitions.drgElement?.[expression?.drgElementIndex ?? 0];
      if (drgElement?.["@_id"] === variableChangedArgs.variableUuid) {
        renameDrgElement({
          definitions: state.dmn.model.definitions,
          newName: newExpression?.["@_label"] ?? drgElement!["@_name"]!,
          index: expression?.drgElementIndex ?? 0,
          externalDmnModelsByNamespaceMap,
          shouldRenameReferencedExpressions: true,
        });
      } else {
        const identifiersRefactor = new IdentifiersRefactor({
          writeableDmnDefinitions: state.dmn.model.definitions,
          _readonly_externalDmnModelsByNamespaceMap: externalDmnModelsByNamespaceMap,
        });
        identifiersRefactor.rename({
          identifierUuid: variableChangedArgs.variableUuid,
          newName: variableChangedArgs.nameChange?.to ?? "",
        });
      }
    });
  }, [
    dmnEditorStoreApi,
    expression?.drgElementIndex,
    externalDmnModelsByNamespaceMap,
    newExpression,
    variableChangedArgs,
  ]);

  const onExpressionChange = useCallback<OnExpressionChange>(
    (args) => {
      dmnEditorStoreApi.setState((state) => {
        const newExpression =
          typeof args.setExpressionAction === "function"
            ? args.setExpressionAction(boxedExpressionRef.current)
            : args.setExpressionAction;

        if (!args.expressionChangedArgs) {
          setExpression({ definitions: state.dmn.model.definitions, expression: newExpression });
        } else {
          if (args.expressionChangedArgs.action === Action.VariableChanged) {
            if (args.expressionChangedArgs.typeChange && !args.expressionChangedArgs.nameChange) {
              updateDrgElementType({
                definitions: state.dmn.model.definitions,
                expression: newExpression!,
                drgElementIndex: expression?.drgElementIndex ?? 0,
              });
              setExpression({ definitions: state.dmn.model.definitions, expression: newExpression });
            } else if (args.expressionChangedArgs.typeChange) {
              const identifiersRefactor = new IdentifiersRefactor({
                writeableDmnDefinitions: state.dmn.model.definitions,
                _readonly_externalDmnModelsByNamespaceMap: externalDmnModelsByNamespaceMap,
              });
              identifiersRefactor.changeType({
                identifierUuid: args.expressionChangedArgs.variableUuid,
                newType: args.expressionChangedArgs.typeChange.to,
              });
              updateDrgElementType({
                definitions: state.dmn.model.definitions,
                expression: newExpression!,
                drgElementIndex: expression?.drgElementIndex ?? 0,
              });
            }
            if (args.expressionChangedArgs.nameChange) {
              setVariableChangedArgs(args.expressionChangedArgs);
              setNewExpression(newExpression);
              if (
                isIdentifierReferencedInSomeExpression({
                  identifierUuid: args.expressionChangedArgs.variableUuid,
                  dmnDefinitions: state.dmn.model.definitions,
                  externalDmnModelsByNamespaceMap,
                })
              ) {
                setIsRefactorModalOpen(true);
              } else {
                setExpression({ definitions: state.dmn.model.definitions, expression: newExpression });
              }
            }
          } else {
            setExpression({ definitions: state.dmn.model.definitions, expression: newExpression });
          }
        }
      });
    },
    [dmnEditorStoreApi, expression?.drgElementIndex, externalDmnModelsByNamespaceMap, setExpression]
  );

  // END (setState batching for `expression` and `widthsById`)

  const isResetSupportedOnRootExpression = useMemo(() => {
    return expression?.drgElementType === "decision"; // BKMs are ALWAYS functions, and can't be reset.
  }, [expression?.drgElementType]);

  ////

  const dataTypes = useMemo<DmnDataType[]>(() => {
    const customDataTypes = dataTypesTree.map((d) => ({
      isCustom: true,
      name: d.feelName,
    }));

    return [...builtInFeelTypes, ...customDataTypes];
  }, [dataTypesTree]);

  const pmmlDocuments = useMemo<PmmlDocument[]>(() => {
    return [...externalPmmlsByNamespace.entries()].flatMap(([namespace, pmml]) => {
      const documentData = getPmmlDocumentData(pmml.model);
      const _import = importsByNamespace.get(namespace);
      if (!_import) {
        return [];
      }

      return {
        document: _import["@_name"],
        modelsFromDocument: documentData.models.map((m) => ({
          model: m.modelName,
          parametersFromModel: m.fields.map((f) => ({
            "@_id": generateUuid(),
            "@_name": f.fieldName,
            description: { __$$text: f.fieldName },
          })),
        })),
      };
    });
  }, [importsByNamespace, externalPmmlsByNamespace]);

  const beeGwtService = useMemo<BeeGwtService>(() => {
    return {
      getDefaultExpressionDefinition(logicType, typeRef, isRoot) {
        const s = dmnEditorStoreApi.getState();
        const c = s.computed(s);

        const allTopLevelDataTypesByFeelName = c.getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName;
        const nodesById = c.getDiagramData(externalModelsByNamespace).nodesById;

        const defaultWidthsById = new Map<string, number[]>();
        const defaultExpression = getDefaultBoxedExpression({
          logicType,
          typeRef,
          allTopLevelDataTypesByFeelName,
          widthsById: defaultWidthsById,
          getDefaultColumnWidth,
          getInputs: () => {
            const drgElement = s.dmn.model.definitions.drgElement?.[drgElementIndex ?? 0];
            if (!isRoot || drgElement?.__$$element !== "decision") {
              return undefined;
            } else {
              return determineInputsForDecision(drgElement, allTopLevelDataTypesByFeelName, nodesById);
            }
          },
        });

        return {
          expression: defaultExpression,
          widthsById: defaultWidthsById,
        };
      },
      selectObject(uuid) {
        dmnEditorStoreApi.setState((state) => {
          state.boxedExpressionEditor.selectedObjectId = uuid;
        });
      },
      openDataTypePage() {
        dmnEditorStoreApi.setState((state) => {
          state.navigation.tab = DmnEditorTab.DATA_TYPES;
        });
      },
    };
  }, [dmnEditorStoreApi, drgElementIndex, externalModelsByNamespace]);

  ////

  const Icon = useMemo(() => {
    if (!drgElement) {
      throw new Error("A node Icon must exist for all types of node");
    }
    const nodeType = getNodeTypeFromDmnObject(drgElement);
    if (nodeType === undefined) {
      throw new Error("Can't determine node icon with undefined node type");
    }
    return NodeIcon({ nodeType, isAlternativeInputDataShape });
  }, [drgElement, isAlternativeInputDataShape]);

  const onConfirmExpressionRefactor = useCallback(() => {
    if (!variableChangedArgs) {
      throw new Error(
        "Can not update variable name because 'variableChangedArgs' is not set in the BoxedExpressionScreen."
      );
    }
    refactor();

    setVariableChangedArgs(undefined);
    setNewExpression(undefined);
    setIsRefactorModalOpen(false);
  }, [refactor, variableChangedArgs]);

  const onConfirmRenameOnly = useCallback(() => {
    setVariableChangedArgs(undefined);
    setNewExpression(undefined);
    setIsRefactorModalOpen(false);
    dmnEditorStoreApi.setState((state) => {
      setExpression({ definitions: state.dmn.model.definitions, expression: newExpression });
    });
  }, [dmnEditorStoreApi, newExpression, setExpression]);

  return (
    <>
      <>
        <Flex
          className={"kie-dmn-editor--sticky-top-glass-header kie-dmn-editor--boxed-expression-header"}
          justifyContent={{ default: "justifyContentSpaceBetween" }}
          alignItems={{ default: "alignItemsCenter" }}
          direction={{ default: "row" }}
        >
          <FlexItem
            style={{ borderRadius: 99999 }}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.dispatch(state).boxedExpressionEditor.close();
              });
            }}
          >
            <Label
              className={"kie-dmn-editor--boxed-expression-back"}
              icon={<ArrowRightIcon style={{ transform: "scale(-1, -1)", marginRight: "8px", marginTop: "4px" }} />}
            >
              <p>Back to Diagram</p>
            </Label>
          </FlexItem>
          <FlexItem>
            <Flex
              flexWrap={{ default: "nowrap" }}
              justifyContent={{ default: "justifyContentSpaceBetween" }}
              alignItems={{ default: "alignItemsCenter" }}
            >
              <FlexItem>
                <div style={{ height: "40px", width: "40px" }}>
                  <Icon />
                </div>
              </FlexItem>
              <FlexItem>
                <TextContent>
                  <Text component={TextVariants.h2}>{expression?.drgElement["@_name"]}</Text>
                </TextContent>
              </FlexItem>
              <FlexItem style={{ width: "105px" }} />
            </Flex>
          </FlexItem>

          <Flex>
            <EvaluationHighlightsBadge />
            <aside
              className={"kie-dmn-editor--properties-panel-toggle"}
              style={{ visibility: isPropertiesPanelOpen ? "hidden" : undefined }}
            >
              <button
                className={"kie-dmn-editor--properties-panel-toggle-button"}
                title={"Properties panel"}
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.boxedExpressionEditor.propertiesPanel.isOpen =
                      !state.boxedExpressionEditor.propertiesPanel.isOpen;
                  });
                }}
              >
                <InfoIcon />
              </button>
            </aside>
          </Flex>
        </Flex>
        <RefactorConfirmationDialog
          onConfirmExpressionRefactor={onConfirmExpressionRefactor}
          onConfirmRenameOnly={onConfirmRenameOnly}
          isRefactorModalOpen={isRefactorModalOpen}
          fromName={variableChangedArgs?.nameChange?.from}
          toName={variableChangedArgs?.nameChange?.to}
          onCancel={() => {
            setIsRefactorModalOpen(false);
            setVariableChangedArgs(undefined);
            setNewExpression(undefined);
          }}
        />

        <div style={{ flexGrow: 1 }}>
          <BoxedExpressionEditor
            beeGwtService={beeGwtService}
            pmmlDocuments={pmmlDocuments}
            isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
            expressionHolderId={activeDrgElementId!}
            expressionHolderName={drgElement?.variable?.["@_name"] ?? drgElement?.["@_name"] ?? ""}
            expressionHolderTypeRef={drgElement?.variable?.["@_typeRef"] ?? expression?.boxedExpression?.["@_typeRef"]}
            expression={expression?.boxedExpression}
            onExpressionChange={onExpressionChange}
            dataTypes={dataTypes}
            scrollableParentRef={container}
            onRequestFeelIdentifiers={onRequestFeelIdentifiers}
            widthsById={widthsById}
            onWidthsChange={onWidthsChange}
            isReadOnly={settings.isReadOnly}
            evaluationHitsCountById={
              isEvaluationHighlightsEnabled
                ? evaluationResultsByNodeId?.get(activeDrgElementId ?? "")?.evaluationHitsCountByRuleOrRowId
                : undefined
            }
          />
        </div>
      </>
    </>
  );
}

export function drgElementToBoxedExpression(
  expressionHolder:
    | (Normalized<DMN15__tDecision> & { __$$element: "decision" })
    | (Normalized<DMN15__tBusinessKnowledgeModel> & { __$$element: "businessKnowledgeModel" })
): Normalized<BoxedExpression> | undefined {
  if (expressionHolder.__$$element === "businessKnowledgeModel") {
    return expressionHolder.encapsulatedLogic
      ? {
          __$$element: "functionDefinition",
          "@_label": expressionHolder.encapsulatedLogic["@_label"] ?? expressionHolder["@_name"],
          "@_typeRef": expressionHolder.encapsulatedLogic["@_typeRef"] ?? expressionHolder.variable?.["@_typeRef"],
          ...expressionHolder.encapsulatedLogic,
        }
      : {
          __$$element: "functionDefinition",
          "@_id": generateUuid(),
          "@_kind": "FEEL",
          expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
          formalParameter: [],
          "@_label": expressionHolder["@_name"],
          "@_typeRef": expressionHolder.variable?.["@_typeRef"],
        };
  } else if (expressionHolder.__$$element === "decision") {
    return expressionHolder.expression
      ? {
          ...expressionHolder.expression,
          "@_label":
            expressionHolder?.variable?.["@_name"] ??
            expressionHolder.expression["@_label"] ??
            expressionHolder?.["@_name"],
          "@_typeRef": expressionHolder?.variable
            ? expressionHolder?.variable["@_typeRef"]
            : expressionHolder.expression["@_typeRef"],
        }
      : undefined;
  } else {
    throw new Error(
      `Unknown __$$element of expressionHolder that has an expression '${(expressionHolder as any).__$$element}'.`
    );
  }
}

function determineInputsForDecision(
  decision: Normalized<DMN15__tDecision>,
  allTopLevelDataTypesByFeelName: DataTypeIndex,
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>
) {
  try {
    const ret = (decision.informationRequirement ?? []).flatMap((s) => {
      const dmnObject = nodesById.get((s.requiredDecision?.["@_href"] ?? s.requiredInput?.["@_href"])!)!.data.dmnObject;
      if (!(dmnObject?.__$$element === "inputData" || dmnObject?.__$$element === "decision")) {
        throw new Error(
          "DMN EDITOR: Information requirement can't ever point to anything other than an InputData or a Decision"
        );
      }

      const dataType = allTopLevelDataTypesByFeelName.get(dmnObject.variable!["@_typeRef"]!);
      return dataType && isStruct(dataType.itemDefinition)
        ? (dataType.itemDefinition.itemComponent ?? []).flatMap((ic) =>
            flattenItemComponents({
              itemDefinition: ic,
              acc: dmnObject.variable!["@_name"],
            })
          )
        : [
            {
              name: dmnObject.variable!["@_name"]!,
              typeRef: dmnObject.variable?.["@_typeRef"],
            },
          ];
    });

    if (ret.length === 0) {
      return undefined;
    }

    return ret;
  } catch (e) {
    console.error(`DMN EDITOR: Error suggesting imports for root expression on '${decision["@_name"]}'.`, e);
    return undefined;
  }
}

function flattenItemComponents({
  itemDefinition,
  acc,
}: {
  itemDefinition: Normalized<DMN15__tItemDefinition>;
  acc: string;
}): { name: string; typeRef: string | undefined }[] {
  if (!isStruct(itemDefinition)) {
    return [
      {
        name: `${acc}.${itemDefinition["@_name"]!}`,
        typeRef: itemDefinition.typeRef?.__$$text,
      },
    ];
  }

  return (itemDefinition.itemComponent ?? []).flatMap((ic) => {
    return flattenItemComponents({ itemDefinition: ic, acc: `${acc}.${itemDefinition["@_name"]!}` });
  });
}

export function getPmmlDocumentData(pmml: PMML): PMMLDocumentData {
  const models: PMMLModelData[] = [];
  const document = new PMMLDocumentData(models);

  if (pmml.models) {
    pmml.models.forEach((model) => {
      const modelData = retrieveModelData(model);
      if (modelData) {
        models.push(modelData);
      }
    });
  }
  return document;
}

export function retrieveModelData(model: Model): PMMLModelData | undefined {
  const modelsTypes = [
    AnomalyDetectionModel,
    AssociationModel,
    BayesianNetworkModel,
    BaselineModel,
    ClusteringModel,
    GaussianProcessModel,
    GeneralRegressionModel,
    MiningModel,
    NaiveBayesModel,
    NearestNeighborModel,
    NeuralNetwork,
    RegressionModel,
    RuleSetModel,
    SequenceModel,
    Scorecard,
    SupportVectorMachineModel,
    TextModel,
    TimeSeriesModel,
    TreeModel,
  ];
  let modelData;

  for (const type of modelsTypes) {
    if (model instanceof type) {
      const modelFields = model.MiningSchema.MiningField.map(
        (field) => new PMMLFieldData(field.name.toString(), field.usageType)
      );
      modelData = new PMMLModelData(model.modelName == null ? "" : model.modelName, modelFields);
    }
  }

  return modelData;
}
