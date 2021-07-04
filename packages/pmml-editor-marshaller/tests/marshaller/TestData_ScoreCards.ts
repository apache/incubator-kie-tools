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
export const SCORE_CARD_SIMPLE_PREDICATE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="3">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="continuous" dataType="double"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="SimpleScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="5" baselineScore="6" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="input1Score" baselineScore="4" reasonCode="Input1ReasonCode">
        <Attribute partialScore="-12">
          <SimplePredicate field="input1" operator="lessOrEqual" value="10"/>
        </Attribute>
        <Attribute partialScore="50">
          <SimplePredicate field="input1" operator="greaterThan" value="10"/>
        </Attribute>
      </Characteristic>
      <Characteristic name="input2Score" baselineScore="8" reasonCode="Input2ReasonCode">
        <Attribute partialScore="-8">
          <SimplePredicate field="input2" operator="lessOrEqual" value="-5"/>
        </Attribute>
        <Attribute partialScore="32">
          <SimplePredicate field="input2" operator="greaterThan" value="-5"/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_SIMPLE_PREDICATE_SINGLE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="3">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="continuous" dataType="double"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="SimpleScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="5" baselineScore="6" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="input1Score" baselineScore="4" reasonCode="Input1ReasonCode">
        <Attribute partialScore="-12">
          <SimplePredicate field="input1" operator="lessOrEqual" value="10"/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_COMPOUND_PREDICATE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="5">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="continuous" dataType="double"/>
    <DataField name="input3" optype="categorical" dataType="string"/>
    <DataField name="input4" optype="categorical" dataType="string"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="CompoundPredicateScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsAbove" initialScore="-15" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input3" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input4" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 3" rank="3" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="characteristic1Score" baselineScore="-5.5" reasonCode="characteristic1ReasonCode">
        <Attribute partialScore="-10">
          <CompoundPredicate booleanOperator="and">
            <SimplePredicate field="input1" operator="lessOrEqual" value="-5"/>
            <SimplePredicate field="input2" operator="lessOrEqual" value="-5"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="15">
          <CompoundPredicate booleanOperator="and">
            <SimplePredicate field="input1" operator="greaterThan" value="-5"/>
            <SimplePredicate field="input2" operator="greaterThan" value="-5"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="25">
          <True/>
        </Attribute>
      </Characteristic>
      <Characteristic name="characteristic2Score" baselineScore="11" reasonCode="characteristic2ReasonCode">
        <Attribute partialScore="-18">
          <CompoundPredicate booleanOperator="or">
            <SimplePredicate field="input3" operator="equal" value="classA"/>
            <SimplePredicate field="input4" operator="equal" value="classA"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="10">
          <CompoundPredicate booleanOperator="or">
            <SimplePredicate field="input3" operator="equal" value="classB"/>
            <SimplePredicate field="input4" operator="equal" value="classB"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="100.5">
          <False/>
        </Attribute>
        <Attribute partialScore="105.5">
          <True/>
        </Attribute>
      </Characteristic>
      <Characteristic name="characteristic3Score" baselineScore="25" reasonCode="characteristic3ReasonCode">
        <Attribute partialScore="-50">
          <CompoundPredicate booleanOperator="xor">
            <SimplePredicate field="input3" operator="equal" value="classA"/>
            <SimplePredicate field="input4" operator="equal" value="classA"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="150">
          <True/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_NESTED_COMPOUND_PREDICATE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="3">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="categorical" dataType="string"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="CompoundNestedPredicateScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="-15" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="characteristic1Score" baselineScore="21.8" reasonCode="characteristic1ReasonCode">
        <Attribute partialScore="-10.5">
          <CompoundPredicate booleanOperator="and">
            <CompoundPredicate booleanOperator="and">
              <True/>
              <SimplePredicate field="input1" operator="greaterThan" value="-15"/>
              <SimplePredicate field="input1" operator="lessOrEqual" value="25.4"/>
            </CompoundPredicate>
            <SimplePredicate field="input2" operator="notEqual" value="classA"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="25">
          <True/>
        </Attribute>
      </Characteristic>
      <Characteristic name="characteristic2Score" baselineScore="11" reasonCode="characteristic2ReasonCode">
        <Attribute partialScore="-18">
          <CompoundPredicate booleanOperator="or">
            <SimplePredicate field="input1" operator="lessOrEqual" value="-20"/>
            <SimplePredicate field="input2" operator="equal" value="classA"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="10">
          <CompoundPredicate booleanOperator="or">
            <CompoundPredicate booleanOperator="and">
              <CompoundPredicate booleanOperator="and">
                <SimplePredicate field="input1" operator="greaterOrEqual" value="5"/>
                <SimplePredicate field="input1" operator="lessThan" value="12"/>
              </CompoundPredicate>
              <SimplePredicate field="input2" operator="equal" value="classB"/>
            </CompoundPredicate>
            <SimplePredicate field="input2" operator="equal" value="classC"/>
          </CompoundPredicate>
        </Attribute>
        <Attribute partialScore="100.5">
          <True/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_BASIC_COMPLEX_PARTIAL_SCORE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="3">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="continuous" dataType="double"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="BasicComplexPartialScore" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="10" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="characteristic1Score" baselineScore="20" reasonCode="characteristic1ReasonCode">
        <Attribute>
          <SimplePredicate field="input1" operator="greaterThan" value="-1000"/>
          <ComplexPartialScore>
            <Apply function="+">
              <FieldRef field="input1"/>
              <FieldRef field="input2"/>
            </Apply>
          </ComplexPartialScore>
        </Attribute>
        <Attribute partialScore="25">
          <True/>
        </Attribute>
      </Characteristic>
      <Characteristic name="characteristic2Score" baselineScore="5" reasonCode="characteristic2ReasonCode">
        <Attribute>
          <SimplePredicate field="input2" operator="lessOrEqual" value="1000"/>
          <ComplexPartialScore>
            <Apply function="*">
              <FieldRef field="input1"/>
              <FieldRef field="input2"/>
            </Apply>
          </ComplexPartialScore>
        </Attribute>
        <Attribute partialScore="-50">
          <True/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_NESTED_COMPLEX_PARTIAL_SCORE: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <DataDictionary numberOfFields="3">
    <DataField name="input1" optype="continuous" dataType="double"/>
    <DataField name="input2" optype="continuous" dataType="double"/>
    <DataField name="score" optype="continuous" dataType="double"/>
  </DataDictionary>
  <Scorecard modelName="NestedComplexPartialScoreScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="10" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="input2" usageType="active" invalidValueTreatment="asMissing"/>
      <MiningField name="score" usageType="target"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
      <OutputField name="Reason Code 1" rank="1" feature="reasonCode" dataType="string" optype="categorical"/>
      <OutputField name="Reason Code 2" rank="2" feature="reasonCode" dataType="string" optype="categorical"/>
    </Output>
    <Characteristics>
      <Characteristic name="characteristic1Score" baselineScore="20" reasonCode="characteristic1ReasonCode">
        <Attribute>
          <SimplePredicate field="input1" operator="greaterThan" value="-1000"/>
          <ComplexPartialScore>
            <Apply function="-">
              <Apply function="+">
                <FieldRef field="input1"/>
                <FieldRef field="input2"/>
              </Apply>
              <Constant>5</Constant>
            </Apply>
          </ComplexPartialScore>
        </Attribute>
        <Attribute partialScore="25">
          <True/>
        </Attribute>
      </Characteristic>
      <Characteristic name="characteristic2Score" baselineScore="5" reasonCode="characteristic2ReasonCode">
        <Attribute>
          <SimplePredicate field="input2" operator="lessOrEqual" value="1000"/>
          <ComplexPartialScore>
            <Apply function="*">
              <Constant>2</Constant>
              <Apply function="*">
                <FieldRef field="input1"/>
                <Apply function="/">
                  <FieldRef field="input2"/>
                  <Constant>2</Constant>
                </Apply>
              </Apply>
            </Apply>
          </ComplexPartialScore>
        </Attribute>
        <Attribute partialScore="-50">
          <True/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;

export const SCORE_CARD_PROTOTYPES: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header/>
  <Scorecard modelName="SimpleScorecard" functionName="regression" useReasonCodes="true" reasonCodeAlgorithm="pointsBelow" initialScore="5" baselineScore="6" baselineMethod="other">
    <MiningSchema>
      <MiningField name="input1" usageType="active" invalidValueTreatment="asMissing"/>
    </MiningSchema>
    <Output>
      <OutputField name="Score" feature="predictedValue" dataType="double" optype="continuous"/>
    </Output>
    <Characteristics>
      <Characteristic name="input1Score" baselineScore="4" reasonCode="Input1ReasonCode">
        <Attribute partialScore="-12">
          <SimplePredicate field="input1" operator="lessOrEqual" value="10"/>
        </Attribute>
      </Characteristic>
    </Characteristics>
  </Scorecard>
</PMML>
`;
