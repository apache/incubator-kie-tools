import * as React from "react";
import { BoxedExpressionEditorBase } from "../../boxedExpressionEditorBase";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../../src/api";

export function BureauCallType() {
  return (
    <BoxedExpressionEditorBase
      expressionDefinition={{
        id: "_A9404499-4077-49F3-BFD5-CE5AE13047F6",
        name: "Bureau call type",
        dataType: "t.BureauCallType" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Context,
        contextEntries: [
          {
            entryInfo: {
              id: "_1AEC97F1-F123-457A-915A-78C26BE22942",
              name: "Pre-Bureau Risk Category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
            },
            entryExpression: {
              id: "_2060068A-7599-493C-B543-870636E7F074",
              name: "Pre-Bureau Risk Category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Pre-bureau risk category",
              width: 600,
            },
          },
        ],
        result: {
          id: "_934EA682-515A-4C91-86E9-9DA2F8C00B33",
          name: "Result Expression",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.DecisionTable,
          hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
          aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
          annotations: [{ name: "annotation-1", width: 145 }],
          input: [
            {
              id: "_759A209A-55EC-4924-B00B-82E45EEEE76D",
              idLiteralExpression: "_759A209A-55EC-4924-B00B-82E45EEEE763",
              name: "Pre-bureau risk category",
              dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
              width: 210,
            },
          ],
          output: [
            {
              id: "_10AF3227-ACC7-4DE9-A572-D9896FEF36D5",
              name: "Result Expression",
              dataType: DmnBuiltInDataType.Undefined,
              width: 175,
            },
          ],
          rules: [
            {
              id: "_E40EB291-316C-4C78-A71C-7CA5B6156F7A",
              inputEntries: [
                {
                  id: "_207A0907-173B-4F16-83E9-7F1F0609739A",
                  content: '"High", "Medium"',
                },
              ],
              outputEntries: [{ id: "_CE08E0C4-7F0C-4B40-A0C5-B4C55E9CB468", content: '"Full"' }],
              annotationEntries: [""],
            },
            {
              id: "_6566163E-72AA-4281-92FE-962FB4C6E365",
              inputEntries: [{ id: "_E92E79EB-A34B-40C3-B4F6-ACD13194BB9B", content: '"Low"' }],
              outputEntries: [{ id: "_24FB227A-571E-4335-AE9D-0272971374A0", content: '"Mini"' }],
              annotationEntries: [""],
            },
            {
              id: "_C7B1B436-8729-40BC-BD87-9DB5E9A71240",
              inputEntries: [
                {
                  id: "_E2EB76AE-34E7-4034-A729-4A352953471B",
                  content: '"Very Low", "Decline"',
                },
              ],
              outputEntries: [{ id: "_60641811-FF0C-4E74-A2C7-9E94CB3B3ABE", content: '"None"' }],
              annotationEntries: [""],
            },
          ],
        },
        entryInfoWidth: 166,
      }}
    />
  );
}
