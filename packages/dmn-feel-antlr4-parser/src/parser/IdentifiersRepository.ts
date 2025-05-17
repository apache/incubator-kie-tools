/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DataType } from "./DataType";
import { FeelSyntacticSymbolNature } from "./FeelSyntacticSymbolNature";
import { IdentifierContext } from "./IdentifierContext";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tContextEntry,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tDecisionTable,
  DMN15__tDefinitions,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInformationRequirement,
  DMN15__tInputClause,
  DMN15__tInputData,
  DMN15__tInvocation,
  DMN15__tItemDefinition,
  DMN15__tKnowledgeRequirement,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tQuantified,
  DMN15__tRelation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Expression } from "./Expression";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { BuiltInTypes } from "./BuiltInTypes";

export type ExpressionSource = { text?: { __$$text: string }; "@_id"?: string };
export type DmnLiteralExpression = { __$$element: "literalExpression" } & DMN15__tLiteralExpression;
export type DmnInvocation = { __$$element: "invocation" } & DMN15__tInvocation;
export type DmnDecisionTable = { __$$element: "decisionTable" } & DMN15__tDecisionTable;
export type DmnContext = { __$$element: "context" } & DMN15__tContext;
export type DmnFunctionDefinition = { __$$element: "functionDefinition" } & DMN15__tFunctionDefinition;
export type DmnRelation = { __$$element: "relation" } & DMN15__tRelation;
export type DmnList = { __$$element: "list" } & DMN15__tList;
export type DmnConditional = { __$$element: "conditional" } & DMN15__tConditional;
export type DmnFilter = { __$$element: "filter" } & DMN15__tFilter;
export type DmnFor = { __$$element: "for" } & DMN15__tFor;
export type DmnEvery = { __$$element: "every" } & DMN15__tQuantified;
export type DmnSome = { __$$element: "some" } & DMN15__tQuantified;
export type DmnDecisionNode = { __$$element: "decision" } & DMN15__tDecision;

export type DmnDefinitions = DMN15__tDefinitions;
export type DmnKnowledgeRequirement = DMN15__tKnowledgeRequirement;
export type DmnContextEntry = DMN15__tContextEntry;

type DmnBusinessKnowledgeModel = DMN15__tBusinessKnowledgeModel;
type DmnItemDefinition = DMN15__tItemDefinition;
type DmnInputData = DMN15__tInputData;
type DmnInformationRequirement = DMN15__tInformationRequirement;
type DmnDecisionService = DMN15__tDecisionService;

export class IdentifiersRepository {
  private readonly _identifiersContextIndexedByUuid: Map<string, IdentifierContext>;
  private readonly _expressionsIndexedByUuid: Map<string, Expression>;
  private readonly _dataTypes: Map<string, DataType>;
  private readonly _dataTypeIndexedByUuid: Map<string, DataType>;
  private readonly _importedIdentifiers: Map<string, Array<IdentifierContext>>;
  private readonly _importedDataTypes: Map<string, Array<DataType>>;
  private currentIdentifierNamePrefix: string;
  private currentUuidPrefix: string;

  constructor(
    private dmnDefinitions: DmnDefinitions,
    private externalDefinitions?: Map<string, DmnLatestModel>
  ) {
    this._dataTypes = new Map([
      [BuiltInTypes.Number.name, BuiltInTypes.Number],
      [BuiltInTypes.Boolean.name, BuiltInTypes.Boolean],
      [BuiltInTypes.String.name, BuiltInTypes.String],
      [BuiltInTypes.DaysAndTimeDuration.name, BuiltInTypes.DaysAndTimeDuration],
      [BuiltInTypes.DateAndTime.name, BuiltInTypes.DateAndTime],
      [BuiltInTypes.YearsAndMonthsDuration.name, BuiltInTypes.YearsAndMonthsDuration],
      [BuiltInTypes.Time.name, BuiltInTypes.Time],
      [BuiltInTypes.Date.name, BuiltInTypes.Date],
    ]);

    this._identifiersContextIndexedByUuid = new Map<string, IdentifierContext>();
    this._expressionsIndexedByUuid = new Map<string, Expression>();
    this._dataTypeIndexedByUuid = new Map<string, DataType>();
    this._importedIdentifiers = new Map<string, Array<IdentifierContext>>();
    this._importedDataTypes = new Map<string, Array<DataType>>();
    this.loadImportedIdentifiers(dmnDefinitions, externalDefinitions);

    this.currentIdentifierNamePrefix = "";
    this.currentUuidPrefix = "";

    this.loadIdentifiers(dmnDefinitions);
  }

  get identifiersContextIndexedByUuid(): Map<string, IdentifierContext> {
    return this._identifiersContextIndexedByUuid;
  }

  get expressionsIndexedByUuid(): Map<string, Expression> {
    return this._expressionsIndexedByUuid;
  }

  get dataTypeIndexedByUuid(): Map<string, DataType> {
    return this._dataTypeIndexedByUuid;
  }

  get importedDataTypes(): Map<string, Array<DataType>> {
    return this._importedDataTypes;
  }

  get importedIdentifiers(): Map<string, Array<IdentifierContext>> {
    return this._importedIdentifiers;
  }

  get identifiers(): Map<string, IdentifierContext> {
    return this._identifiersContextIndexedByUuid;
  }

  get dataTypes(): Map<string, DataType> {
    return this._dataTypes;
  }

  get expressions(): Map<string, Expression> {
    return this._expressionsIndexedByUuid;
  }

  public reload() {
    this._identifiersContextIndexedByUuid.clear();
    this._expressionsIndexedByUuid.clear();
    this._dataTypeIndexedByUuid.clear();
    this._importedIdentifiers.clear();
    this._importedDataTypes.clear();
    this.loadImportedIdentifiers(this.dmnDefinitions, this.externalDefinitions);

    this.currentIdentifierNamePrefix = "";
    this.currentUuidPrefix = "";

    this.loadIdentifiers(this.dmnDefinitions);
  }

  private createDataTypes(definitions: DmnDefinitions) {
    definitions.itemDefinition?.forEach((itemDefinition) => {
      const dataType = this.createDataType(itemDefinition);

      this._dataTypeIndexedByUuid.set(dataType.uuid, dataType);
      this.addImportedDataType(dataType);

      itemDefinition.itemComponent?.forEach((itemComponent) => {
        const innerType = this.createInnerType(itemComponent);
        this._dataTypeIndexedByUuid.set(innerType.uuid, innerType);
        this.addImportedDataType(innerType);
        dataType.properties.set(innerType.name, innerType);
      });

      this.dataTypes.set(dataType.name, dataType);
    });
  }

  private addImportedDataType(dataType: DataType) {
    if (this.currentIdentifierNamePrefix.length != 0) {
      if (!this._importedDataTypes.has(this.currentIdentifierNamePrefix)) {
        this._importedDataTypes.set(this.currentIdentifierNamePrefix, []);
      }
      this._importedDataTypes.get(this.currentIdentifierNamePrefix)?.push(dataType);
    }
  }

  private loadIdentifiersFromDefinitions(definitions: DmnDefinitions) {
    definitions.drgElement?.forEach((drg) => {
      switch (drg.__$$element) {
        case "decision":
          this.loadIdentifiersFromDecision(drg);
          break;

        case "inputData":
          this.loadIdentifiersFromInputData(drg);
          break;

        case "businessKnowledgeModel":
          this.loadIdentifiersFromBkm(drg);
          break;

        case "decisionService":
          this.loadIdentifiersFromDecisionService(drg);
          break;

        default:
          // Do nothing because it is an element that does not declare variables
          break;
      }
    });
  }

  private loadIdentifiersFromInputData(drg: DmnInputData) {
    this.addIdentifierContext({
      uuid: drg["@_id"] ?? "",
      identifierDefinedByTheContext: drg["@_name"],
      kind: FeelSyntacticSymbolNature.GlobalVariable,
      parentContext: undefined,
      typeRef: drg.variable?.["@_typeRef"],
      applyValueToSource: (value) => {
        drg["@_name"] = value;
      },
      applyTypeRefToSource: (value) => {
        if (drg.variable) {
          if (typeof value === "string") {
            drg.variable["@_typeRef"] = value;
          } else {
            drg.variable["@_typeRef"] = value?.typeRef;
          }
        }
      },
    });
  }

  private loadIdentifiersFromDecisionService(drg: DmnDecisionService) {
    this.addIdentifierContext({
      uuid: drg["@_id"] ?? "",
      identifierDefinedByTheContext: drg["@_name"],
      kind: FeelSyntacticSymbolNature.Invocable,
      parentContext: undefined,
      typeRef: drg.variable?.["@_typeRef"],
      applyValueToSource: (value) => {
        drg["@_name"] = value;
      },
      applyTypeRefToSource: (value) => {
        if (drg.variable) {
          if (typeof value === "string") {
            drg.variable["@_typeRef"] = value;
          } else {
            drg.variable["@_typeRef"] = value?.typeRef;
          }
        }
      },
    });
  }

  private loadIdentifiersFromBkm(drg: DmnBusinessKnowledgeModel) {
    const parent = this.addIdentifierContext({
      uuid: drg["@_id"] ?? "",
      identifierDefinedByTheContext: drg["@_name"],
      kind: FeelSyntacticSymbolNature.Invocable,
      parentContext: undefined,
      typeRef: drg.variable?.["@_typeRef"],
      applyValueToSource: (value) => {
        drg["@_name"] = value;
      },
      applyTypeRefToSource: (value) => {
        if (drg.variable) {
          if (typeof value === "string") {
            drg.variable["@_typeRef"] = value;
          } else {
            drg.variable["@_typeRef"] = value?.typeRef;
          }
        }
      },
    });

    if (drg.encapsulatedLogic) {
      let parentElement = parent;

      if (drg.encapsulatedLogic.formalParameter) {
        for (const parameter of drg.encapsulatedLogic.formalParameter) {
          parentElement = this.addIdentifierContext({
            uuid: parameter["@_id"] ?? "",
            identifierDefinedByTheContext: parameter["@_name"] ?? "<parameter>",
            kind: FeelSyntacticSymbolNature.Parameter,
            parentContext: parentElement,
            applyValueToSource: (value) => {
              parameter["@_name"] = value;
            },
          });
        }
      }

      if (drg.encapsulatedLogic.expression) {
        this.addInnerExpression(parentElement, drg.encapsulatedLogic.expression);
      }
    }
  }

  private loadIdentifiersFromDecision(drg: DmnDecisionNode) {
    const parent: IdentifierContext = this.addIdentifierContext({
      uuid: drg["@_id"] ?? "",
      identifierDefinedByTheContext: drg["@_name"],
      kind: FeelSyntacticSymbolNature.InvisibleVariables,
      parentContext: undefined,
      typeRef: drg.variable?.["@_typeRef"],
      applyValueToSource: (value) => {
        drg["@_name"] = value;
      },
      applyTypeRefToSource: (value) => {
        if (drg.variable) {
          if (typeof value === "string") {
            drg.variable["@_typeRef"] = value;
          } else {
            drg.variable["@_typeRef"] = value?.typeRef;
          }
        }
      },
    });

    if (drg.informationRequirement) {
      for (const requirement of drg.informationRequirement) {
        this.addInputVariable(parent, requirement);
      }
    }

    if (drg.knowledgeRequirement) {
      for (const knowledgeRequirement of drg.knowledgeRequirement) {
        this.addInputVariableFromKnowledge(parent, knowledgeRequirement);
      }
    }

    if (drg.expression) {
      this.addInnerExpression(parent, drg.expression);
    }
  }

  private addIdentifierContext(args: {
    uuid: string;
    identifierDefinedByTheContext: string;
    kind: FeelSyntacticSymbolNature;
    parentContext?: IdentifierContext;
    typeRef?: string;
    allowDynamicVariables?: boolean;
    applyValueToSource?: (value: string) => void;
    applyTypeRefToSource?: (value: DataType | string | undefined) => void;
  }) {
    const variableContext = this.createIdentifierContext(
      this.buildIdentifierUuid(args.uuid),
      this.buildName(args.identifierDefinedByTheContext),
      args.kind,
      args.parentContext,
      args.typeRef,
      args.allowDynamicVariables,
      args.applyValueToSource,
      args.applyTypeRefToSource
    );

    if (this.currentIdentifierNamePrefix.length != 0) {
      if (!this._importedIdentifiers.has(this.currentIdentifierNamePrefix)) {
        this._importedIdentifiers.set(this.currentIdentifierNamePrefix, []);
      }
      this._importedIdentifiers.get(this.currentIdentifierNamePrefix)?.push(variableContext);
    }

    this._identifiersContextIndexedByUuid.set(this.buildIdentifierUuid(args.uuid), variableContext);

    return variableContext;
  }

  private createIdentifierContext(
    uuid: string,
    identifierDefinedByTheContext: string,
    variableType: FeelSyntacticSymbolNature,
    parent: IdentifierContext | undefined,
    typeRef: string | undefined,
    allowDynamicVariables: boolean | undefined,
    applyValueToSource?: (value: string) => void,
    applyTypeRefToSource?: (value: DataType | string | undefined) => void
  ): IdentifierContext {
    return {
      uuid: uuid,
      children: new Map<string, IdentifierContext>(),
      parent: parent,
      inputIdentifiers: new Array<string>(),
      allowDynamicVariables: allowDynamicVariables,
      identifier: {
        value: identifierDefinedByTheContext,
        feelSyntacticSymbolNature: variableType,
        typeRef: this.getTypeRef(typeRef),
        expressionsThatUseTheIdentifier: new Map<string, Expression>(),
        applyValueToSource() {
          if (applyValueToSource) {
            applyValueToSource(this.value);
          }
        },
        applyTypeRefToSource() {
          if (applyTypeRefToSource) {
            applyTypeRefToSource(this.typeRef);
          }
        },
      },
    };
  }

  public getTypeRef(typeRef: string | undefined) {
    return this.dataTypes.has(typeRef ?? "") ? this.dataTypes.get(typeRef ?? "") : typeRef;
  }

  private createDataType(itemDefinition: DmnItemDefinition) {
    const name = this.buildName(itemDefinition["@_name"]);
    const dataType: DataType = {
      uuid: itemDefinition["@_id"] ?? "datatype_uuid",
      name: name,
      properties: new Map<string, DataType>(),
      typeRef: itemDefinition["typeRef"]?.__$$text ?? itemDefinition["@_name"],
      source: {
        value: name,
        feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
        expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      },
    };
    return dataType;
  }

  private createInnerType(itemComponent: DmnItemDefinition) {
    const dataType: DataType = {
      uuid: itemComponent["@_id"] ?? "item_uuid",
      name: itemComponent["@_name"],
      properties: this.buildProperties(itemComponent),
      typeRef: itemComponent["typeRef"]?.__$$text ?? itemComponent["@_name"],
      source: {
        value: itemComponent["@_name"],
        feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
        expressionsThatUseTheIdentifier: new Map<string, Expression>(),
      },
    };
    return dataType;
  }

  private buildProperties(itemComponent: DmnItemDefinition): Map<string, DataType> {
    const properties = new Map<string, DataType>();

    itemComponent.itemComponent?.forEach((def) => {
      const property: DataType = {
        uuid: def["@_id"] ?? "root_property",
        name: def["@_name"],
        properties: this.buildProperties(def),
        typeRef: def["typeRef"]?.__$$text ?? def["@_name"],
        source: {
          value: def["@_name"],
          feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
          expressionsThatUseTheIdentifier: new Map<string, Expression>(),
        },
      };

      this._dataTypeIndexedByUuid.set(property.uuid, property);
      properties.set(property.name, property);
    });

    return properties;
  }

  private addExpression(parent: IdentifierContext, element: ExpressionSource) {
    const id = element["@_id"] ?? "";
    const expression = new Expression(id, element);
    this._expressionsIndexedByUuid.set(id, expression);
    this.addIdentifierContext({
      uuid: id,
      identifierDefinedByTheContext: "",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parent,
    });
  }

  private addInvocation(parent: IdentifierContext, element: DmnInvocation) {
    if (element.binding) {
      for (const bindingElement of element.binding) {
        if (bindingElement.expression) {
          this.addInnerExpression(parent, bindingElement.expression);
        }
      }
    }
  }

  private addContext(parent: IdentifierContext, element: DmnContext) {
    let parentNode = parent;
    if (element.contextEntry) {
      for (const innerEntry of element.contextEntry) {
        parentNode = this.addContextEntry(parentNode, innerEntry);
      }
    }
  }

  private addContextEntry(parentNode: IdentifierContext, contextEntry: DmnContextEntry) {
    const variableNode = this.addIdentifierContext({
      uuid: contextEntry.variable?.["@_id"] ?? "",
      identifierDefinedByTheContext: contextEntry.variable?.["@_name"] ?? "",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parentNode,
      typeRef: contextEntry.variable?.["@_typeRef"],
      applyValueToSource: (value) => {
        if (contextEntry.variable) {
          contextEntry.variable["@_name"] = value;
        }
      },
      applyTypeRefToSource: (value) => {
        if (contextEntry.variable) {
          if (typeof value === "string") {
            contextEntry.variable["@_typeRef"] = value;
          } else {
            contextEntry.variable["@_typeRef"] = value?.typeRef;
          }
        }
      },
    });

    parentNode.children.set(variableNode.uuid, variableNode);

    if (contextEntry.expression) {
      if (contextEntry.expression.__$$element) {
        // The parent is always the previous node to prevent recursive calls.
        // Consider this example:
        //
        // [ROOT] Context Expression
        // [X] Client DTI  | [A]
        // [Y] Some Other  | [B]
        // [Z] And Another | [C]
        //
        // Inside the B we can not call "Some Other" for instance, but we can call "Client DTI"
        //
        // So the structure for that case should be:
        //            [ROOT]
        //              /    \
        //             [X]  [A]
        //             /  \
        //            [Y]  [B]
        //            / \
        //          [Z]  [C]
        //
        // So in that case, inside "C" we recognize Y, X and ROOT
        // Inside B, X and ROOT
        //
        // By "ROOT" we understand the root expression which for example
        // can be the Decision Node itself and its input nodes.
        this.addInnerExpression(parentNode, contextEntry.expression);
      }
    }

    return variableNode;
  }

  private addFunctionDefinition(parent: IdentifierContext, element: DmnFunctionDefinition) {
    let parentElement = parent;

    if (element.formalParameter) {
      for (const parameter of element.formalParameter) {
        parentElement = this.addIdentifierContext({
          uuid: parameter["@_id"] ?? "",
          identifierDefinedByTheContext: parameter["@_name"] ?? "<parameter>",
          kind: FeelSyntacticSymbolNature.Parameter,
          parentContext: parentElement,
          applyValueToSource: (value) => {
            parameter["@_name"] = value;
          },
        });
      }
    }

    if (element.expression) {
      this.addInnerExpression(parentElement, element.expression);
    }
  }

  private addRelation(parent: IdentifierContext, element: DmnRelation) {
    if (element.row) {
      for (const rowElement of element.row) {
        if (rowElement.expression) {
          for (const expression of rowElement.expression) {
            this.addInnerExpression(parent, expression);
          }
        }
      }
    }
  }

  private addList(parent: IdentifierContext, element: DmnList) {
    if (element.expression) {
      for (const expression of element.expression) {
        if (expression) {
          this.addInnerExpression(parent, expression);
        }
      }
    }
  }

  private addConditional(parent: IdentifierContext, element: DmnConditional) {
    if (element.if?.expression) {
      this.addInnerExpression(parent, element.if.expression);
    }
    if (element.then?.expression) {
      this.addInnerExpression(parent, element.then.expression);
    }
    if (element.else?.expression) {
      this.addInnerExpression(parent, element.else.expression);
    }
  }

  private addIterable(parent: IdentifierContext, expression: DmnSome | DmnEvery) {
    const localParent = this.addIteratorVariable(parent, expression);

    if (expression.in.expression) {
      this.addInnerExpression(localParent, expression.in.expression);
    }
    if (expression.satisfies.expression) {
      this.addInnerExpression(localParent, expression.satisfies.expression);
    }
  }

  private addFor(parent: IdentifierContext, expression: DmnFor) {
    const localParent = this.addIteratorVariable(parent, expression);

    if (expression.return.expression) {
      this.addInnerExpression(localParent, expression.return.expression);
    }
    if (expression.in.expression) {
      this.addInnerExpression(localParent, expression.in.expression);
    }
  }

  private addFilterVariable(parent: IdentifierContext, expression: DmnFilter) {
    let type = undefined;

    // We're assuming that the 'in' expression is with the correct type (a list of @_typeRef).
    // If it is not the expression will fail anyway.
    if (expression.in.expression) {
      type = expression.in.expression["@_typeRef"];
    }
    return this.addIdentifierContext({
      uuid: expression["@_id"] ?? "",
      identifierDefinedByTheContext: "item",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parent,
      typeRef: type,
      allowDynamicVariables: true,
    });
  }

  private addIteratorVariable(parent: IdentifierContext, expression: DmnFor | DmnEvery | DmnSome) {
    let localParent = parent;
    if (expression["@_iteratorVariable"]) {
      let type = undefined;

      // We're assuming that the 'in' expression is with the correct type (a list of @_typeRef).
      // If it is not the expression will fail anyway.
      if (expression.in.expression) {
        type = expression.in.expression["@_typeRef"];
      }
      localParent = this.addIdentifierContext({
        uuid: expression["@_id"] ?? "",
        identifierDefinedByTheContext: expression["@_iteratorVariable"],
        kind: FeelSyntacticSymbolNature.LocalVariable,
        parentContext: parent,
        typeRef: type,
        allowDynamicVariables: true,
        applyValueToSource: (value) => {
          expression["@_iteratorVariable"] = value;
        },
        applyTypeRefToSource: (value) => {
          if (expression.in.expression) {
            if (typeof value === "string") {
              expression.in.expression["@_typeRef"] = value;
            } else {
              expression.in.expression["@_typeRef"] = value?.typeRef;
            }
          }
        },
      });
    }
    return localParent;
  }

  private addFilter(parent: IdentifierContext, expression: DmnFilter) {
    if (expression.in.expression) {
      this.addInnerExpression(parent, expression.in.expression);
    }

    const localParent = this.addFilterVariable(parent, expression);
    if (expression.match.expression) {
      this.addInnerExpression(localParent, expression.match.expression);
    }
  }

  private addInnerExpression(
    parent: IdentifierContext,
    expression:
      | DmnLiteralExpression
      | DmnInvocation
      | DmnDecisionTable
      | DmnContext
      | DmnFunctionDefinition
      | DmnRelation
      | DmnList
      | DmnFor
      | DmnFilter
      | DmnEvery
      | DmnSome
      | DmnConditional
  ) {
    switch (expression.__$$element) {
      case "literalExpression":
        this.addExpression(parent, expression);
        break;

      case "invocation":
        this.addInvocation(parent, expression);
        break;

      case "decisionTable":
        // It doesn't define variables, but we need it to create its own context to use variables inside of Decision Table.
        this.addDecisionTable(parent, expression);
        break;

      case "context":
        this.addContext(parent, expression);
        break;

      case "functionDefinition":
        this.addFunctionDefinition(parent, expression);
        break;

      case "relation":
        this.addRelation(parent, expression);
        break;

      case "list":
        this.addList(parent, expression);
        break;

      case "conditional":
        this.addConditional(parent, expression);
        break;

      case "every":
      case "some":
        this.addIterable(parent, expression);
        break;

      case "for":
        this.addFor(parent, expression);
        break;

      case "filter":
        this.addFilter(parent, expression);
        break;

      default:
      // throw new Error("Unknown or not supported type for expression.");
    }
  }

  private addDecisionTableEntryNode(parent: IdentifierContext, entryNode: ExpressionSource) {
    this.addExpression(parent, entryNode);
    const ruleInputElementNode = this.addIdentifierContext({
      uuid: entryNode["@_id"] ?? "",
      identifierDefinedByTheContext: "?",
      kind: FeelSyntacticSymbolNature.DynamicVariable,
      parentContext: parent,
    });
    parent.children.set(ruleInputElementNode.uuid, ruleInputElementNode);
  }

  private addDecisionTableInputEntryNode(parent: IdentifierContext, inputEntryNode: DMN15__tInputClause) {
    const identifierContext = this.addIdentifierContext({
      uuid: inputEntryNode["@_id"] ?? "",
      identifierDefinedByTheContext: "",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parent,
    });
    parent.children.set(identifierContext.uuid, identifierContext);

    // Notice that the expression of the inputEntryNode it is in an inner element, NOT in the inputEntryNode by itself.
    this.addExpression(parent, inputEntryNode.inputExpression);
  }

  private addDecisionTable(parent: IdentifierContext, decisionTable: DmnDecisionTable) {
    const addedIdentifierContext = this.addIdentifierContext({
      uuid: decisionTable["@_id"] ?? "",
      identifierDefinedByTheContext: "",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parent,
    });
    parent.children.set(addedIdentifierContext.uuid, addedIdentifierContext);

    // We need to create Identifier Context for each cell of the Decision Table to be able to have
    // autocomplete and refactor inside of those, otherwise the parser will not find the context to get the identifiers.
    if (decisionTable.rule) {
      for (const ruleElement of decisionTable.rule) {
        ruleElement.inputEntry?.forEach((inputElement) => this.addDecisionTableEntryNode(parent, inputElement));
        ruleElement.outputEntry?.forEach((outputElement) => this.addDecisionTableEntryNode(parent, outputElement));
      }
    }

    // The Decision Table inputs also have expressions and its own contexts.
    if (decisionTable.input) {
      for (const inputClause of decisionTable.input) {
        this.addDecisionTableInputEntryNode(addedIdentifierContext, inputClause);
      }
    }

    this.addIdentifierContext({
      uuid: addedIdentifierContext.uuid,
      identifierDefinedByTheContext: "",
      kind: FeelSyntacticSymbolNature.LocalVariable,
      parentContext: parent,
      applyTypeRefToSource: (value) => {
        if (typeof value === "string") {
          decisionTable["@_typeRef"] = value;
        } else {
          decisionTable["@_typeRef"] = value?.typeRef;
        }
      },
    });
  }

  private addInputVariable(parent: IdentifierContext, requirement: DmnInformationRequirement) {
    if (requirement.requiredDecision) {
      parent.inputIdentifiers.push(requirement.requiredDecision["@_href"]?.replace("#", ""));
    } else if (requirement.requiredInput) {
      parent.inputIdentifiers.push(requirement.requiredInput["@_href"]?.replace("#", ""));
    }
  }

  private addInputVariableFromKnowledge(parent: IdentifierContext, knowledgeRequirement: DmnKnowledgeRequirement) {
    if (knowledgeRequirement.requiredKnowledge) {
      parent.inputIdentifiers.push(knowledgeRequirement.requiredKnowledge["@_href"]?.replace("#", ""));
    }
  }

  private buildIdentifierUuid(uuid: string) {
    if (this.currentUuidPrefix.length != 0) {
      return this.currentUuidPrefix + uuid;
    }

    return uuid;
  }

  private buildName(name: string) {
    if (this.currentIdentifierNamePrefix.length != 0) {
      return `${this.currentIdentifierNamePrefix}.${name}`;
    }

    return name;
  }

  private loadIdentifiers(dmnDefinitions: DmnDefinitions) {
    this.createDataTypes(dmnDefinitions);
    this.loadIdentifiersFromDefinitions(dmnDefinitions);
  }

  private loadImportedIdentifiers(dmnDefinitions: DmnDefinitions, externalDefinitions?: Map<string, DmnLatestModel>) {
    if (!(dmnDefinitions.import && externalDefinitions)) {
      return;
    }

    for (const dmnImport of dmnDefinitions.import.filter((imp) => externalDefinitions.has(imp["@_namespace"]))) {
      this.currentIdentifierNamePrefix = dmnImport["@_name"];
      this.currentUuidPrefix = dmnImport["@_namespace"];
      const externalDef = externalDefinitions.get(dmnImport["@_namespace"]);
      if (externalDef) {
        this.loadIdentifiers(externalDef.definitions);
      }
    }
  }
}
