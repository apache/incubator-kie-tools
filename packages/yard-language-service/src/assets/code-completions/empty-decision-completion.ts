export const emptyDecisionCompletion = `specVersion: alpha
kind: YaRD
name: 'Decision Name'
expressionLang: alpha
inputs:
 - name: 'FirstInputName'
   type: 'http://myapi.org/jsonSchema.json#FirstInputName'
 - name: 'SecondInputName'
   type: 'http://myapi.org/jsonSchema.json#SecondInputName'
elements:
 - name: 'FirstDecision'
   type: Decision
   requirements: ['SecondInputName']
   logic:
     type: DecisionTable
     inputs: ['SecondInputName.someField']
     outputComponents: ['OutputName']
     rules:
      - ['="someFieldValue"', '-', 1, 1]`;
