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
import * as vscode from "vscode";

export interface BPMNValidationResponse {
  uri: vscode.Uri;
  processID: string;
  name: string;
  error: string;
}

export interface DMNValidationResponse {
  severity: string;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export function parseBPMNValidationResponse(jsonResponse: string[]): BPMNValidationResponse[] {
  if (jsonResponse.length > 0) {
    return jsonResponse.map((response) => {
      const uriMarker = "Uri: ";
      const processIdMarker = "Process id: ";
      const nameMarker = "name : ";
      const errorMarker = "error : ";

      const splitResponse = response.split(" - ");

      const uriResponse = splitResponse.find((resp) => resp.startsWith(uriMarker));
      const processIdResponse = splitResponse.find((resp) => resp.startsWith(processIdMarker));
      const nameResponse = splitResponse.find((resp) => resp.startsWith(nameMarker));
      const errorResponse = splitResponse.find((resp) => resp.startsWith(errorMarker));

      const uri = uriResponse ? uriResponse.substring(uriMarker.length).trim() : "";
      const processId = processIdResponse ? processIdResponse.substring(processIdMarker.length).trim() : "";
      const name = nameResponse ? nameResponse.substring(nameMarker.length).trim() : "";
      const error = errorResponse ? errorResponse.substring(errorMarker.length).trim() : response;

      return {
        uri: vscode.Uri.parse(uri),
        processID: processId,
        name: name,
        error: error,
      };
    });
  } else {
    return [];
  }
}

export function parseDMNValidationResponse(json: any): DMNValidationResponse[] {
  if (json && !json.details) {
    return json as DMNValidationResponse[];
  } else {
    return [];
  }
}
