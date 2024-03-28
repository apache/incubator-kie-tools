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

import { BoxedExpression } from "./BoxedExpression";

/**
 * This interface defines all the API methods which BoxedExpressionEditor component can use to dialog with GWT Layer
 */
export interface BeeGwtService {
  getDefaultExpressionDefinition(
    logicType: BoxedExpression["__$$element"] | undefined,
    typeRef: string,
    isRoot?: boolean
  ): { expression: BoxedExpression; widthsById: Map<string, number[]> };

  openDataTypePage: () => void; // Just open the data types tab

  selectObject: (uuid?: string) => void; // Changes the Properties panel
}
