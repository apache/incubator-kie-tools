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

import fastDeepEqual from "fast-deep-equal";

/**
 * This function is the reason why nodes and edges are memoized. Without it, the prev and next objects are compared as
 * references, and they'll always be different, resulting in every node and edge re-rendering for every change. By
 * comparing the values inside the objects, we avoid re-rendering unnecessarily.
 */
export const propsHaveSameValuesDeep = fastDeepEqual;
