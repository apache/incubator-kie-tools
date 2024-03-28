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

export interface PingResponse {
  version: string;
  started: boolean;
}

export class BPMNValidationResponseParser {
  public static parse(jsonResponse: string[]): BPMNValidationResponse[] {
    if (jsonResponse.length > 0) {
      return jsonResponse.map((response) => {
        const uriMarker = "Uri: ";
        const processIdMarker = " - Process id: ";
        const nameMarker = " - name : ";
        const errorMarker = " - error : ";

        const uriStartIndex = response.indexOf(uriMarker);
        const processIdStartIndex = response.indexOf(processIdMarker);
        const nameStartIndex = response.indexOf(nameMarker);
        const errorStartIndex = response.indexOf(errorMarker);

        const uriEndIndex = response.indexOf(processIdMarker);
        const processIdEndIndex = response.indexOf(nameMarker);
        const nameEndIndex = response.indexOf(errorMarker);
        const errorEndIndex = response.length;

        if (uriStartIndex !== -1 || processIdStartIndex !== -1 || nameStartIndex !== -1 || errorStartIndex !== -1) {
          const uri = response.substring(uriStartIndex + uriMarker.length, uriEndIndex).trim();
          const processId = response.substring(processIdStartIndex + processIdMarker.length, processIdEndIndex).trim();
          const name = response.substring(nameStartIndex + nameMarker.length, nameEndIndex).trim();
          const error = response.substring(errorStartIndex + errorMarker.length, errorEndIndex).trim();

          return {
            uri: vscode.Uri.parse(uri),
            processID: processId,
            name: name,
            error: error,
          };
        } else {
          return {
            uri: vscode.Uri.parse(""),
            processID: "",
            name: "",
            error: response,
          };
        }
      });
    } else {
      return [];
    }
  }
}

export class DMNValidationResponseParser {
  public static parse(json: any): DMNValidationResponse[] {
    if (json && !json.details) {
      return json as DMNValidationResponse[];
    } else {
      return [];
    }
  }
}
