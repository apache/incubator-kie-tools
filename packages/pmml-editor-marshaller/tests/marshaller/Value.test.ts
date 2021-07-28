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
import { DataDictionary, DataField, PMML, PMML2XML, Value, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";

describe("Value tests", () => {
  test("Empty", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.DataField.length).toBe(1);
    expect(dataDictionary.DataField[0].Value).not.toBeUndefined();
    expect(dataDictionary.DataField[0].Value?.length).toBe(0);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataField name="field1" optype="categorical" dataType="string"/>`);
  });

  test("Add Value", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataField: DataField = pmml.DataDictionary.DataField[0];
    const value: Value = new Value({ value: "value", displayValue: "displayValue", property: "valid" });

    dataField.Value?.push(value);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Value value="value" displayValue="displayValue" property="valid"/>`);
  });

  test("Update Value", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4">
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string">
            <Value value="value" displayValue="displayValue" property="valid"/>
          </DataField>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const values: Value[] | undefined = pmml.DataDictionary.DataField[0].Value;
    if (!values) {
      fail("Values should not be undefined");
    }

    const value: Value = values[0];
    expect(value.value).toBe("value");
    expect(value.displayValue).toBe("displayValue");
    expect(value.property).toBe("valid");

    value.value = "value-changed";
    value.displayValue = "displayValue-changed";
    value.property = "invalid";

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Value value="value-changed" displayValue="displayValue-changed" property="invalid"/>`);
  });

  test("Delete Value", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4">
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string">
            <Value value="value" displayValue="displayValue" property="valid"/>
          </DataField>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const values: Value[] | undefined = pmml.DataDictionary.DataField[0].Value;
    if (!values) {
      fail("Values should not be undefined");
    }

    const value: Value = values[0];
    expect(value).not.toBeUndefined();

    values.splice(0, 1);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataField name="field1" optype="categorical" dataType="string"/>`);
  });
});
