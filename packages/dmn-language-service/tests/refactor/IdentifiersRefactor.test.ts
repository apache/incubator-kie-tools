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

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnLatestModel, getMarshaller } from "@kie-tools/dmn-marshaller";
import { includeMathModelDmn, mathDmnModel, sampleLoanDmnModel } from "../fs/fixtures";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

describe("Refactor renamed identifiers", () => {
  test("rename input element - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(sampleLoanDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "Requested Product" to "Changed Input"
    identifiersRefactor.rename({ identifierUuid: "_6E3205AF-7E3D-4ABE-A367-96F3F6E8210E", newName: "Changed Input" });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    expect(
      Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier("_6E3205AF-7E3D-4ABE-A367-96F3F6E8210E"))[0]
        .fullExpression
    ).toEqual(
      "(Changed Input.Amount * ((Changed Input.Rate/100)/12)) / (1-(1/(1+(Changed Input.Rate/100)/12) * -Changed Input.Term))"
    );
  });

  test("rename bkm element - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(sampleLoanDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "Lender Acceptable PITI" to "LenderAcceptable_PITI"
    identifiersRefactor.rename({
      identifierUuid: "_C98BE939-B9C7-43E0-83E8-EE7A16C5276D",
      newName: "LenderAcceptable_PITI",
    });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    expect(
      Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier("_C98BE939-B9C7-43E0-83E8-EE7A16C5276D"))[0]
        .fullExpression
    ).toEqual("if Client PITI <= LenderAcceptable_PITI()\n" + '  then "Sufficient"\n' + '  else "Insufficient"');
  });

  test("rename data type properties - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(sampleLoanDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename the "Monthly" data type to "MON"
    identifiersRefactor.rename({
      identifierUuid: "_bb9ef72e-2e0d-4175-ba58-d613bda7e9b3",
      newName: "MON",
    });

    // Rename the "Tax" property of "Monthly" data type to "The Tax"
    identifiersRefactor.rename({
      identifierUuid: "_4a4d01be-fe97-49a2-8c4c-3a49ff27968d",
      newName: "The Tax",
    });

    // Rename the "Rate" property of "Requested Product" data type to "R_A_T_E"
    identifiersRefactor.rename({
      identifierUuid: "_ab1647c2-cb63-4808-8d90-36d41591a40c",
      newName: "R_A_T_E",
    });

    identifiersRefactor.reload();

    const expressions = Array.from(identifiersRefactor.expressions.values()).map((e) => e.fullExpression);

    expect(expressions).not.toContain(
      "(Requested Product.Amount * ((Requested Product.Rate/100)/12)) / (1-(1/(1+(Requested Product.Rate/100)/12) * -Requested Product.Term))"
    );
    expect(expressions).not.toContain("Applicant Data.Monthly.Tax");
    expect(expressions).not.toContain("Applicant Data.Monthly.Insurance");

    expect(expressions).toContain(
      "(Requested Product.Amount * ((Requested Product.R_A_T_E/100)/12)) / (1-(1/(1+(Requested Product.R_A_T_E/100)/12) * -Requested Product.Term))"
    );
    expect(expressions).toContain("Applicant Data.MON.The Tax");
    expect(expressions).toContain("Applicant Data.MON.Insurance");
  });

  test("rename decision element - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(mathDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "Sum Numbers" to "This Sum 3 Numbers"
    identifiersRefactor.rename({
      identifierUuid: "_D1B3D6E9-83C7-4BD0-B8CE-1D2F15E73826",
      newName: "This Sum 3 Numbers",
    });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    const expressions = Array.from(
      identifiersRefactor.getExpressionsThatUseTheIdentifier("_D1B3D6E9-83C7-4BD0-B8CE-1D2F15E73826")
    ).map((c) => c.fullExpression);

    expect(expressions).toContain("This Sum 3 Numbers + 10 + This Sum 3 Numbers + 20");
    expect(expressions).toContain(
      "10 + This Sum 3 Numbers + 50+This Sum 3 Numbers/10*This Sum 3 Numbers-This Sum 3 Numbers+1"
    );
    expect(expressions).toContain(
      "Inner Calc 1+1+Inner Calc 2+2+This Sum 3 Numbers/Inner Calc 1 + Inner Calc 2 + This Sum 3 Numbers"
    );
    expect(expressions).toContain("This Sum 3 Numbers +10");
    expect(expressions).toContain("This Sum 3 Numbers*2");
  });

  test("rename context entry - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(mathDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "Inner Calc 1" to "Some-Calc-1"
    identifiersRefactor.rename({ identifierUuid: "_0A838F50-7852-45B4-A3A8-615A45D24C90", newName: "Some-Calc-1" });
    // Rename "Inner Calc 2" to "a"
    identifiersRefactor.rename({ identifierUuid: "_B5B3C83D-6BDB-4924-AF58-D7D85EF70BDF", newName: "a" });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    expect(
      Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier("_0A838F50-7852-45B4-A3A8-615A45D24C90"))[0]
        .fullExpression
    ).toEqual("Some-Calc-1+1+a+2+Sum Numbers/Some-Calc-1 + a + Sum Numbers");
  });

  test("rename 'some' variable in Some expression - should update satisfies", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(mathDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "mySomeVar" to "my New Var"
    identifiersRefactor.rename({ identifierUuid: "_46F5BE0C-ADA7-4FE9-B418-48C7D2A3EBFD", newName: "my New Var" });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    expect(
      Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier("_46F5BE0C-ADA7-4FE9-B418-48C7D2A3EBFD"))[0]
        .fullExpression
    ).toEqual("my New Var > 20");
  });

  test("rename 'every' variable in Every expression - should update satisfies", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(mathDmnModel()),
      _readonly_externalDmnModelsByNamespaceMap: new Map(),
    });

    // Rename "myEveryVar" to "x"
    identifiersRefactor.rename({ identifierUuid: "_531BBFBA-6C3B-4A5C-B412-113CF70DF8F4", newName: "x" });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    expect(
      Array.from(identifiersRefactor.getExpressionsThatUseTheIdentifier("_531BBFBA-6C3B-4A5C-B412-113CF70DF8F4"))[0]
        .fullExpression
    ).toEqual("x > 2");
  });

  test("rename included model - should update referenced expressions", async () => {
    const identifiersRefactor = new IdentifiersRefactor({
      writeableDmnDefinitions: getDefinitions(includeMathModelDmn()),
      _readonly_externalDmnModelsByNamespaceMap: new Map([
        ["https://kie.org/dmn/_39AA2E1D-15A9-400B-BA55-B663B90AA2DF", getModel(mathDmnModel())],
      ]),
    });

    identifiersRefactor.renameImport({ oldName: "INCLUDED_MATH", newName: "math" });

    // We reload to re-read the DMN file and make sure that the changes was correctly applied to it
    identifiersRefactor.reload();

    // This is the expression in the model that uses the included nodes.
    expect(identifiersRefactor.expressions.get("_C8B740AC-F2E3-47F4-A9FE-60B6FF1A713B")?.fullExpression).toEqual(
      "math.Input A + math.Input B + math.Input C + math.Sum Numbers + INCLUDED_MATH_FAKE"
    );
  });
});

function getDefinitions(content: string): Normalized<DMN15__tDefinitions> {
  return getMarshaller(content, { upgradeTo: "latest" }).parser.parse().definitions as Normalized<DMN15__tDefinitions>;
}

function getModel(content: string): Normalized<DmnLatestModel> {
  return getMarshaller(content, { upgradeTo: "latest" }).parser.parse() as Normalized<DmnLatestModel>;
}
