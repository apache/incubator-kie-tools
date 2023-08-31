import * as React from "react";
import { BoxedExpressionEditorBase } from "../../boxedExpressionEditorBase";
import {
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
} from "../../../../../src/api";

export function RequiredMonthlyInstallment() {
  return (
    <BoxedExpressionEditorBase
      expressionDefinition={{
        id: "_1DB9D3E9-C836-428F-A726-474813ECBCFA",
        name: "Required monthly installment",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Context,
        contextEntries: [
          {
            entryInfo: {
              id: "_C7147354-28C4-4174-AAD2-D46F5CF8DF6D",
              name: "Installment calculation",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_2C7FED80-B0E8-4BD1-BE41-C7F39EA00FE0",
              name: "Installment calculation",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Function,
              formalParameters: [
                {
                  id: "_00C17E28-F742-4415-A7C0-01AF2A368E8C",
                  name: "Product Type",
                  dataType: "t.ProductType" as DmnBuiltInDataType,
                },
                { id: "_36143DE8-AC2A-4FCC-938F-7B2C03FAF9BE", name: "Rate", dataType: DmnBuiltInDataType.Number },
                { id: "_D24EF3AA-9B03-46A8-A06F-15709C1A4904", name: "Term", dataType: DmnBuiltInDataType.Number },
                { id: "_FBFDD58D-7F4E-481D-B2BB-2B5BE75A5600", name: "Amount", dataType: DmnBuiltInDataType.Number },
              ],
              functionKind: FunctionExpressionDefinitionKind.Feel,
              expression: {
                id: "_7544F040-0F29-4B0B-8FAC-E395F0DA30A3",
                name: "Feel Expression",
                dataType: DmnBuiltInDataType.Undefined,
                logicType: ExpressionDefinitionLogicType.Context,
                contextEntries: [
                  {
                    entryInfo: {
                      id: "_A9A94B13-44CC-460E-8BF3-7BBDCB9E51B7",
                      name: "Monthly Fee",
                      dataType: DmnBuiltInDataType.Number,
                    },
                    entryExpression: {
                      id: "_BB6E6CE5-1DA2-4975-8073-D30FA9565168",
                      name: "Monthly Fee",
                      dataType: DmnBuiltInDataType.Number,
                      logicType: ExpressionDefinitionLogicType.Literal,
                      content:
                        'if Product Type = "Standard Loan"\nthen 20.00\nelse if Product Type = "Special Loan"\nthen 25.00\nelse null',
                      width: 366,
                    },
                  },
                  {
                    entryInfo: {
                      id: "_02342C14-A555-4640-8B6A-0916513254B4",
                      name: "Monthly Repayments",
                      dataType: DmnBuiltInDataType.Number,
                    },
                    entryExpression: {
                      id: "_AA35EECA-C842-43AF-BE4C-656FAA3BBA77",
                      name: "Monthly Repayments",
                      dataType: DmnBuiltInDataType.Number,
                      logicType: ExpressionDefinitionLogicType.Literal,
                      content: "(Amount*Rate/12)/(1-(1+Rate/12)**-Term)",
                      width: 366,
                    },
                  },
                ],
                result: {
                  id: "_0784B73A-F988-4524-9D6C-19C36E1EFF51",
                  name: "Result Expression",
                  dataType: DmnBuiltInDataType.Undefined,
                  logicType: ExpressionDefinitionLogicType.Literal,
                  content: "Monthly Fee + Monthly Repayments",
                  width: 366,
                },
                entryInfoWidth: 120,
              },
            },
          },
        ],
        result: {
          id: "_937F7650-5BE5-4BFA-9DDB-E6883D880CCA",
          name: "Result Expression",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.Invocation,
          invokedFunction: { id: "_8EB25DF6-3184-4CEB-BC2A-D5FF4173E42D", name: "Installment calculation" },
          bindingEntries: [
            {
              entryInfo: {
                id: "_C91DA2E1-E9F7-42CC-903A-B4E448627840",
                name: "Product Type",
                dataType: "t.ProductType" as DmnBuiltInDataType,
              },
              entryExpression: {
                id: "_8370F9B3-6F77-427C-BCAC-70ADF3DAC5C2",
                name: "Product Type",
                dataType: "t.ProductType" as DmnBuiltInDataType,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "Requested product.ProductType",
                width: 428,
              },
            },
            {
              entryInfo: {
                id: "_D7C6DD33-55CE-48CB-8C9B-A871D7755896",
                name: "Rate",
                dataType: DmnBuiltInDataType.Number,
              },
              entryExpression: {
                id: "_7B16911B-C8B4-403C-99BE-0FAD8D45D473",
                name: "Rate",
                dataType: DmnBuiltInDataType.Number,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "Requested product.Rate",
                width: 428,
              },
            },
            {
              entryInfo: {
                id: "_1AF2E31C-19AF-4EF9-8D5B-A4CB78C358E7",
                name: "Term",
                dataType: DmnBuiltInDataType.Number,
              },
              entryExpression: {
                id: "_538E29D6-61AC-4660-8F1B-ACFC750AF39E",
                name: "Term",
                dataType: DmnBuiltInDataType.Number,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "Requested product.Term",
                width: 428,
              },
            },
            {
              entryInfo: {
                id: "_075848DB-DD85-444B-8318-6C2B9783F57C",
                name: "Amount",
                dataType: DmnBuiltInDataType.Number,
              },
              entryExpression: {
                id: "_AEFC27C0-4811-4248-84BE-D0BA774826E6",
                name: "Amount",
                dataType: DmnBuiltInDataType.Number,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "Requested product.Amount",
                width: 428,
              },
            },
          ],
          entryInfoWidth: 120,
        },
        entryInfoWidth: 186,
      }}
    />
  );
}
