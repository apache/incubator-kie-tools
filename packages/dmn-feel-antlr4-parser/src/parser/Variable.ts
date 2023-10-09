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
import { SymbolType } from "./SymbolType";

/**
 * Describe a variable in FEEL.
 */
export interface Variable {
  /**
   * The name/value of the variable.
   */
  value: string;

  /**
   * The source of the variable.
   */
  variableType: SymbolType;

  /**
   * Some variables are of one of the custom data types defined by user.
   */
  typeRef?: DataType | undefined;
}
