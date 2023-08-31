import * as React from "react";
import { BoxedExpressionEditorBase } from "../../boxedExpressionEditorBase";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../../src/api";

export function ApplicationRiskScore() {
  return (
    <BoxedExpressionEditorBase
      expressionDefinition={{
        id: "_545BBDF2-E67A-4CB3-ACC0-E9AFA30D9CD9",
        name: "Application risk score",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Context,
        contextEntries: [
          {
            entryInfo: {
              id: "_0949739F-4A1A-4848-8F12-89B760F89CF1",
              name: "Age",
              dataType: DmnBuiltInDataType.Number,
            },
            entryExpression: {
              id: "_DD17D0BF-9F01-431C-91D0-4546643756B1",
              name: "Age",
              dataType: DmnBuiltInDataType.Number,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.Age",
              width: 619,
            },
          },
          {
            entryInfo: {
              id: "_3204AD73-8DAB-4734-B06C-9EAF204F67D5",
              name: "Maritial Status",
              dataType: "t.MaritalStatus" as DmnBuiltInDataType,
            },
            entryExpression: {
              id: "_E6C2E3C3-0C71-4E37-8FC3-2ABD9C177620",
              name: "Maritial Status",
              dataType: "t.MaritalStatus" as DmnBuiltInDataType,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.MaritialStatus",
              width: 619,
            },
          },
          {
            entryInfo: {
              id: "_186F68D8-1C59-44BA-A7ED-B5F8F34F9DBC",
              name: "Employment Status",
              dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
            },
            entryExpression: {
              id: "_FB79B8A1-051B-4C76-A330-41533D6DA2C8",
              name: "Employment Status",
              dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
              logicType: ExpressionDefinitionLogicType.Literal,
              content: "Applicant data.EmploymentStatus",
              width: 619,
            },
          },
        ],
        result: {
          id: "_C13B5E57-E9AD-49B6-A039-2227EE818DBC",
          name: "Result Expression",
          dataType: DmnBuiltInDataType.Undefined,
          logicType: ExpressionDefinitionLogicType.DecisionTable,
          hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Collect,
          aggregation: DecisionTableExpressionDefinitionBuiltInAggregation.SUM,
          annotations: [{ name: "annotation-1", width: 105 }],
          input: [
            {
              id: "_14344D45-0F6C-428B-B7B6-12F8B592E49D",
              name: "Age",
              dataType: DmnBuiltInDataType.Number,
              width: 100,
              idLiteralExpression: "_BE1DBAB9-BE82-4F8F-8E16-6227731C9E65",
            },
            {
              id: "_8AC7E6EE-33A8-41F7-BCC7-F53CF6FFD661",
              name: "Employment Status",
              dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
              width: 140,
              idLiteralExpression: "_E7F1CC56-2891-48BF-92A1-41414FC14994",
            },
            {
              id: "_0CBCCE0C-7C87-4FC4-83F8-FD4ED6F26C7D",
              name: "Maritial Status",
              dataType: "t.MaritalStatus" as DmnBuiltInDataType,
              width: 111,
              idLiteralExpression: "_4A1DB037-D38D-45D1-A8E8-CB17896811B2",
            },
          ],
          output: [
            {
              id: "_5A362A4B-0A54-449D-98FB-71B42F2A43A5",
              name: "Result Expression",
              dataType: DmnBuiltInDataType.Undefined,
              width: 123,
            },
          ],
          rules: [
            {
              id: "_D4A2DC05-376D-4952-B5E0-AAB17CC39AAE",
              inputEntries: [
                { id: "_ECB98A21-E3FE-442C-BBD5-27D6734ACE71", content: "[18..22)" },
                { id: "_F26E6F86-5F2A-42AA-ADC3-7FB743B96A31", content: "-" },
                { id: "_DFE4497B-AA57-4EBE-AAED-7DE8DD5D26EC", content: "-" },
              ],
              outputEntries: [{ id: "_252B7272-C7EB-4E01-85A6-B57D745382E5", content: "32" }],
              annotationEntries: [""],
            },
            {
              id: "_02AFCCBC-0810-479A-A08F-10948378718B",
              inputEntries: [
                { id: "_7947E3F5-671A-4D48-9FE0-323723BFEED2", content: "[22..26)" },
                { id: "_F8334B11-4C5C-4D81-A85A-7B45E148B91C", content: "-" },
                { id: "_9965CDF2-769B-4314-A3B8-735FE1BE03DA", content: "-" },
              ],
              outputEntries: [{ id: "_888FA796-5632-41C9-8968-194C9E8873CD", content: "35" }],
              annotationEntries: [""],
            },
            {
              id: "_3746325F-9D75-4CA9-8609-85F7A4A3059E",
              inputEntries: [
                { id: "_824F69DA-6323-49F2-8AE5-0181F2824370", content: "[26..36)" },
                { id: "_4ED26A91-7B93-4547-A47E-E1A337342ADE", content: "-" },
                { id: "_E0FA2C13-B67A-42C6-B420-DFA3245D66B4", content: "-" },
              ],
              outputEntries: [{ id: "_AB00BB73-AE86-4B77-8DE4-67AB16992E1F", content: "40" }],
              annotationEntries: [""],
            },
            {
              id: "_9D5BB3D6-9261-4BCB-9E48-81C6FDDE7756",
              inputEntries: [
                { id: "_B68B9769-CE93-4314-9722-FA38CFDF059A", content: "[36..50)" },
                { id: "_D65A3F34-D05C-48BB-A315-8E7936C2FF8E", content: "-" },
                { id: "_68965D5E-E84B-4736-BAA8-2ADCBA090DA2", content: "-" },
              ],
              outputEntries: [{ id: "_FF3F89A5-4044-41DB-8894-7B929A2EE5D0", content: "43" }],
              annotationEntries: [""],
            },
            {
              id: "_4C2436DC-B41D-45ED-9993-F94EF18C65C7",
              inputEntries: [
                { id: "_7A15945A-3ECB-4CA8-A427-2CF41CD130DC", content: ">=50" },
                { id: "_84025600-AE6A-453D-9B19-84721F1E59CF", content: "-" },
                { id: "_8AEC8F10-FE87-4BAA-AFDE-0DE9D8F8E498", content: "-" },
              ],
              outputEntries: [{ id: "_62671976-2B81-4FED-987A-3810383A6066", content: "48" }],
              annotationEntries: [""],
            },
            {
              id: "_60F717C9-76AF-4634-AB5E-B2E54BAD91D4",
              inputEntries: [
                { id: "_11E67A30-FB52-4A44-97D8-A26268B81271", content: "-" },
                { id: "_2B60FF06-D6CE-49BE-800B-D8037A20E622", content: "-" },
                { id: "_66491435-BADA-47EC-A470-080F8B75B305", content: '"S"' },
              ],
              outputEntries: [{ id: "_11106165-D0F1-4175-BF1C-BB02BB00CE47", content: "25" }],
              annotationEntries: [""],
            },
            {
              id: "_309CC7B3-8772-48DB-92D5-EF9B3219C2F6",
              inputEntries: [
                { id: "_698BAEA6-3A96-46BD-B603-32E7F254DCC1", content: "-" },
                { id: "_F1106E47-747C-41DA-8BC7-D34DA7E22AAB", content: "-" },
                { id: "_FBEAC877-6DD1-4F2D-91F0-FB2CF4320DC6", content: '"M"' },
              ],
              outputEntries: [{ id: "_47ADC293-292F-4651-B59A-12EA2463A39D", content: "45" }],
              annotationEntries: [""],
            },
            {
              id: "_55272FBE-EC6A-446B-BFD1-EFC5CD961A1D",
              inputEntries: [
                { id: "_E673FB0C-0E9C-45BD-A09E-58A3E867B146", content: "-" },
                {
                  id: "_4D1D52D4-153D-4AD1-983B-4BE2089FFDDA",
                  content: '"Unemployed"',
                },
                { id: "_685A91C6-81EA-47D1-A7B7-CF66C8FA6881", content: "-" },
              ],
              outputEntries: [{ id: "_0F69DAE2-59F3-4711-90D0-315CCD778381", content: "15" }],
              annotationEntries: [""],
            },
            {
              id: "_7186AB80-196C-4C2F-BD2E-8AD50B3932F9",
              inputEntries: [
                { id: "_78F43238-22C4-4E85-AF43-494FB7D69350", content: "-" },
                {
                  id: "_C38CE61F-0A17-4378-8319-145DDA9CC785",
                  content: '"Student"',
                },
                { id: "_A232AB77-3A8D-4E17-9CBB-9B70554CF532", content: "-" },
              ],
              outputEntries: [{ id: "_8680DD3C-4310-4B33-A098-9110D722AF97", content: "18" }],
              annotationEntries: [""],
            },
            {
              id: "_3BD759E2-56C1-4A12-B6C2-8E964909CB03",
              inputEntries: [
                { id: "_96E4F818-78F2-488A-A2DC-4E8A825FD986", content: "-" },
                {
                  id: "_E8C56FFE-95B2-4994-B7EB-1F969E724255",
                  content: '"Employed"',
                },
                { id: "_9EAEE2B4-B6BC-4B0E-8A8B-D2674EE80F6B", content: "-" },
              ],
              outputEntries: [{ id: "_7E7AFF1D-96F6-4A71-B625-E170CB00A297", content: "45" }],
              annotationEntries: [""],
            },
            {
              id: "_4F5AC672-7D8F-41D4-AFE3-65266126FCCE",
              inputEntries: [
                { id: "_F6971233-A445-4045-9297-BFBB603311B1", content: "-" },
                {
                  id: "_59F48BD1-0194-42FD-A373-1B00B6D326B9",
                  content: '"Self-Employed"',
                },
                { id: "_5886BC65-D0E5-4F16-89D6-4E4947E9B5EE", content: "-" },
              ],
              outputEntries: [{ id: "_B6DA87F4-66A2-4D33-A078-92FC335214D1", content: "36" }],
              annotationEntries: [""],
            },
          ],
        },
        entryInfoWidth: 176,
      }}
    />
  );
}
