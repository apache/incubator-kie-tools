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
  BeeGwtService,
  DmnBuiltInDataType,
  DmnDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  PmmlParam,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import { FeelVariables } from "@kie-tools/dmn-feel-antlr4-parser";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
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
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import * as RF from "reactflow";
import { builtInFeelTypes } from "../dataTypes/BuiltInFeelTypes";
import { isStruct } from "../dataTypes/DataTypeSpec";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { NodeIcon } from "../icons/Icons";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { updateExpression } from "../mutations/updateExpression";
import { DmnEditorTab } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { dmnToBee, getUndefinedExpressionDefinition } from "./dmnToBee";
import { getDefaultColumnWidth } from "./getDefaultColumnWidth";
import { getDefaultExpressionDefinitionByLogicType } from "./getDefaultExpressionDefinitionByLogicType";

export function BoxedExpression({ container }: { container: React.RefObject<HTMLElement> }) {
  const { externalModelsByNamespace } = useExternalModels();

  //

  const thisDmn = useDmnEditorStore((s) => {
    return s.dmn;
  });
  const diagram = useDmnEditorStore((s) => {
    return s.diagram;
  });
  const boxedExpressionEditor = useDmnEditorStore((s) => {
    return s.boxedExpressionEditor;
  });
  const externalDmnsByNamespace = useDmnEditorStore((s) => {
    return s.computed(s).getExternalModelTypesByNamespace(externalModelsByNamespace).dmns;
  });
  const dataTypesTree = useDmnEditorStore((s) => {
    return s.computed(s).getDataTypes(externalModelsByNamespace).dataTypesTree;
  });
  const allTopLevelDataTypesByFeelName = useDmnEditorStore((s) => {
    return s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName;
  });
  const nodesById = useDmnEditorStore((s) => {
    return s.computed(s).getDiagramData(externalModelsByNamespace).nodesById;
  });
  const importsByNamespace = useDmnEditorStore((s) => {
    return s.computed(s).importsByNamespace();
  });
  const externalPmmlsByNamespace = useDmnEditorStore((s) => {
    return s.computed(s).getExternalModelTypesByNamespace(externalModelsByNamespace).pmmls;
  });

  //
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const feelVariables = useMemo(() => {
    const externalModels = new Map<string, DmnLatestModel>();

    for (const [key, externalDmn] of externalDmnsByNamespace) {
      externalModels.set(key, externalDmn.model);
    }

    return new FeelVariables(thisDmn.model.definitions, externalModels);
  }, [externalDmnsByNamespace, thisDmn.model.definitions]);

  const widthsById = useMemo(() => {
    return (
      thisDmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[diagram.drdIndex]["di:extension"]?.[
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
  }, [diagram.drdIndex, thisDmn.model.definitions]);

  const expression = useMemo(() => {
    if (!boxedExpressionEditor.activeDrgElementId) {
      return undefined;
    }

    const drgElementIndex = (thisDmn.model.definitions.drgElement ?? []).findIndex(
      (e) => e["@_id"] === boxedExpressionEditor.activeDrgElementId
    );
    if (drgElementIndex < 0) {
      return undefined;
    }

    const drgElement = thisDmn.model.definitions.drgElement![drgElementIndex];
    if (!(drgElement.__$$element === "decision" || drgElement.__$$element === "businessKnowledgeModel")) {
      return undefined;
    }

    return {
      beeExpression: drgElementToBoxedExpression(widthsById, drgElement),
      drgElementIndex,
      drgElement,
      drgElementType: drgElement.__$$element,
    };
  }, [boxedExpressionEditor.activeDrgElementId, thisDmn.model.definitions.drgElement, widthsById]);

  const setExpression: React.Dispatch<React.SetStateAction<ExpressionDefinition>> = useCallback(
    (expressionAction) => {
      dmnEditorStoreApi.setState((state) => {
        const newExpression =
          typeof expressionAction === "function"
            ? expressionAction(expression?.beeExpression ?? getUndefinedExpressionDefinition())
            : expressionAction;

        updateExpression({
          definitions: state.dmn.model.definitions,
          drdIndex: diagram.drdIndex,
          expression: newExpression,
          drgElementIndex: expression?.drgElementIndex ?? 0,
        });
      });
    },
    [diagram.drdIndex, dmnEditorStoreApi, expression?.beeExpression, expression?.drgElementIndex]
  );

  const isResetSupportedOnRootExpression = useMemo(() => {
    return expression?.drgElementType === "decision"; // BKMs are ALWAYS functions, and can't be reset.
  }, [expression?.drgElementType]);

  ////

  const dataTypes = useMemo<DmnDataType[]>(() => {
    const customDataTypes = dataTypesTree.map((d) => ({
      isCustom: true,
      typeRef: d.itemDefinition.typeRef?.__$$text ?? "",
      name: d.feelName,
    }));

    return [...builtInFeelTypes, ...customDataTypes];
  }, [dataTypesTree]);

  const pmmlParams = useMemo<PmmlParam[]>(() => {
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
            id: generateUuid(),
            name: f.fieldName,
            dataType: undefined as any,
          })),
        })),
      };
    });
  }, [importsByNamespace, externalPmmlsByNamespace]);

  const beeGwtService = useMemo<BeeGwtService>(() => {
    return {
      getDefaultExpressionDefinition(
        logicType: string,
        typeRef: string | undefined,
        isRoot?: boolean
      ): ExpressionDefinition {
        return getDefaultExpressionDefinitionByLogicType({
          logicType: logicType as ExpressionDefinitionLogicType,
          typeRef: typeRef ?? DmnBuiltInDataType.Undefined,
          expressionHolderName: expression?.drgElement?.["@_name"],
          allTopLevelDataTypesByFeelName,
          getDefaultColumnWidth,
          getInputs: () => {
            if (!isRoot || expression?.drgElement.__$$element !== "decision") {
              return undefined;
            } else {
              return determineInputsForDecision(expression?.drgElement, allTopLevelDataTypesByFeelName, nodesById);
            }
          },
        });
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
  }, [allTopLevelDataTypesByFeelName, dmnEditorStoreApi, expression?.drgElement, nodesById]);

  ////

  const Icon = expression ? NodeIcon(getNodeTypeFromDmnObject(expression.drgElement)) : () => <></>;

  return (
    <>
      <>
        <Flex
          className={"kie-dmn-editor--sticky-top-glass-header kie-dmn-editor--boxed-expression-header"}
          justifyContent={{ default: "justifyContentSpaceBetween" }}
          alignItems={{ default: "alignItemsCenter" }}
          direction={{ default: "row" }}
        >
          <FlexItem>
            <Label
              className={"kie-dmn-editor--boxed-expression-back"}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.dispatch(state).boxedExpressionEditor.close();
                });
              }}
            >
              <ArrowRightIcon style={{ transform: "scale(-1, -1)", marginRight: "12px" }} />
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

          <aside
            className={"kie-dmn-editor--properties-panel-toggle"}
            style={{ visibility: boxedExpressionEditor.propertiesPanel.isOpen ? "hidden" : undefined }}
          >
            <button
              className={"kie-dmn-editor--properties-panel-toggle-button"}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.boxedExpressionEditor.propertiesPanel.isOpen =
                    !state.boxedExpressionEditor.propertiesPanel.isOpen;
                });
              }}
            >
              <InfoIcon size={"sm"} />
            </button>
          </aside>
        </Flex>
        {!expression && (
          <>
            <EmptyState>
              <EmptyStateIcon icon={ErrorCircleOIcon} />
              <Title size="lg" headingLevel="h4">
                {`Expression with ID '${boxedExpressionEditor.activeDrgElementId}' doesn't exist.`}
              </Title>
              <EmptyStateBody>
                This happens when the DMN file is modified externally while the expression was open here.
              </EmptyStateBody>
              <EmptyStatePrimary>
                <Button
                  variant="link"
                  onClick={() => {
                    dmnEditorStoreApi.setState((state) => {
                      state.dispatch(state).boxedExpressionEditor.close();
                    });
                  }}
                >
                  Go back to the Diagram
                </Button>
              </EmptyStatePrimary>
            </EmptyState>
          </>
        )}
        {expression && (
          <div style={{ flexGrow: 1 }}>
            <BoxedExpressionEditor
              beeGwtService={beeGwtService}
              pmmlParams={pmmlParams}
              isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
              decisionNodeId={boxedExpressionEditor.activeDrgElementId!}
              expressionDefinition={expression.beeExpression}
              setExpressionDefinition={setExpression}
              dataTypes={dataTypes}
              scrollableParentRef={container}
              variables={feelVariables}
            />
          </div>
        )}
      </>
    </>
  );
}

function drgElementToBoxedExpression(
  widthsById: Map<string, number[]>,
  expressionHolder:
    | (DMN15__tDecision & { __$$element: "decision" })
    | (DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" })
): ExpressionDefinition {
  if (expressionHolder.__$$element === "businessKnowledgeModel") {
    return {
      ...dmnToBee(widthsById, {
        expression: {
          __$$element: "functionDefinition",
          ...expressionHolder.encapsulatedLogic,
        },
      }),
      dataType: (expressionHolder.variable?.["@_typeRef"] ??
        expressionHolder.encapsulatedLogic?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      name: expressionHolder["@_name"],
    };
  } else if (expressionHolder.__$$element === "decision") {
    return {
      ...dmnToBee(widthsById, expressionHolder),
      dataType: (expressionHolder.variable?.["@_typeRef"] ??
        expressionHolder.expression?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as unknown as DmnBuiltInDataType,
      name: expressionHolder["@_name"],
    };
  } else {
    throw new Error(
      `Unknown __$$element of expressionHolder that has an expression '${(expressionHolder as any).__$$element}'.`
    );
  }
}

function determineInputsForDecision(
  decision: DMN15__tDecision,
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
            flattenComponents(ic, dmnObject.variable!["@_name"])
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
function flattenComponents(
  itemDefinition: DMN15__tItemDefinition,
  acc: string
): { name: string; typeRef: string | undefined }[] {
  if (!isStruct(itemDefinition)) {
    return [
      {
        name: `${acc}.${itemDefinition["@_name"]!}`,
        typeRef: itemDefinition.typeRef?.__$$text,
      },
    ];
  }

  return (itemDefinition.itemComponent ?? []).flatMap((ic) => {
    return flattenComponents(ic, `${acc}.${itemDefinition["@_name"]!}`);
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
