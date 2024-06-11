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

export function objToMap<K extends string, V>(obj: Record<K, V>): Map<K, V> {
  const n = new Map<K, V>();
  for (const p in obj) {
    n.set(p, obj[p]);
  }
  return n;
}

export function mapToObj<K extends string, V>(map: Map<K, V>) {
  const n: Record<K, V> = {} as any;
  map.forEach((value, key) => {
    n[key] = value;
  });
  return n;
}
