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

import { BoxedExpressionEditorI18n } from "..";
import { de as de_common } from "@kie-tools/i18n-common-dictionary";

export const de: BoxedExpressionEditorI18n = {
  ...de_common,
  addParameter: "Parameter hinzufügen",
  builtInAggregator: "Integrierter Aggregator",
  builtInAggregatorHelp: {
    sum: "Gibt die Summe aller gesammelten Werte aus. Die Werte müssen numerisch sein.",
    count: "Gibt die Anzahl passender Regeln aus.",
    min: "Gibt den kleinsten Wert unter den Ergebnissen aus. Die resultierenden Werte müssen vergleichbar sein, z. B. Zahlen, Daten oder Text (lexikografische Reihenfolge).",
    max: "Gibt den größten Wert unter den Ergebnissen aus. Die resultierenden Werte müssen vergleichbar sein, z. B. Zahlen, Daten oder Text (lexikografische Reihenfolge).",
    none: "Aggregiert Werte in einer beliebigen Liste.",
  },
  choose: "Auswählen...",
  class: "Klasse",
  columnOperations: {
    delete: "Löschen",
    insertLeft: "Links einfügen",
    insertRight: "Rechts einfügen",
  },
  columns: "SPALTEN",
  context: "Kontext",
  contextEntry: "KONTEXT-EINTRAG",
  dataType: "Datentyp",
  dataTypeDropDown: {
    builtIn: "BUILT-IN",
    custom: "CUSTOM",
  },
  decisionRule: "ENTSCHEIDUNGSREGEL",
  decisionTable: "Entscheidungstabelle",
  delete: "Löschen",
  document: "Dokument",
  editClause: {
    input: "Eingangsklausel bearbeiten",
    output: "Ausgangsklausel bearbeiten",
  },
  editContextEntry: "Kontexteintrag bearbeiten",
  editExpression: "Ausdruck bearbeiten",
  editHitPolicy: "Hit Policy bearbeiten",
  editParameter: "Parameter bearbeiten",
  editParameters: "Parameter bearbeiten",
  editRelation: "Relation bearbeiten",
  enterFunction: "Funktionsname",
  enterText: "Text eingeben",
  expression: "Ausdruck",
  function: "Funktion",
  hitPolicy: "Hit Policy",
  hitPolicyHelp: {
    unique: "Erlaubt nur eine Übereinstimmung mit einer Regel. Jede Überschneidung führt zu einem Fehler.",
    first: "Verwendet den ersten Treffer in der Reihenfolge der Regeln.",
    priority:
      "Ermöglicht die Übereinstimmung mehrerer Regeln mit unterschiedlichen Ausgaben. Die Ausgabe, die in der Liste der Ausgabewerte an erster Stelle steht, wird ausgewählt.",
    any: "Erlaubt die Übereinstimmung mehrerer Regeln, die jedoch alle die gleiche Ausgabe haben müssen. Wenn mehrere übereinstimmende Regeln nicht die gleiche Ausgabe haben, wird ein Fehler ausgegeben.",
    collect: "Aggregiert die Ausgabe von mehreren Regeln auf der Grundlage einer Aggregationsfunktion.",
    ruleOrder:
      "Sammelt die Ausgabe von mehreren Regeln in einer Liste, die nach der Reihenfolge der Regeln geordnet ist. Es ist vergleichbar mit 'Collect' ohne Aggregationsfunktion, aber mit expliziter konsistenter Reihenfolge in der endgültigen Liste wie in der Tabelle definiert.",
    outputOrder:
      "Sammelt die Ausgabe von mehreren Regeln in einer Liste, die nach demselben Sortiermechanismus wie die Trefferrichtlinie 'Priorität' geordnet ist.",
  },
  inputClause: "EINGANGS-KLAUSEL",
  insert: "Insert",
  insertDirections: {
    above: "Above",
    below: "Below",
    toTheLeft: "To the left",
    toTheRight: "To the right",
  },
  invocation: "Aufruf",
  list: "Liste",
  literal: "Literal",
  manage: "Verwalten",
  methodSignature: "Methodensignatur",
  model: "Modell",
  name: "Name",
  noParametersDefined: "Es wurden keine Parameter definiert.",
  outputClause: "AUSGABE-KLAUSEL",
  parameters: "PARAMETER",
  pmml: {
    firstSelection: "Zunächst PMML-Dokument auswählen",
    secondSelection: "Als nächstes PMML-Dokument auswählen",
  },
  relation: "Relation",
  rowOperations: {
    reset: "Zurücksetzen",
    delete: "Löschen",
    duplicate: "Duplizieren",
    insertAbove: "Oberhalb einfügen",
    insertBelow: "Unterhalb einfügen",
  },
  rows: "REIHEN",
  ruleAnnotation: "REGELANMERKUNG",
  selectExpression: "Ausdruck auswählen",
  selectFunctionKind: "Funktionsart auswählen",
  selectLogicType: "Logiktyp auswählen",
};
