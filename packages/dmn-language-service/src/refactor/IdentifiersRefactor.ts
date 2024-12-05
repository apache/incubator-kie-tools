/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import { DmnDefinitions, IdentifiersRepository } from "@kie-tools/dmn-feel-antlr4-parser/dist";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { Expression } from "@kie-tools/dmn-feel-antlr4-parser/dist";
import { FeelIdentifiersParser } from "@kie-tools/dmn-feel-antlr4-parser/dist";
import { IdentifierContext } from "@kie-tools/dmn-feel-antlr4-parser/dist/parser/IdentifierContext";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

/**
 * Defines the IdentifiersRefactor, which can be used to rename identifiers in the DMN files changing all expressions
 * that use those identifiers.
 */
export class IdentifiersRefactor {
  private readonly repository: IdentifiersRepository;

  constructor(args: {
    writeableDmnDefinitions: Normalized<DmnDefinitions>;
    _readonly_externalDmnModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
  }) {
    this.repository = new IdentifiersRepository(
      args.writeableDmnDefinitions,
      args._readonly_externalDmnModelsByNamespaceMap
    );

    this.computeIdentifiersLinksToExpressions();
  }

  get expressions(): Map<string, Expression> {
    return this.repository.expressions;
  }

  get identifiers(): Map<string, IdentifierContext> {
    return this.repository.identifiers;
  }

  public reload() {
    this.repository.reload();
    this.computeIdentifiersLinksToExpressions();
  }

  public changeType(args: { identifierUuid: string; newType: string | undefined }) {
    const context = this.repository.identifiersContextIndexedByUuid.get(args.identifierUuid);
    if (context) {
      context.identifier.typeRef = this.repository.getTypeRef(args.newType);
      if (context.identifier.applyTypeRefToSource) {
        context.identifier.applyTypeRefToSource();
      }
    }
    this.applyExpressionsChangesToDefinition();
  }

  /**
   * Rename some specific identifier to the new name.
   * We need to use the `identifierUuid` because the names are context dependent, so we may have the same identifier
   * name in more than one context.
   * @param args An object with the `identifierUuid` and the `newName`.
   */
  public rename(args: { identifierUuid: string; newName: string }) {
    const context = this.repository.identifiersContextIndexedByUuid.get(args.identifierUuid);
    if (context) {
      for (const expression of context.identifier.expressionsThatUseTheIdentifier.values()) {
        expression.renameIdentifier(context.identifier, args.newName);
      }

      context.identifier.value = args.newName;
      if (context.identifier.applyValueToSource) {
        context.identifier.applyValueToSource();
      }
    } else {
      const dataType = this.repository.dataTypeIndexedByUuid.get(args.identifierUuid);
      if (dataType) {
        for (const expression of dataType.source.expressionsThatUseTheIdentifier.values()) {
          expression.renameIdentifier(dataType.source, args.newName);
        }
        dataType.source.value = args.newName;
      }
    }
    this.applyExpressionsChangesToDefinition();
  }

  public renameImport(args: { oldName: string; newName: string }) {
    const importedIdentifiers = this.repository.importedIdentifiers.get(args.oldName);
    if (importedIdentifiers) {
      for (const imported of importedIdentifiers) {
        const newName = imported.identifier.value.replace(args.oldName + ".", args.newName + ".");
        for (const expression of imported.identifier.expressionsThatUseTheIdentifier.values()) {
          expression.renameIdentifier(imported.identifier, newName);
        }
        imported.identifier.value = newName;
      }
      this.repository.importedIdentifiers.delete(args.oldName);
      this.repository.importedIdentifiers.set(args.newName, importedIdentifiers);
    }
    const importedDataTypes = this.repository.importedDataTypes.get(args.oldName);
    if (importedDataTypes) {
      for (const imported of importedDataTypes) {
        const newDataTypeName = imported.source.value.replace(args.oldName + ".", args.newName + ".");
        for (const expression of imported.source.expressionsThatUseTheIdentifier.values()) {
          expression.renameIdentifier(imported.source, newDataTypeName);
        }
        imported.source.value = newDataTypeName;
      }
      this.repository.importedDataTypes.delete(args.oldName);
      this.repository.importedDataTypes.set(args.newName, importedDataTypes);
    }
    this.applyExpressionsChangesToDefinition();
  }

  public getExpressionsThatUseTheIdentifier(identifierId: string) {
    const identifierContext = this.repository.identifiers.get(identifierId);
    if (!identifierContext) {
      return (
        this.repository.dataTypeIndexedByUuid.get(identifierId)?.source.expressionsThatUseTheIdentifier.values() ?? []
      );
    }

    return identifierContext.identifier.expressionsThatUseTheIdentifier.values();
  }

  private computeIdentifiersLinksToExpressions() {
    const parser = new FeelIdentifiersParser(this.repository);

    for (const expression of this.repository.expressions.values()) {
      for (const identifier of expression.identifiersOfTheExpression) {
        identifier.source?.expressionsThatUseTheIdentifier.delete(expression.uuid);
      }

      // The parser is the only one able to parse all the expressions and set in the repository the links between
      // the existing expressions and the existing identifiers. Otherwise, we just have a collection of known identifiers
      // and known expressions without any link between them.
      const parsedExpression = parser.parse(expression.uuid, expression.fullExpression);
      expression.identifiersOfTheExpression = parsedExpression.feelIdentifiedSymbols;
      for (const feelIdentifiedSymbol of parsedExpression.feelIdentifiedSymbols) {
        feelIdentifiedSymbol.source?.expressionsThatUseTheIdentifier.set(expression.uuid, expression);
      }
    }
  }

  private applyExpressionsChangesToDefinition() {
    for (const expression of this.expressions.values()) {
      expression.applyChangesToExpressionSource();
    }
  }
}
