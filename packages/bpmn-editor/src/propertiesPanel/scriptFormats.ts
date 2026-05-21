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

export enum ScriptLanguage {
  Java = "Java",
  MVEL = "MVEL",
  Drools = "Drools",
  FEEL = "FEEL",
}

const SCRIPT_LANGUAGE_FORMATS: Record<ScriptLanguage, string> = {
  [ScriptLanguage.Java]: "http://www.java.com/java",
  [ScriptLanguage.MVEL]: "http://www.mvel.org/2.0",
  [ScriptLanguage.Drools]: "http://www.jboss.org/drools/rule",
  [ScriptLanguage.FEEL]: "http://www.omg.org/spec/DMN/20180521/FEEL/",
};

export function getScriptFormat(language: ScriptLanguage): string {
  return SCRIPT_LANGUAGE_FORMATS[language];
}
