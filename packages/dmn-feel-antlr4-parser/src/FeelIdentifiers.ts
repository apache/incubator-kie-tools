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

import { FeelIdentifiersParser } from "./parser/FeelIdentifiersParser";
import { DmnDefinitions, IdentifiersRepository } from "./parser/IdentifiersRepository";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { ParsedExpression } from "./parser/ParsedExpression";
import { Expression } from "./parser/Expression";

export class FeelIdentifiers {
  private readonly parser: FeelIdentifiersParser;
  private readonly repository: IdentifiersRepository;

  constructor(args: {
    _readonly_dmnDefinitions: DmnDefinitions;
    _readonly_externalDefinitions?: Map<string, DmnLatestModel>;
  }) {
    this.repository = new IdentifiersRepository(args._readonly_dmnDefinitions, args._readonly_externalDefinitions);
    this.parser = new FeelIdentifiersParser(this.repository);
  }

  public parse(args: { identifierContextUuid: string; expression: string }): ParsedExpression {
    return this.parser.parse(args.identifierContextUuid, args.expression);
  }

  get expressions(): Map<string, Expression> {
    return this.repository.expressions;
  }
}
