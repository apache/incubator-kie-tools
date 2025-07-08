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

import SimpleSchema2Bridge from "uniforms-bridge-simple-schema-2";
import SimpleSchema from "simpl-schema";

const schema = new SimpleSchema({
  date: {
    type: Date,
    defaultValue: new Date(),
  },

  adult: {
    type: Boolean,
  },

  size: {
    type: String,
    defaultValue: "m",
    allowedValues: ["xs", "s", "m", "l", "xl"],
  },

  rating: {
    type: Number,
    allowedValues: [1, 2, 3, 4, 5],
    uniforms: {
      checkboxes: true,
    },
  },

  hello: {
    type: Object,
  },

  "hello.something": {
    type: String,
  },

  "hello.somethingelse": {
    type: String,
  },

  friends: {
    type: Array,
    minCount: 1,
  },

  "friends.$": {
    type: Object,
    uniforms: {
      label: false,
    },
  },

  "friends.$.name": {
    type: String,
    min: 3,
  },

  "friends.$.age": {
    type: Number,
    min: 0,
    max: 150,
  },
} as any);

export const bridge = new SimpleSchema2Bridge(schema);
