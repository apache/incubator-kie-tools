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

import { elements, meta } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/meta";

export function keepIntersection(args: {
  fromElement: keyof typeof elements;
  toElement: keyof typeof elements;
  srcObj: any;
  targetObj: any;
}) {
  for (const fkey in (meta as any)[elements[args.fromElement]]) {
    // keeps all common elements
    if ((meta as any)[elements[args.toElement]][fkey]) {
      args.targetObj[fkey] = args.srcObj[fkey];
    }

    // removes differences
    else {
      delete args.targetObj[fkey];
    }
  }
}
