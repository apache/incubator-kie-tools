# BPMN Editor Extension Points and Conventions

## Overview

This document comprehensively describes all extension points and conventions used by the BPMN Editor beyond the standard BPMN 2.0 specification. These extensions, implemented through the `drools:` namespace, provide additional capabilities to support executable business processes in the Drools/jBPM runtime environment. The extensions enable process-level configuration, task-specific behaviors, data type specifications, runtime scripting capabilities, and custom metadata.

**Scope**: This documentation covers ALL extension points defined in the BPMN marshaller. Any extension point not documented here should be treated as a bug.

The BPMN Editor extensions are implemented through:

- **Custom XML namespace**: Uses the `drools:` prefix (namespace URI: `http://www.jboss.org/drools`) to distinguish editor-specific attributes and elements from standard BPMN 2.0
- **Seamless integration**: Extensions are integrated with the standard BPMN 2.0 specification, allowing the editor to read and write both standard and extended BPMN elements in the same document
- **Type-safe model**: All extensions are fully typed in TypeScript, providing compile-time validation
- **Backward compatibility**: Supports both current and legacy namespace formats to ensure existing BPMN files continue to work

This design allows the BPMN Editor to work with standard BPMN 2.0 files while also supporting additional capabilities needed for executable business processes in runtime environments like jBPM and Kogito.

---

## Using Extensions in BPMN Files

Extensions use the `drools:` namespace prefix to add custom attributes and elements to standard BPMN 2.0 documents. When you save a BPMN file, these extensions appear alongside standard BPMN elements:

```xml
<bpmn2:process id="MyProcess" drools:packageName="com.example" drools:version="1.0">
  <bpmn2:extensionElements>
    <drools:import name="com.example.MyClass" />
    <drools:global identifier="logger" type="org.slf4j.Logger" />
  </bpmn2:extensionElements>
  <!-- standard BPMN elements -->
</bpmn2:process>
```

The editor can read and write both standard BPMN 2.0 elements and `drools:` extensions, ensuring compatibility with both standard BPMN tools and runtime engines that support these extensions.

**Namespace**: `http://www.jboss.org/drools` (prefix: `drools:`)

---

## Custom Attributes by Element

### Process (tProcess)

Process-level attributes that configure the overall workflow behavior.

| Attribute              | Type    | Description                                                                                                      |
| ---------------------- | ------- | ---------------------------------------------------------------------------------------------------------------- |
| `@_drools:packageName` | string  | The Java package name for the generated process class. Used for organizing processes in the runtime environment. |
| `@_drools:version`     | string  | Version identifier for the process definition. Enables versioning and migration of process instances.            |
| `@_drools:adHoc`       | boolean | Indicates whether the process is ad-hoc, allowing dynamic task execution order rather than strict sequencing.    |

**Example:**

```xml
<bpmn2:process id="HiringProcess" drools:packageName="com.example.hr" drools:version="1.0" drools:adHoc="false">
  <!-- process content -->
</bpmn2:process>
```

### Call Activity (tCallActivity)

Attributes controlling subprocess invocation behavior.

| Attribute                    | Type    | Description                                                                                                                    |
| ---------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `@_drools:independent`       | boolean | When `true`, the called process runs independently with its own lifecycle. When `false`, it shares the parent process context. |
| `@_drools:waitForCompletion` | boolean | Determines whether the calling process waits for the subprocess to complete before continuing.                                 |

### Service Task (tServiceTask)

Attributes defining service task implementation details.

| Attribute                        | Type   | Description                                                                                   |
| -------------------------------- | ------ | --------------------------------------------------------------------------------------------- |
| `@_drools:serviceimplementation` | string | Specifies the implementation type: `"Java"` or `"WebService"`                                 |
| `@_drools:serviceinterface`      | string | The fully qualified interface name for Java implementations or WSDL location for web services |
| `@_drools:serviceoperation`      | string | The specific method or operation name to invoke on the service interface                      |

**Example:**

```xml
<bpmn2:serviceTask
  id="EmailService"
  drools:serviceimplementation="Java"
  drools:serviceinterface="com.example.EmailService"
  drools:serviceoperation="sendEmail"
>
  <!-- task content -->
</bpmn2:serviceTask>
```

### Business Rule Task (tBusinessRuleTask)

Attributes for business rule task configuration.

| Attribute                | Type   | Description                                                                                       |
| ------------------------ | ------ | ------------------------------------------------------------------------------------------------- |
| `@_drools:ruleFlowGroup` | string | The rule flow group name that identifies which rules should be activated when this task executes. |

### Task (tTask)

Generic task attributes for custom task types.

| Attribute           | Type   | Description                                                                                                      |
| ------------------- | ------ | ---------------------------------------------------------------------------------------------------------------- |
| `@_drools:taskName` | string | Custom task type identifier. Used to specify specialized task behaviors (e.g., "Milestone" for milestone tasks). |

### Data Input (tDataInput)

Data input parameter type specification.

| Attribute        | Type   | Description                                                                                       |
| ---------------- | ------ | ------------------------------------------------------------------------------------------------- |
| `@_drools:dtype` | string | The Java data type for the input parameter (e.g., "String", "Integer", "com.example.CustomType"). |

### Data Output (tDataOutput)

Data output parameter type specification.

| Attribute        | Type   | Description                                                                                        |
| ---------------- | ------ | -------------------------------------------------------------------------------------------------- |
| `@_drools:dtype` | string | The Java data type for the output parameter (e.g., "String", "Integer", "com.example.CustomType"). |

### Sequence Flow (tSequenceFlow)

Sequence flow priority configuration.

| Attribute           | Type   | Description                                                                                                                        |
| ------------------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| `@_drools:priority` | string | Numeric priority value for sequence flow evaluation. Higher priority flows are evaluated first when multiple outgoing flows exist. |

### Message Event Definition (tMessageEventDefinition)

Message event reference configuration.

| Attribute         | Type   | Description                                                                 |
| ----------------- | ------ | --------------------------------------------------------------------------- |
| `@_drools:msgref` | string | Reference to the message structure or identifier used by the message event. |

### Escalation Event Definition (tEscalationEventDefinition)

Escalation event code specification.

| Attribute          | Type   | Description                                                                        |
| ------------------ | ------ | ---------------------------------------------------------------------------------- |
| `@_drools:esccode` | string | The escalation code that identifies the type of escalation being thrown or caught. |

### Error Event Definition (tErrorEventDefinition)

Error event reference configuration.

| Attribute           | Type   | Description                                                                     |
| ------------------- | ------ | ------------------------------------------------------------------------------- |
| `@_drools:erefname` | string | The error reference name that identifies the error type being thrown or caught. |

---

## Extension Elements

Extension elements appear within `<extensionElements>` blocks and provide additional process metadata and scripting capabilities.

### drools:metaData

Provides key-value metadata pairs for process elements. Used for custom properties, documentation, and runtime configuration.

**Supported Elements**: All executable BPMN elements (see [Elements Supporting Metadata](#elements-supporting-metadata))

**Example:**

```xml
<bpmn2:task id="Task1">
  <bpmn2:extensionElements>
    <drools:metaData name="customProperty">
      <drools:metaValue>customValue</drools:metaValue>
    </drools:metaData>
  </bpmn2:extensionElements>
</bpmn2:task>
```

### drools:import

Declares Java class imports available to the process. Similar to Java import statements, these make classes available for use in scripts, expressions, and data type declarations.

**Supported Elements**: Process only

**Example:**

```xml
<bpmn2:process id="MyProcess">
  <bpmn2:extensionElements>
    <drools:import name="com.example.MyClass" />
    <drools:import name="java.util.Date" />
  </bpmn2:extensionElements>
</bpmn2:process>
```

### drools:global

Declares global variables accessible throughout the process execution. Globals are shared across all process instances.

**Supported Elements**: Process only

**Example:**

```xml
<bpmn2:process id="MyProcess">
  <bpmn2:extensionElements>
    <drools:global identifier="logger" type="org.slf4j.Logger" />
  </bpmn2:extensionElements>
</bpmn2:process>
```

### drools:onEntry-script

Script executed when entering an element (before the element's main action). Used for initialization, logging, variable manipulation, or pre-conditions.

**Supported Elements**: All executable BPMN elements (see [Elements Supporting Entry/Exit Scripts](#elements-supporting-entryexit-scripts))

**Example:**

```xml
<bpmn2:task id="Task1">
  <bpmn2:extensionElements>
    <drools:onEntry-script scriptFormat="http://www.java.com/java">
      <drools:script>System.out.println("Entering task");</drools:script>
    </drools:onEntry-script>
  </bpmn2:extensionElements>
</bpmn2:task>
```

### drools:onExit-script

Script executed when exiting an element (after the element's main action completes). Used for cleanup, logging, variable updates, or post-conditions.

**Supported Elements**: All executable BPMN elements (see [Elements Supporting Entry/Exit Scripts](#elements-supporting-entryexit-scripts))

**Example:**

```xml
<bpmn2:task id="Task1">
  <bpmn2:extensionElements>
    <drools:onExit-script scriptFormat="http://www.java.com/java">
      <drools:script>System.out.println("Exiting task");</drools:script>
    </drools:onExit-script>
  </bpmn2:extensionElements>
</bpmn2:task>
```

---

## Reserved Names & Constants

### Business Rule Task Implementation Types

Valid values for `@_drools:serviceimplementation` on Business Rule Tasks:

| Value                                | Description                                       |
| ------------------------------------ | ------------------------------------------------- |
| `"http://www.jboss.org/drools/rule"` | Drools rule engine implementation using DRL rules |
| `"http://www.jboss.org/drools/dmn"`  | DMN (Decision Model and Notation) implementation  |

### Service Task Implementation Types

Valid values for `@_drools:serviceimplementation` on Service Tasks:

| Value          | Description                      |
| -------------- | -------------------------------- |
| `"Java"`       | Java class method invocation     |
| `"WebService"` | SOAP/REST web service invocation |

### Reserved Data Input Names for DMN Business Rule Tasks

When binding a Business Rule Task to a DMN decision, use these specific data input names:

| Input Name    | Purpose                   |
| ------------- | ------------------------- |
| `"fileName"`  | Path to the DMN file      |
| `"namespace"` | DMN model namespace URI   |
| `"model"`     | DMN model name identifier |

### Reserved Data Input Names for User Tasks

These names are reserved for standard user task properties. Do not use them for custom data inputs:

| Input Name                | Purpose                             |
| ------------------------- | ----------------------------------- |
| `"TaskName"`              | Display name for the user task      |
| `"Skippable"`             | Whether the task can be skipped     |
| `"GroupId"`               | Group(s) assigned to the task       |
| `"Comment"`               | Task comment/description            |
| `"Description"`           | Detailed task description           |
| `"Priority"`              | Task priority level                 |
| `"CreatedBy"`             | User who created the task           |
| `"Content"`               | Task content data                   |
| `"NotStartedReassign"`    | Reassignment rules if not started   |
| `"NotCompletedReassign"`  | Reassignment rules if not completed |
| `"NotStartedNotify"`      | Notification rules if not started   |
| `"NotCompletedNotify"`    | Notification rules if not completed |
| `"multiInstanceItemType"` | Data type for multi-instance items  |

### Reserved Data Input Names for Milestone Tasks

Required data inputs for milestone task configuration:

| Input Name    | Purpose                                                   |
| ------------- | --------------------------------------------------------- |
| `"Condition"` | Condition expression for milestone achievement            |
| `"TaskName"`  | Name of the milestone task (use `"Milestone"` as default) |

---

## Non-Executable Elements

The following BPMN elements do **not** support Drools extensions because they are non-executable and don't participate in process execution:

| Element                | Reason                                                          |
| ---------------------- | --------------------------------------------------------------- |
| `tManualTask`          | Requires human intervention outside the system                  |
| `tReceiveTask`         | Passive message reception without system action                 |
| `tSendTask`            | Message sending without complex logic                           |
| `tCallChoreography`    | Choreography elements are not supported in executable processes |
| `tChoreographyTask`    | Choreography elements are not supported in executable processes |
| `tSubChoreography`     | Choreography elements are not supported in executable processes |
| `tDataObjectReference` | Reference element without runtime behavior                      |
| `tDataStoreReference`  | Reference element without runtime behavior                      |
| `tImplicitThrowEvent`  | Implicit events are handled by the engine automatically         |

---

## Elements Supporting Extensions

### Elements Supporting Metadata

The following elements support `drools:metaData` extension elements:

**Process Elements**:

- `tProcess` (process-level metadata only, no entry/exit scripts)
- `tProperty` (metadata only)
- `tLane` (metadata only)

**Executable Elements** (with entry/exit scripts):

- `tAdHocSubProcess`
- `tBoundaryEvent`
- `tBusinessRuleTask`
- `tCallActivity`
- `tComplexGateway`
- `tDataObject`
- `tEndEvent`
- `tEvent`
- `tEventBasedGateway`
- `tExclusiveGateway`
- `tInclusiveGateway`
- `tIntermediateCatchEvent`
- `tIntermediateThrowEvent`
- `tParallelGateway`
- `tScriptTask`
- `tSequenceFlow`
- `tServiceTask`
- `tStartEvent`
- `tSubProcess`
- `tTask`
- `tTransaction`
- `tUserTask`
- `tAssociation`
- `tGroup`
- `tTextAnnotation`

### Elements Supporting Entry/Exit Scripts

All elements listed in the "Executable Elements" section above support both:

- `drools:onEntry-script`
- `drools:onExit-script`

---

## Usage Guidelines

### Naming Conventions

- **Attributes**: Use the pattern `@_drools:attributeName` (e.g., `@_drools:packageName`)
- **Elements**: Use the pattern `drools:elementName` (e.g., `drools:metaData`)

### Extension Element Placement

Extension elements must be placed within `<extensionElements>` blocks:

```xml
<bpmn2:process id="Process_1" drools:packageName="com.example" drools:version="1.0">
  <bpmn2:extensionElements>
    <drools:import name="com.example.MyClass" />
    <drools:global identifier="logger" type="org.slf4j.Logger" />
  </bpmn2:extensionElements>
  <!-- process content -->
</bpmn2:process>
```

### Data Type Specifications

The `@_drools:dtype` attribute accepts:

- **Primitive types**: `String`, `Integer`, `Boolean`, `Float`, `Object`
- **Fully qualified class names**: `com.example.CustomType`
- **Collection types**: `java.util.List`, `java.util.Map`

**Example:**

```xml
<bpmn2:dataInput id="input1" name="customer" drools:dtype="com.example.Customer" />
```

### Backward Compatibility

The editor supports both current (`drools:`) and legacy (`http://www.jboss.org/drools`) namespace formats to ensure existing BPMN files continue to work.
