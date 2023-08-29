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

import { Reducer } from "react";
import { HistoryService } from "./HistoryProvider";
import { ModelType } from "..";
import { Model } from "@kie-tools/pmml-editor-marshaller";
import { ValidationRegistry } from "../validation";

export type HistoryAwareReducer<S, A> = (historyService: HistoryService) => Reducer<S, A>;

export type HistoryAwareValidatingReducer<S, A> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
) => Reducer<S, A>;

export interface ModelReducerBinding<S, A> {
  reducer: Reducer<S, A>;
  //A Factory is required to instantiate an *Object* as opposed to just *JSON structure* as
  //instanceof is used to determine whether an editor exists for a specific Model type
  factory: (data: S) => S;
}

export type HistoryAwareModelReducer<A> = (
  historyService: HistoryService,
  modelReducers: Map<ModelType, ModelReducerBinding<any, any>>
) => Reducer<Model[], A>;
