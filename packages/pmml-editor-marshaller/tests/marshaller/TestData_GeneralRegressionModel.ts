export const GENERAL_REGRESSION_MODEL_1: string = `
<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
  <Header copyright="DMG.org"/>
  <DataDictionary numberOfFields="1">
    <DataField name="field1" optype="continuous" dataType="double"/>
  </DataDictionary>
  <GeneralRegressionModel modelName="name">
    <MiningSchema>
      <MiningField name="field1"/>
    </MiningSchema>
  </GeneralRegressionModel>
</PMML>
`;
