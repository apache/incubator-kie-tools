/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { StateControlActions, VersionActions } from "./PMMLReducer";
import { DataDictionaryActions } from "./DataDictionaryReducer";
import { DataFieldActions } from "./DataFieldReducer";
import { HeaderActions } from "./HeaderReducer";
import { ModelActions } from "./ModelReducer";
import { ScorecardActions } from "./ScorecardReducer";
import { CharacteristicsActions } from "./CharacteristicsReducer";
import { CharacteristicActions } from "./CharacteristicReducer";
import { AttributesActions } from "./AttributesReducer";

export type ActionMap<M extends { [index: string]: any }> = {
  [Key in keyof M]: M[Key] extends undefined
    ? {
        type: Key;
        payload: undefined;
      }
    : {
        type: Key;
        payload: M[Key];
      };
};

export enum Actions {
  SetVersion = "SET_VERSION",
  DeleteModel = "DELETE_MODEL",
  CreateDataField = "CREATE_DATA_FIELD",
  DeleteDataField = "DELETE_DATA_FIELD",
  SetDataFieldName = "SET_DATA_FIELD_NAME",
  SetHeaderDescription = "SET_HEADER_DESCRIPTION",
  Undo = "UNDO",
  Redo = "REDO",
  Scorecard_SetCoreProperties = "SCORECARD_SET_CORE_PROPERTIES",
  Scorecard_AddCharacteristic = "SCORECARD_ADD_CHARACTERISTIC",
  Scorecard_DeleteCharacteristic = "SCORECARD_DELETE_CHARACTERISTIC",
  Scorecard_UpdateCharacteristic = "SCORECARD_UPDATE_CHARACTERISTIC",
  Scorecard_AddAttribute = "SCORECARD_ADD_ATTRIBUTE",
  Scorecard_DeleteAttribute = "SCORECARD_DELETE_ATTRIBUTE",
  Scorecard_UpdateAttribute = "SCORECARD_UPDATE_ATTRIBUTE"
}

export type AllActions =
  | StateControlActions
  | VersionActions
  | HeaderActions
  | DataDictionaryActions
  | DataFieldActions
  | ModelActions
  | ScorecardActions
  | CharacteristicsActions
  | CharacteristicActions
  | AttributesActions;
