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
export const LINEAR_REGRESSION_MODEL_1: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header copyright="DMG.org"/>
  <DataDictionary numberOfFields="4">
    <DataField name="age" optype="continuous" dataType="double"/>
    <DataField name="salary" optype="continuous" dataType="double"/>
    <DataField name="car_location" optype="categorical" dataType="string">
      <Value value="carpark"/>
      <Value value="street"/>
      <Value value="garage"/>
    </DataField>
    <DataField name="number_of_claims" optype="continuous" dataType="integer"/>
  </DataDictionary>
  <RegressionModel modelName="Sample for linear regression" functionName="regression" algorithmName="linearRegression" targetFieldName="number_of_claims">
    <MiningSchema>
      <MiningField name="age"/>
      <MiningField name="salary"/>
      <MiningField name="car_location"/>
      <MiningField name="number_of_claims" usageType="target"/>
    </MiningSchema>
    <RegressionTable intercept="132.37">
      <NumericPredictor name="age" exponent="1" coefficient="7.1"/>
      <CategoricalPredictor name="car_location" value="carpark" coefficient="41.1"/>
      <CategoricalPredictor name="car_location" value="street" coefficient="325.03"/>
      <CategoricalPredictor name="car_location" value="garage" coefficient="-500.0"/>
    </RegressionTable>
  </RegressionModel>
</PMML>
`;

export const LINEAR_REGRESSION_MODEL_2: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header copyright="DMG.org"/>
  <DataDictionary numberOfFields="3">
    <DataField name="water_temperature" optype="continuous" dataType="double"/>
    <DataField name="hemisphere" optype="categorical" dataType="string">
      <Value value="northern"/>
      <Value value="southern"/>
    </DataField>
    <DataField name="height_of_tide" optype="continuous" dataType="double"/>
  </DataDictionary>
  <RegressionModel modelName="Tide height" functionName="regression" algorithmName="linearRegression" targetFieldName="height_of_tide">
    <MiningSchema>
      <MiningField name="water_temperature"/>
      <MiningField name="height_of_tide" usageType="target"/>
    </MiningSchema>
    <RegressionTable intercept="1.2">
      <NumericPredictor name="water_temperature" exponent="1" coefficient="6"/>
      <CategoricalPredictor name="hemisphere" value="northern" coefficient="-2.0"/>
    </RegressionTable>
  </RegressionModel>
</PMML>
`;

export const LINEAR_REGRESSION_MODEL_3: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" xmlns:cheese="http://cheese.org" version="4.4"> 
  <Header copyright="DMG.org">
    <Application name="application" version="1.0"/>
    <Annotation>annotation1</Annotation>
    <Annotation>annotation2</Annotation>
    <Timestamp>timestamp</Timestamp>
  </Header>
  <DataDictionary numberOfFields="2">
    <DataField name="age" optype="continuous" dataType="double">
      <Interval closure="closedClosed" leftMargin="0" rightMargin="100"/>
      <Value value="bananna" displayValue="yellow fruit" property="valid"/>
    </DataField>
    <DataField name="weight" optype="continuous" dataType="double">
      <Interval closure="closedClosed" leftMargin="0" rightMargin="200"/>
    </DataField>
  </DataDictionary>
  <RegressionModel modelName="You get fatter as you get older" functionName="regression" algorithmName="linearRegression" targetFieldName="height_of_tide">
    <MiningSchema cheese:withExtraFlavour="edam">
      <MiningField name="age"/>
      <MiningField name="weight" usageType="target"/>
    </MiningSchema>
    <RegressionTable intercept="2">
      <NumericPredictor name="age" exponent="1" coefficient="1.6"/>
    </RegressionTable>
  </RegressionModel>
</PMML>
`;
