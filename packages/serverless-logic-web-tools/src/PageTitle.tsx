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
import { APP_NAME } from "./AppConstants";

/**
 *
 * Sets the title of the page by concatenating the given title sections and append the application name.
 * @param titleSections An array of strings representing the sections of the title in the order of [parent, children]
 * @returns
 */
export function setPageTitle(titleSections: string[]): void {
  document.title = [APP_NAME, ...titleSections].reverse().join(" | ");
}
