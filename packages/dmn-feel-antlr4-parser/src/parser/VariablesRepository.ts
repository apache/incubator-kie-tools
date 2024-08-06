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
import { VariableContext } from "./VariableContext";
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
  DMN15__tInputData,
  DMN15__tInvocation,
  DMN15__tItemDefinition,
  DMN15__tKnowledgeRequirement,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tQuantified,
  DMN15__tRelation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Expression } from "./VariableOccurrence";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { BuiltInTypes } from "./BuiltInTypes";

type DmnLiteralExpression = { __$$element: "literalExpression" } & DMN15__tLiteralExpression;
type DmnInvocation = { __$$element: "invocation" } & DMN15__tInvocation;
type DmnDecisionTable = { __$$element: "decisionTable" } & DMN15__tDecisionTable;
type DmnContext = { __$$element: "context" } & DMN15__tContext;
type DmnFunctionDefinition = { __$$element: "functionDefinition" } & DMN15__tFunctionDefinition;
type DmnRelation = { __$$element: "relation" } & DMN15__tRelation;
type DmnList = { __$$element: "list" } & DMN15__tList;
type DmnKnowledgeRequirement = DMN15__tKnowledgeRequirement;
type DmnConditional = { __$$element: "conditional" } & DMN15__tConditional;
type DmnFilter = { __$$element: "filter" } & DMN15__tFilter;
type DmnFor = { __$$element: "for" } & DMN15__tFor;
type DmnEvery = { __$$element: "every" } & DMN15__tQuantified;
type DmnSome = { __$$element: "some" } & DMN15__tQuantified;
type DmnDecisionNode = { __$$element: "decision" } & DMN15__tDecision;
type DmnBusinessKnowledgeModel = DMN15__tBusinessKnowledgeModel;
export type DmnDefinitions = DMN15__tDefinitions;
type DmnItemDefinition = DMN15__tItemDefinition;
type DmnContextEntry = DMN15__tContextEntry;
type DmnInputData = DMN15__tInputData;
type DmnInformationRequirement = DMN15__tInformationRequirement;
type DmnDecisionService = DMN15__tDecisionService;

export class VariablesRepository {
  private readonly variablesIndexedByUuid: Map<string, VariableContext>;
  private readonly expressionsIndexedByUuid: Map<string, Expression>;
  private readonly dataTypes: Map<string, DataType>;
  private currentVariablePrefix: string;
  private currentUuidPrefix: string;

  constructor(dmnDefinitions: DmnDefinitions, externalDefinitions: Map<string, DmnLatestModel>) {
    this.dataTypes = new Map([
      [BuiltInTypes.Number.name, BuiltInTypes.Number],
      [BuiltInTypes.Boolean.name, BuiltInTypes.Boolean],
      [BuiltInTypes.String.name, BuiltInTypes.String],
      [BuiltInTypes.DaysAndTimeDuration.name, BuiltInTypes.DaysAndTimeDuration],
      [BuiltInTypes.DateAndTime.name, BuiltInTypes.DateAndTime],
      [BuiltInTypes.YearsAndMonthsDuration.name, BuiltInTypes.YearsAndMonthsDuration],
      [BuiltInTypes.Time.name, BuiltInTypes.Time],
      [BuiltInTypes.Date.name, BuiltInTypes.Date],
    ]);

    this.variablesIndexedByUuid = new Map<string, VariableContext>();
    this.expressionsIndexedByUuid = new Map<string, Expression>();
    this.loadImportedVariables(dmnDefinitions, externalDefinitions);

    this.currentVariablePrefix = "";
    this.currentUuidPrefix = "";

    this.loadVariables(dmnDefinitions);
  }

  get variables(): Map<string, VariableContext> {
    return this.variablesIndexedByUuid;
  }

  public updateVariableType(variableUuid: string, newType: string) {
    const variableContext = this.variablesIndexedByUuid.get(variableUuid);
    if (variableContext) {
      variableContext.variable.typeRef = this.getTypeRef(newType);
    }
  }

  get expressions(): Map<string, Expression> {
    return this.expressionsIndexedByUuid;
  }

  public renameVariable(variableUuid: string, newName: string) {
    const variableContext = this.variablesIndexedByUuid.get(variableUuid);
    if (variableContext) {
      for (const expression of variableContext.variable.expressions.values()) {
        expression.renameVariable(variableContext.variable, newName);
      }

      variableContext.variable.value = newName;
    }
  }

  public addVariableToContext(variableUuid: string, variableName: string, parentUuid: string, childUuid?: string) {
    const parentContext = this.variablesIndexedByUuid.get(parentUuid);
    if (parentContext) {
      const newVariable = {
        value: variableName,
        feelSyntacticSymbolNature: FeelSyntacticSymbolNature.GlobalVariable,
        typeRef: undefined,
        expressions: new Map<string, Expression>(),
      };

      const newContext: VariableContext = {
        uuid: variableUuid,
        parent: parentContext,
        variable: newVariable,
        children: new Map<string, VariableContext>(),
        inputVariables: new Array<string>(),
      };

      this.variablesIndexedByUuid.set(newContext.uuid, newContext);

      parentContext.children.set(variableUuid, newContext);

      if (childUuid) {
        const childContext = this.variablesIndexedByUuid.get(childUuid);
        if (childContext) {
          parentContext.children.delete(childUuid);
          childContext.parent = newContext;
        }
      }
    }
  }

  public removeVariable(variableUuid: string, removeChildren?: boolean) {
    const variable = this.variablesIndexedByUuid.get(variableUuid);
    if (variable) {
      const newChildParent = variable.parent;
      if (!removeChildren) {
        if (newChildParent) {
          newChildParent.children.delete(variableUuid);
          for (const child of variable.children.values()) {
            child.parent = newChildParent;
            newChildParent.children.set(child.uuid, child);
          }
        }
      } else {
        variable.parent?.children.delete(variableUuid);
        for (const child of variable.children.keys()) {
          this.removeVariable(child, true);
        }
      }
      this.variablesIndexedByUuid.delete(variableUuid);
    }
  }

  private createDataTypes(definitions: DmnDefinitions) {
    definitions.itemDefinition?.forEach((itemDefinition) => {
      const dataType = this.createDataType(itemDefinition);

      itemDefinition.itemComponent?.forEach((itemComponent) => {
        const innerType = this.createInnerType(itemComponent);
        dataType.properties.set(innerType.name, innerType);
      });

      this.dataTypes.set(dataType.name, dataType);
    });
  }

  private createVariables(definitions: DmnDefinitions) {
    definitions.drgElement?.forEach((drg) => {
      switch (drg.__$$element) {
        case "decision":
          this.createVariablesFromDecision(drg);
          break;

        case "inputData":
          this.createVariablesFromInputData(drg);
          break;

        case "businessKnowledgeModel":
          this.createVariablesFromBkm(drg);
          break;

        case "decisionService":
          this.createVariablesFromDecisionService(drg);
          break;

        default:
          // Do nothing because it is an element that does not declare variables
          break;
      }
    });
  }

  private createVariablesFromInputData(drg: DmnInputData) {
    this.addVariable(
      drg["@_id"] ?? "",
      drg["@_name"],
      FeelSyntacticSymbolNature.GlobalVariable,
      undefined,
      drg.variable?.["@_typeRef"]
    );
  }

  private createVariablesFromDecisionService(drg: DmnDecisionService) {
    this.addVariable(
      drg["@_id"] ?? "",
      drg["@_name"],
      FeelSyntacticSymbolNature.Invocable,
      undefined,
      drg.variable?.["@_typeRef"]
    );
  }

  private createVariablesFromBkm(drg: DmnBusinessKnowledgeModel) {
    const parent = this.addVariable(
      drg["@_id"] ?? "",
      drg["@_name"],
      FeelSyntacticSymbolNature.Invocable,
      undefined,
      drg.variable?.["@_typeRef"]
    );

    if (drg.encapsulatedLogic) {
      let parentElement = parent;

      if (drg.encapsulatedLogic.formalParameter) {
        for (const parameter of drg.encapsulatedLogic.formalParameter) {
          parentElement = this.addVariable(
            parameter["@_id"] ?? "",
            parameter["@_name"] ?? "<parameter>",
            FeelSyntacticSymbolNature.Parameter,
            parentElement
          );
        }
      }

      if (drg.encapsulatedLogic.expression) {
        this.addInnerExpression(parentElement, drg.encapsulatedLogic.expression);
      }
    }
  }

  private createVariablesFromDecision(drg: DmnDecisionNode) {
    const parent = this.addVariable(
      drg["@_id"] ?? "",
      drg["@_name"],
      FeelSyntacticSymbolNature.InvisibleVariables,
      undefined,
      drg.variable?.["@_typeRef"]
    );

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

  private addVariable(
    uuid: string,
    name: string,
    variableType: FeelSyntacticSymbolNature,
    parent?: VariableContext,
    typeRef?: string,
    allowDynamicVariables?: boolean
  ) {
    const node = this.createVariableNode(
      this.buildVariableUuid(uuid),
      this.buildName(name),
      variableType,
      parent,
      typeRef,
      allowDynamicVariables
    );

    this.variablesIndexedByUuid.set(this.buildVariableUuid(uuid), node);

    return node;
  }

  private createVariableNode(
    uuid: string,
    name: string,
    variableType: FeelSyntacticSymbolNature,
    parent: VariableContext | undefined,
    typeRef: string | undefined,
    allowDynamicVariables: boolean | undefined
  ): VariableContext {
    return {
      uuid: uuid,
      children: new Map<string, VariableContext>(),
      parent: parent,
      inputVariables: new Array<string>(),
      allowDynamicVariables: allowDynamicVariables,
      variable: {
        value: name,
        feelSyntacticSymbolNature: variableType,
        typeRef: this.getTypeRef(typeRef),
        expressions: new Map<string, Expression>(),
      },
    };
  }

  private getTypeRef(typeRef: string | undefined) {
    return this.dataTypes.has(typeRef ?? "") ? this.dataTypes.get(typeRef ?? "") : typeRef;
  }

  private createDataType(itemDefinition: DmnItemDefinition) {
    return {
      name: this.buildName(itemDefinition["@_name"]),
      properties: new Map<string, DataType>(),
      typeRef: itemDefinition["typeRef"]?.__$$text ?? itemDefinition["@_name"],
    };
  }

  private createInnerType(itemComponent: DmnItemDefinition) {
    return {
      name: itemComponent["@_name"],
      properties: this.buildProperties(itemComponent),
      typeRef: itemComponent["typeRef"]?.__$$text ?? itemComponent["@_name"],
    };
  }

  private buildProperties(itemComponent: DmnItemDefinition): Map<string, DataType> {
    const properties = new Map<string, DataType>();

    itemComponent.itemComponent?.forEach((def) => {
      const rootProperty = {
        name: def["@_name"],
        properties: this.buildProperties(def),
        typeRef: def["typeRef"]?.__$$text ?? def["@_name"],
      };

      properties.set(rootProperty.name, rootProperty);
    });

    return properties;
  }

  private addLiteralExpression(parent: VariableContext, element: DmnLiteralExpression) {
    const id = element["@_id"] ?? "";
    const expression = new Expression(id, element.text?.__$$text);
    this.expressionsIndexedByUuid.set(id, expression);
    this.addVariable(id, "", FeelSyntacticSymbolNature.LocalVariable, parent);
  }

  private addInvocation(parent: VariableContext, element: DmnInvocation) {
    if (element.binding) {
      for (const bindingElement of element.binding) {
        if (bindingElement.expression) {
          this.addInnerExpression(parent, bindingElement.expression);
        }
      }
    }
  }

  private addContext(parent: VariableContext, element: DmnContext) {
    let parentNode = parent;
    if (element.contextEntry) {
      for (const innerEntry of element.contextEntry) {
        parentNode = this.addContextEntry(parentNode, innerEntry);
      }
    }
  }

  private addContextEntry(parentNode: VariableContext, contextEntry: DmnContextEntry) {
    const variableNode = this.addVariable(
      contextEntry.variable?.["@_id"] ?? "",
      contextEntry.variable?.["@_name"] ?? "",
      FeelSyntacticSymbolNature.LocalVariable,
      parentNode,
      contextEntry.variable?.["@_typeRef"]
    );

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

  private addFunctionDefinition(parent: VariableContext, element: DmnFunctionDefinition) {
    let parentElement = parent;

    if (element.formalParameter) {
      for (const parameter of element.formalParameter) {
        parentElement = this.addVariable(
          parameter["@_id"] ?? "",
          parameter["@_name"] ?? "<parameter>",
          FeelSyntacticSymbolNature.Parameter,
          parentElement
        );
      }
    }

    if (element.expression) {
      this.addInnerExpression(parentElement, element.expression);
    }
  }

  private addRelation(parent: VariableContext, element: DmnRelation) {
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

  private addList(parent: VariableContext, element: DmnList) {
    if (element.expression) {
      for (const expression of element.expression) {
        if (expression) {
          this.addInnerExpression(parent, expression);
        }
      }
    }
  }

  private addConditional(parent: VariableContext, element: DmnConditional) {
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

  private addIterable(parent: VariableContext, expression: DmnSome | DmnEvery) {
    const localParent = this.addIteratorVariable(parent, expression);

    if (expression.in.expression) {
      this.addInnerExpression(localParent, expression.in.expression);
    }
    if (expression.satisfies.expression) {
      this.addInnerExpression(localParent, expression.satisfies.expression);
    }
  }

  private addFor(parent: VariableContext, expression: DmnFor) {
    const localParent = this.addIteratorVariable(parent, expression);

    if (expression.return.expression) {
      this.addInnerExpression(localParent, expression.return.expression);
    }
    if (expression.in.expression) {
      this.addInnerExpression(localParent, expression.in.expression);
    }
  }

  private addFilterVariable(parent: VariableContext, expression: DmnFilter) {
    let type = undefined;

    // We're assuming that the 'in' expression is with the correct type (a list of @_typeRef).
    // If it is not the expression will fail anyway.
    if (expression.in.expression) {
      type = expression.in.expression["@_typeRef"];
    }
    return this.addVariable(
      expression["@_id"] ?? "",
      "item",
      FeelSyntacticSymbolNature.LocalVariable,
      parent,
      type,
      true
    );
  }

  private addIteratorVariable(parent: VariableContext, expression: DmnFor | DmnEvery | DmnSome) {
    let localParent = parent;
    if (expression["@_iteratorVariable"]) {
      let type = undefined;

      // We're assuming that the 'in' expression is with the correct type (a list of @_typeRef).
      // If it is not the expression will fail anyway.
      if (expression.in.expression) {
        type = expression.in.expression["@_typeRef"];
      }
      localParent = this.addVariable(
        expression["@_id"] ?? "",
        expression["@_iteratorVariable"],
        FeelSyntacticSymbolNature.LocalVariable,
        parent,
        type,
        true
      );
    }
    return localParent;
  }

  private addFilter(parent: VariableContext, expression: DmnFilter) {
    if (expression.in.expression) {
      this.addInnerExpression(parent, expression.in.expression);
    }

    const localParent = this.addFilterVariable(parent, expression);
    if (expression.match.expression) {
      this.addInnerExpression(localParent, expression.match.expression);
    }
  }

  private addInnerExpression(
    parent: VariableContext,
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
        this.addLiteralExpression(parent, expression);
        break;

      case "invocation":
        this.addInvocation(parent, expression);
        break;

      case "decisionTable":
        // Do nothing because DecisionTable does not define variables
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

  private addInputVariable(parent: VariableContext, requirement: DmnInformationRequirement) {
    if (requirement.requiredDecision) {
      parent.inputVariables.push(requirement.requiredDecision["@_href"]?.replace("#", ""));
    } else if (requirement.requiredInput) {
      parent.inputVariables.push(requirement.requiredInput["@_href"]?.replace("#", ""));
    }
  }

  private addInputVariableFromKnowledge(parent: VariableContext, knowledgeRequirement: DmnKnowledgeRequirement) {
    if (knowledgeRequirement.requiredKnowledge) {
      parent.inputVariables.push(knowledgeRequirement.requiredKnowledge["@_href"]?.replace("#", ""));
    }
  }

  private buildVariableUuid(uuid: string) {
    if (this.currentUuidPrefix.length != 0) {
      return this.currentUuidPrefix + uuid;
    }

    return uuid;
  }

  private buildName(name: string) {
    if (this.currentVariablePrefix.length != 0) {
      return this.currentVariablePrefix + "." + name;
    }

    return name;
  }

  private loadVariables(dmnDefinitions: DmnDefinitions) {
    this.createDataTypes(dmnDefinitions);
    this.createVariables(dmnDefinitions);
  }

  private loadImportedVariables(dmnDefinitions: DmnDefinitions, externalDefinitions: Map<string, DmnLatestModel>) {
    if (dmnDefinitions.import) {
      for (const dmnImport of dmnDefinitions.import) {
        if (externalDefinitions.has(dmnImport["@_namespace"])) {
          this.currentVariablePrefix = dmnImport["@_name"];
          this.currentUuidPrefix = dmnImport["@_namespace"];
          const externalDef = externalDefinitions.get(dmnImport["@_namespace"]);
          if (externalDef) {
            this.loadVariables(externalDef.definitions);
          }
        }
      }
    }
  }
}
