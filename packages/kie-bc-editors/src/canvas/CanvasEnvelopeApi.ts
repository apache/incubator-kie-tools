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

/**
 * Bridge for Envelope to connect Javascript API for Lienzo with envelope.
 * See {@see CanvasConsumedInteropApi} for full documentation for the API.
 */
export interface CanvasEnvelopeApi {
  canvas_getNodeIds(): Promise<string[]>;

  canvas_getBackgroundColor(uuid: string): Promise<string>;

  canvas_setBackgroundColor(uuid: string, backgroundColor: string): Promise<void>;

  canvas_getBorderColor(uuid: string): Promise<string>;

  canvas_setBorderColor(uuid: string, borderColor: string): Promise<void>;

  canvas_getLocation(uuid: string): Promise<number[]>;

  canvas_getAbsoluteLocation(uuid: string): Promise<number[]>;

  canvas_getDimensions(uuid: string): Promise<number[]>;

  canvas_applyState(uuid: string, state: string): Promise<void>;

  canvas_centerNode(uuid: string): Promise<void>;
}
