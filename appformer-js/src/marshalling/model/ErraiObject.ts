/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ErraiObjectConstants } from "./ErraiObjectConstants";

export interface ErraiObject {
  [ErraiObjectConstants.ENCODED_TYPE]: string;
  [ErraiObjectConstants.OBJECT_ID]?: string;
  [ErraiObjectConstants.NUM_VAL]?: string | number | boolean;
  [ErraiObjectConstants.VALUE]?: any;
  [ErraiObjectConstants.ENUM_STRING_VALUE]?: string;
}
