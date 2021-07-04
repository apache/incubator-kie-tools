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
import { DataDictionary, DataField, Interval, PMML, PMML2XML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";

describe("Interval tests", () => {
  test("Empty collection on DataField", () => {
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
    expect(dataDictionary.DataField[0].Interval).not.toBeUndefined();
    expect(dataDictionary.DataField[0].Interval?.length).toBe(0);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataField name="field1" optype="categorical" dataType="string"/>`);
  });

  test("Add Interval", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataField: DataField = pmml.DataDictionary.DataField[0];
    const interval: Interval = new Interval({ closure: "openOpen", leftMargin: 0, rightMargin: 100 });

    dataField.Interval?.push(interval);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Interval closure="openOpen" leftMargin="0" rightMargin="100"/>`);
  });

  test("Add Interval::No leftMargin", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataField: DataField = pmml.DataDictionary.DataField[0];
    const interval: Interval = new Interval({ closure: "openOpen", rightMargin: 100 });

    dataField.Interval?.push(interval);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Interval closure="openOpen" rightMargin="100"/>`);
  });

  test("Add Interval::No rightMargin", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string"/>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const dataField: DataField = pmml.DataDictionary.DataField[0];
    const interval: Interval = new Interval({ closure: "openOpen", leftMargin: 10 });

    dataField.Interval?.push(interval);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Interval closure="openOpen" leftMargin="10"/>`);
  });

  test("Update Interval", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4">
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string">
            <Interval closure="openOpen" leftMargin="10" rightMargin="100"/>
          </DataField>
        </DataDictionary>
      </PMML>
    `);

    expect(pmml).not.toBeNull();

    const intervals: Interval[] | undefined = pmml.DataDictionary.DataField[0].Interval;
    if (!intervals) {
      fail("Intervals should not be undefined");
    }

    const interval: Interval = intervals[0];
    expect(interval.closure).toBe("openOpen");
    expect(interval.leftMargin).toBe(10);
    expect(interval.rightMargin).toBe(100);

    interval.closure = "closedClosed";
    interval.leftMargin = 25;
    interval.rightMargin = 500;

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<Interval closure="closedClosed" leftMargin="25" rightMargin="500"/>`);
  });

  test("Delete Interval", () => {
    const pmml: PMML = XML2PMML(`
      <PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4">
        <DataDictionary>
          <DataField name="field1" optype="categorical" dataType="string">
            <Interval closure="closedClosed" leftMargin="25" rightMargin="500"/>
          </DataField>
        </DataDictionary>
      </PMML> 
    `);

    expect(pmml).not.toBeNull();

    const intervals: Interval[] | undefined = pmml.DataDictionary.DataField[0].Interval;
    if (!intervals) {
      fail("Intervals should not be undefined");
    }

    const interval: Interval = intervals[0];
    expect(interval).not.toBeUndefined();

    intervals.splice(0, 1);

    const xml: string = PMML2XML(pmml);

    expect(xml).toContain(`<DataField name="field1" optype="categorical" dataType="string"/>`);
  });
});
