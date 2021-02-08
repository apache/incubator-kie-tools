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
import { Reducer } from "react";
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import {
  FieldName,
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OpType,
  OutlierTreatmentMethod,
  UsageType
} from "@kogito-tooling/pmml-editor-marshaller";
import { Builder, ValidationService } from "../validation";
import {
  areLowHighValuesRequired,
  isInvalidValueReplacementRequired,
  isMissingValueReplacementRequired,
  validateMiningField,
  validateMiningFields
} from "../validation/MiningSchema";

interface MiningSchemaFieldPayload {
  [Actions.UpdateMiningSchemaField]: {
    readonly modelIndex: number;
    readonly miningSchemaIndex: number;
    readonly name: FieldName;
    readonly originalName: FieldName | undefined;
    readonly usageType: UsageType | undefined;
    readonly optype: OpType | undefined;
    readonly importance: number | undefined;
    readonly outliers: OutlierTreatmentMethod | undefined;
    readonly lowValue: number | undefined;
    readonly highValue: number | undefined;
    readonly missingValueTreatment: MissingValueTreatmentMethod | undefined;
    readonly missingValueReplacement: any | undefined;
    readonly invalidValueTreatment: InvalidValueTreatmentMethod | undefined;
    readonly invalidValueReplacement: any | undefined;
  };
}

export type MiningSchemaFieldActions = ActionMap<MiningSchemaFieldPayload>[keyof ActionMap<MiningSchemaFieldPayload>];

export const MiningSchemaFieldReducer: HistoryAwareValidatingReducer<MiningField[], AllActions> = (
  historyService: HistoryService,
  validationService: ValidationService
): Reducer<MiningField[], AllActions> => {
  return (state: MiningField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateDataDictionaryField:
        state.forEach((mf, index) => {
          if (mf.name === action.payload.originalName) {
            historyService.batch(state, `models[${action.payload.modelIndex}].MiningSchema.MiningField`, draft => {
              draft[index] = {
                ...draft[index],
                name: action.payload.dataField.name
              };
            });
          }
        });
        break;

      case Actions.UpdateMiningSchemaField:
        historyService.batch(state, `models[${action.payload.modelIndex}].MiningSchema.MiningField`, draft => {
          const modelIndex = action.payload.modelIndex;
          const miningSchemaIndex = action.payload.miningSchemaIndex;
          const _areLowHighValuesRequired = areLowHighValuesRequired(action.payload.outliers);
          const _isMissingValueReplacementRequired = isMissingValueReplacementRequired(
            action.payload.missingValueTreatment
          );
          const _isInvalidValueReplacementRequired = isInvalidValueReplacementRequired(
            action.payload.invalidValueTreatment
          );

          if (miningSchemaIndex >= 0 && miningSchemaIndex < draft.length) {
            const outlierChanged = draft[miningSchemaIndex].outliers !== action.payload.outliers;
            const missingValueTreatmentChanged =
              draft[miningSchemaIndex].missingValueTreatment !== action.payload.missingValueTreatment;
            const invalidValueTreatmentChanged =
              draft[miningSchemaIndex].invalidValueTreatment !== action.payload.invalidValueTreatment;
            const clearLowHighValues = outlierChanged && !_areLowHighValuesRequired;
            const clearMissingValueReplacement = missingValueTreatmentChanged && !_isMissingValueReplacementRequired;
            const clearInvalidValueReplacement = invalidValueTreatmentChanged && !_isInvalidValueReplacementRequired;
            const newLowValue = clearLowHighValues ? undefined : action.payload.lowValue;
            const newHighValue = clearLowHighValues ? undefined : action.payload.highValue;
            const newMissingValueReplacement = clearMissingValueReplacement
              ? undefined
              : action.payload.missingValueReplacement;
            const newInvalidValueReplacement = clearInvalidValueReplacement
              ? undefined
              : action.payload.invalidValueReplacement;

            draft[miningSchemaIndex] = {
              ...draft[miningSchemaIndex],
              name: action.payload.name,
              usageType: action.payload.usageType,
              optype: action.payload.optype,
              importance: action.payload.importance,
              outliers: action.payload.outliers,
              lowValue: newLowValue,
              highValue: newHighValue,
              missingValueTreatment: action.payload.missingValueTreatment,
              missingValueReplacement: newMissingValueReplacement,
              invalidValueTreatment: action.payload.invalidValueTreatment,
              invalidValueReplacement: newInvalidValueReplacement
            };
            validationService.clear(
              Builder()
                .forModel(modelIndex)
                .forMiningSchema()
                .forMiningField(miningSchemaIndex)
                .build()
            );
            validateMiningField(
              action.payload.modelIndex,
              action.payload.miningSchemaIndex,
              {
                ...action.payload,
                lowValue: newLowValue,
                highValue: newHighValue,
                missingValueReplacement: newMissingValueReplacement,
                invalidValueReplacement: newInvalidValueReplacement
              },
              validationService
            );
          }
        });
        break;

      case Actions.Validate:
        if (action.payload.modelIndex !== undefined) {
          const modelIndex = action.payload.modelIndex;
          validateMiningFields(modelIndex, state, validationService);
        }
    }

    return state;
  };
};
