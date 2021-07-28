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
import { DataDictionary, DataField, FieldName, PMML, PMML2XML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";

describe("DataDictionary tests", () => {
  test("Empty", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary/>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary).not.toBeNull();
    expect(dataDictionary.numberOfFields).toBeUndefined();
    expect(dataDictionary.DataField).not.toBeUndefined();

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataDictionary/>`);
  });

  test("Add DataField", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary/>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    const dataField: DataField = new DataField({
      name: "field1" as FieldName,
      dataType: "string",
      optype: "categorical",
    });

    dataDictionary.DataField.push(dataField);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataDictionary numberOfFields="1">`);
    expect(xml).toContain(`<DataField name="field1" optype="categorical" dataType="string"/>`);
  });

  test("Update DataField", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary numberOfFields="1">
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.numberOfFields).toBe(1);
    expect(dataDictionary.DataField.length).toBe(1);
    expect(dataDictionary.DataField[0].name).toBe("field1");
    expect(dataDictionary.DataField[0].optype).toBe("categorical");
    expect(dataDictionary.DataField[0].dataType).toBe("string");

    dataDictionary.DataField[0].name = "field1-changed" as FieldName;
    dataDictionary.DataField[0].optype = "continuous";
    dataDictionary.DataField[0].dataType = "integer";

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataDictionary numberOfFields="1">`);
    expect(xml).toContain(`<DataField name="field1-changed" optype="continuous" dataType="integer"/>`);
  });

  test("Delete DataField", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary numberOfFields="1">
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.DataField.length).toBe(1);

    dataDictionary.DataField.splice(0, 1);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataDictionary/>`);
  });
});
