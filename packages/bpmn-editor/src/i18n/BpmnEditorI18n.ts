/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * License); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing;
 * software distributed under the License is distributed on an
 * AS IS BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface BpmnEditorDictionary
  extends ReferenceDictionary<{
    customTasks: {
      emptyStateBody: string;
    };
    nodeLabel: {
      placeHolder: string;
    };
    bpmnDiagramEmptyState: {
      emptyBpmnTitle: string;
      emptyBpmnBody: string;
      startByDraggingNodes: string;
    };
    bpmnTopRightPanels: {
      overlays: string;
      propertiesPanel: string;
    };
    bpmnPalette: {
      processVariables: string;
      addVariable: string;
      noVariables: string;
      noVariablesYet: string;
      emptyBpmnBody: string;
      variables: string;
      correlations: string;
      startEvents: string;
      intermediateCatchEvents: string;
      intermediateThrowEvents: string;
      endEvents: string;
      tasks: string;
      callActivity: string;
      subProcesses: string;
      gateways: string;
      lanes: string;
      dataObject: string;
      group: string;
      textAnnotation: string;
      customTasks: string;
      propertiesManagement: string;
    };
    versionUpgraded: string;
    bpmnLatestVersion: string;
    importedBpmn: (propsVersion: string, bpmnLatestVersion: string) => string;
    newInBpmn: (bpmnLatestVersion: string) => string;
    enableStyles: string;
    unknown: string;
    undefined: string;
    unnamed: string;
    correlation: {
      properties: string;
      messageBindings: string;
      expressionPlaceHolder: string;
      keys: string;
      addProperty: string;
      propertiesIncluded: string;
      subscriptions: string;
      valueOrExpressionPlaceHolder: string;
      addSubscriptions: (selectedKey: string) => string;
      correlationEmptyState: (selectedKey: string) => string;
      noCorrelationKeys: string;
      noCorrelationKeysYet: string;
      correlationsKeyEmptyBody: string;
      addCorrelationKey: string;
      noCorrelations: string;
      noCorrelationsYet: string;
      correlationEmptyBody: string;
      addCorrelationProperty: string;
    };
    dataMapping: {
      title: string;
      manage: string;
      mappingForElement: (elementName: string) => string;
      inputs: string;
      outputs: string;
      save: string;
      input: string;
      output: string;
      name: string;
      dataType: string;
      namePlaceholder: string;
      noWhiteSpaces: string;
      noDataMappings: (entryTitle: string) => string;
      noDataMappingsYet: (entryTitle: string) => string;
      addDataMapping: (entryTitle: string) => string;
    };
    eventDefinitionProperties: {
      message: string;
      signal: string;
      errorCode: string;
      escalationCode: string;
    };
    propertiesPanel: {
      endEvent: string;
      activity: string;
      adhocAutoStart: string;
      async: string;
      calledElement: string;
      codePlaceholder: string;
      conditionalExpression: string;
      defaultRoute: string;
      none: string;
      noneYet: string;
      createError: string;
      createEscalation: string;
      name: string;
      classNamePlaceholder: string;
      createDataType: string;
      link: string;
      createMessage: string;
      value: string;
      namePlaceholder: string;
      valuePlaceholder: string;
      executionMode: string;
      sequential: string;
      completionCondition: string;
      collectionInput: string;
      dataInput: string;
      dataInputPlaceholder: string;
      dataType: string;
      collectionOutput: string;
      dataOutput: string;
      dataOutputPlaceholder: string;
      multiInstance: string;
      enterNamePlaceholder: string;
      id: string;
      documentation: string;
      documentationPlaceholder: string;
      copy: string;
      copied: string;
      manage: string;
      notificationsProperties: {
        notifications: string;
        expiresAt: string;
        from: string;
        toUser: string;
        toGroup: string;
        toEmail: string;
        replyTo: string;
        subject: string;
        body: string;
        expiresPlaceholder: string;
        fromPlaceholder: string;
        toUserPlaceholder: string;
        toGroupPlaceholder: string;
        toEmailPlaceholder: string;
        subjectPlaceholder: string;
        replyPlaceholder: string;
        bodyPlaceholder: string;
        noNotifications: string;
        addNotifications: string;
        emptyNotificationMessage: string;
      };
      save: string;
      onEntry: string;
      onExit: string;
      reassignments: string;
      users: string;
      groups: string;
      type: string;
      period: string;
      usersPlaceholder: string;
      groupsPlaceholder: string;
      periodPlaceholder: string;
      addReassignment: string;
      emptyReassignmentMessage: string;
      noReassignMents: string;
      signalScope: string;
      createSignal: string;
      priority: string;
      priorityPlaceholder: string;
      slaDuedate: string;
      datePlaceholder: string;
      timerOptions: string;
      fireOnceAfterDuration: string;
      durationPlaceholder: string;
      fireMultipleTimes: string;
      timePlaceholder: string;
      iso: string;
      cron: string;
      fireAtspecificDate: string;
      fireAtspecificDatePlaceholder: string;
      tags: string;
      variables: string;
      addCustomtag: string;
      addCustomtagPlaceholder: string;
      enteringExpressions: string;
      noVariablesYet: string;
      expr: string;
      var: string;
      expression: string;
      process: string;
      close: string;
      adhoc: string;
      imports: string;
      metadata: string;
      idNamespace: string;
      regenerateIdNamespace: string;
      idPlaceholder: string;
      namespace: string;
      namespacePlaceholder: string;
      misc: string;
      expressionLanguage: string;
      expressionLanguagePlaceholder: string;
      private: string;
      public: string;
      executable: string;
      packageName: string;
      packageNamePlaceholder: string;
      version: string;
      versionPlaceholder: string;
      processInstanceDescription: string;
      processInstanceDescriptionPlaceholder: string;
      regenerateId: string;
      regenerateMessage: string;
      continueMessage: string;
      cancel: string;
      edgesNodesSelected: (size: number) => string;
      cantEditProperties: string;
      selectOnlyNodes: string;
      selectOnlyEdges: string;
      multipleEdgesSelected: (size: number) => string;
      multipleNodesSelected: (size: number) => string;
      sequenceFlow: string;
      association: string;
      unsupported: string;
      noPropertiesToEdit: string;
      noMatches: string;
      undefined: string;
      empty: string;
      unexpectedError: string;
      bugReport: string;
      undoAction: string;
      fileAnIssue: string;
    };
    singleNodeProperties: {
      close: string;
      adhocSubprocess: string;
      adhocOrdering: string;
      parallel: string;
      adhocActivationCondition: string;
      adhocCompletionCondition: string;
      boundaryEvent: string;
      cancelActivity: string;
      businessRuleTask: string;
      implementation: string;
      chooseRules: string;
      drl: string;
      autofill: string;
      drlRuleFlowGroup: string;
      drlRuleFlowPlaceholder: string;
      dmnModelRelativePath: string;
      dmnModelRelativePathPlaceholder: string;
      dmnModelNamespace: string;
      dmnModelNamespacePlaceholder: string;
      dmnModelName: string;
      dmnModelNamePlaceholder: string;
      callActivity: string;
      independent: string;
      abortParent: string;
      waitForCompletion: string;
      complexGateway: string;
      eventBasedGateway: string;
      eventSubprocess: string;
      exclusiveGateway: string;
      inclusiveGateway: string;
      intermediateCatchEvent: string;
      intermediateThrowEvent: string;
      lane: string;
      parallelGateway: string;
      scriptTask: string;
      script: string;
      serviceTask: string;
      java: string;
      webservice: string;
      interface: string;
      operation: string;
      startEvent: string;
      interrupting: string;
      interfacePlacehlder: string;
      operationPlaceholder: string;
      subprocess: string;
      task: string;
      textAnnotation: string;
      format: string;
      formatPlaceholder: string;
      text: string;
      textPlaceholder: string;
      transaction: string;
      userTask: string;
      taskName: string;
      taskNamePlaceholder: string;
      subjectPlaceholder: string;
      content: string;
      contentPlaceholder: string;
      taskPriority: string;
      taskPriorityPlaceholder: string;
      description: string;
      descriptionPlaceholder: string;
      skippable: string;
      actors: string;
      actorsPlaceholder: string;
      groups: string;
      groupPlaceholder: string;
      createdBy: string;
      createdByPlaceholder: string;
    };
    propertiesManager: {
      dataTypeplaceholder: string;
      dataType: string;
      dataTypes: string;
      noDataTypeProperties: string;
      noDataTypePropertiesYet: string;
      noProperty: string;
      addDataType: string;
      messages: string;
      name: string;
      noMessageproperties: string;
      noMessagepropertiesYet: string;
      anyMessage: string;
      addMessage: string;
      signals: string;
      noSignalproperties: string;
      noSignalpropertiesYet: string;
      anySignal: string;
      addSignal: string;
      escalations: string;
      noEscalationproperties: string;
      noEscalationpropertiesYet: string;
      anyEscalation: string;
      addEscalation: string;
      errors: string;
      noErrorproperties: string;
      noErrorpropertiesYet: string;
      anyError: string;
      addError: string;
    };
    overlaysPanel: {
      snapping: string;
      horizontal: string;
      vertical: string;
    };
  }> {}

export interface BpmnEditorI18n extends BpmnEditorDictionary, CommonI18n {}
