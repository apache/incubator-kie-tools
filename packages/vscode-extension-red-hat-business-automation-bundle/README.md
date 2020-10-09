# 0.7.0 (alpha)

## New features

VS Code
*   [[KOGITO-1508](https://issues.redhat.com/browse/KOGITO-1508)] - Implement selenium test for kogito-bpmn in community
*   [[KOGITO-1517](https://issues.redhat.com/browse/KOGITO-1517)] - Improve accessibility on file with unsupported extension error
*   [[KOGITO-2210](https://issues.redhat.com/browse/KOGITO-2210)] - Define Integration API for Java Backend Services
*   [[KOGITO-2863](https://issues.redhat.com/browse/KOGITO-2863)] - Think about removing a default Envelope from `embedded-editor`
*   [[KOGITO-2887](https://issues.redhat.com/browse/KOGITO-2887)] - Implement a sample service
*   [[KOGITO-3042](https://issues.redhat.com/browse/KOGITO-3042)] - Replace MessageBusClient with proxified version of ApiToConsume
*   [[KOGITO-3043](https://issues.redhat.com/browse/KOGITO-3043)] - Provide locale information in a way that GWT can read
*   [[KOGITO-3056](https://issues.redhat.com/browse/KOGITO-3056)] - Move I18nService and and its Envelope/Channel APIs to its own module
*   [[KOGITO-3057](https://issues.redhat.com/browse/KOGITO-3057)] - Add a initialLocale prop on I18nDictionariesProvider
*   [[KOGITO-3096](https://issues.redhat.com/browse/KOGITO-3096)] - CI for kogito-tooling-java
*   [[KOGITO-3100](https://issues.redhat.com/browse/KOGITO-3100)] - Documentation for backend services
*   [[KOGITO-2984](https://issues.redhat.com/browse/KOGITO-2984)] - Use i18n dictionaries on VS Code extension backend
*   [[KOGITO-3205](https://issues.redhat.com/browse/KOGITO-3205)] - Create test runner service running on the backend infra

Editors
*   [[KOGITO-543](https://issues.redhat.com/browse/KOGITO-543)] - DMN - Read Only mode
*   [[KOGITO-1716](https://issues.redhat.com/browse/KOGITO-1716)] - [Test Scenario Editor] Show warning if user opens scesim file for rule based test scenario in VSCode
*   [[KOGITO-2674](https://issues.redhat.com/browse/KOGITO-2674)] - [DMN Designer] Multiple DRDs support - Context menu component
*   [[KOGITO-2675](https://issues.redhat.com/browse/KOGITO-2675)] - [DMN Designer] Multiple DRDs support - Context menu - Show the DRD clickable icon when node is selected
*   [[KOGITO-2761](https://issues.redhat.com/browse/KOGITO-2761)] - [DMN Designer] PMML support - Resource content API integration
*   [[KOGITO-2895](https://issues.redhat.com/browse/KOGITO-2895)] - [DMN Designer] PMML support - PMML Marshaller integration
*   [[KOGITO-2896](https://issues.redhat.com/browse/KOGITO-2896)] - [DMN Designer] PMML support - Using a linked PMML inside the editor
*   [[KOGITO-3022](https://issues.redhat.com/browse/KOGITO-3022)] - [DMN Designer] Multiple DRDs support - Context menu - When multiple nodes are selected, showing context menu with right click

## Fixed issues

VS Code
*   [[KOGITO-3313](https://issues.redhat.com/browse/KOGITO-3313)] - Fix on filename change
*   [[KOGITO-3326](https://issues.redhat.com/browse/KOGITO-3326)] - Enable EmbeddedEditor to support an update on the StateControl instance
*   [[KOGITO-3417](https://issues.redhat.com/browse/KOGITO-3417)] - EditorEnvelopeView reference should be get with a function to be updated
*   [[KOGITO-3314](https://issues.redhat.com/browse/KOGITO-3314)] - Broken link on popup due to circular dependency issue

Editors
*   [[KOGITO-2060](https://issues.redhat.com/browse/KOGITO-2060)] - [Test Scenario Editor] Order of facts of Test Tools in Kogito and BC is different
*   [[KOGITO-3153](https://issues.redhat.com/browse/KOGITO-3153)] - [DMN Designer] PMML support - run via quarkus
*   [[KOGITO-3155](https://issues.redhat.com/browse/KOGITO-3155)] - [SCESIM] Test DMN model including PMML
*   [[KOGITO-3504](https://issues.redhat.com/browse/KOGITO-3504)] - Interrogation mark cannot be used on new inline editor