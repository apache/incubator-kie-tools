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

import { DmnUnitablesI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { en as en_boxed_expression } from "@kie-tools/boxed-expression-component/dist/i18n/locales/en";

export const en: DmnUnitablesI18n = {
  ...en_common,
  ...en_boxed_expression,
  schema: {
    selectPlaceholder: "Select...",
  },
};
