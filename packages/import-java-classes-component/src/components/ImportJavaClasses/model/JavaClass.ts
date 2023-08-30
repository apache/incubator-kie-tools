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

import { JavaField } from "./JavaField";

export class JavaClass {
  /** Java Class Name (eg. java.lang.String OR com.mypackage.Test) */
  public name: string;
  /** Java Fields of the class */
  public fields: JavaField[];
  /** It indicates if the fields has been loaded, in order to support empty fields Java Classes */
  public fieldsLoaded: boolean;

  constructor(name: string) {
    this.name = name;
    this.fields = [];
    this.fieldsLoaded = false;
  }

  setFields(fields: JavaField[]) {
    this.fields = fields;
    this.fieldsLoaded = true;
  }
}
