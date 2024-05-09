#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

@quay.io/kiegroup/kogito-jit-runner
Feature: Kogito-jit-runner feature.

  Scenario: verify if all labels are correctly set on kogito-jit-runner image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito JIT Runner
    And the image should contain label io.k8s.display-name with value Kogito JIT Runner
    And the image should contain label io.openshift.tags with value kogito,jit-runner

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar

  Scenario: Verify that jit runner can evaluate a DMN model with a context
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then check that page is served
      | property        | value                                                                                            |
      | port            | 8080                                                                                             |
      | path            | /jitdmn                                                                                          |
      | wait            | 80                                                                                               |
      | expected_phrase | {"sum":3,"m":2,"n":1}                                                                            |
      | request_method  | POST                                                                                             |
      | content_type    | application/json                                                                                 |
      | request_body    | {"context": {"n" : 1, "m" : 2}, "model": "<dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_81A31B42-A686-4ED2-81FB-C1F91A95D685\" name=\"new-file\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kiegroup.org/dmn/_35091C3B-6022-4D40-8982-D528940CD5F9\">\n  <dmn:extensionElements/>\n  <dmn:inputData id=\"_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" name=\"n\">\n    <dmn:extensionElements/>\n    <dmn:variable id=\"_EC4D123A-D6D4-4E5D-B369-6E99F57D9C22\" name=\"n\" typeRef=\"number\"/>\n  </dmn:inputData>\n  <dmn:decision id=\"_1D69C44E-D782-492A-A50D-740B444F1993\" name=\"sum\">\n    <dmn:extensionElements/>\n    <dmn:variable id=\"_3AF7A705-8304-4B5E-8EC7-05D9934E6C06\" name=\"sum\" typeRef=\"number\"/>\n    <dmn:informationRequirement id=\"_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\">\n      <dmn:requiredInput href=\"#_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\"/>\n    </dmn:informationRequirement>\n    <dmn:informationRequirement id=\"_C52CB29E-3236-4661-8856-7276AE8ED01F\">\n      <dmn:requiredInput href=\"#_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\"/>\n    </dmn:informationRequirement>\n    <dmn:literalExpression id=\"_3DB33034-AC21-45DE-A5B7-D6B09B01ED1E\">\n      <dmn:text>n + m</dmn:text>\n    </dmn:literalExpression>\n  </dmn:decision>\n  <dmn:inputData id=\"_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" name=\"m\">\n    <dmn:extensionElements/>\n    <dmn:variable id=\"_455CD571-BBD9-4762-B496-832E7EBCD07F\" name=\"m\" typeRef=\"number\"/>\n  </dmn:inputData>\n  <dmndi:DMNDI>\n    <dmndi:DMNDiagram id=\"_7FC1E997-A627-409E-A6D5-9A30F2F30AB4\" name=\"DRG\">\n      <di:extension>\n        <kie:ComponentsWidthsExtension>\n          <kie:ComponentWidths dmnElementRef=\"_3DB33034-AC21-45DE-A5B7-D6B09B01ED1E\">\n            <kie:width>300</kie:width>\n          </kie:ComponentWidths>\n        </kie:ComponentsWidthsExtension>\n      </di:extension>\n      <dmndi:DMNShape id=\"dmnshape-drg-_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" dmnElementRef=\"_6FFA48B5-FB55-4962-9E64-F08418BBFF9E\" isCollapsed=\"false\">\n        <dmndi:DMNStyle>\n          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>\n          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>\n          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>\n        </dmndi:DMNStyle>\n        <dc:Bounds x=\"704\" y=\"364\" width=\"100\" height=\"50\"/>\n        <dmndi:DMNLabel/>\n      </dmndi:DMNShape>\n      <dmndi:DMNShape id=\"dmnshape-drg-_1D69C44E-D782-492A-A50D-740B444F1993\" dmnElementRef=\"_1D69C44E-D782-492A-A50D-740B444F1993\" isCollapsed=\"false\">\n        <dmndi:DMNStyle>\n          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>\n          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>\n          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>\n        </dmndi:DMNStyle>\n        <dc:Bounds x=\"756\" y=\"283\" width=\"100\" height=\"50\"/>\n        <dmndi:DMNLabel/>\n      </dmndi:DMNShape>\n      <dmndi:DMNShape id=\"dmnshape-drg-_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" dmnElementRef=\"_B8221A07-DFB5-40BC-95A9-7926A6EC55C4\" isCollapsed=\"false\">\n        <dmndi:DMNStyle>\n          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>\n          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>\n          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>\n        </dmndi:DMNStyle>\n        <dc:Bounds x=\"822\" y=\"364\" width=\"100\" height=\"50\"/>\n        <dmndi:DMNLabel/>\n      </dmndi:DMNShape>\n      <dmndi:DMNEdge id=\"dmnedge-drg-_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\" dmnElementRef=\"_E0FE5C90-5EAF-45DB-ABFD-10D27FA97AB4\">\n        <di:waypoint x=\"754\" y=\"389\"/>\n        <di:waypoint x=\"806\" y=\"333\"/>\n      </dmndi:DMNEdge>\n      <dmndi:DMNEdge id=\"dmnedge-drg-_C52CB29E-3236-4661-8856-7276AE8ED01F\" dmnElementRef=\"_C52CB29E-3236-4661-8856-7276AE8ED01F\">\n        <di:waypoint x=\"872\" y=\"389\"/>\n        <di:waypoint x=\"806\" y=\"333\"/>\n      </dmndi:DMNEdge>\n    </dmndi:DMNDiagram>\n  </dmndi:DMNDI>\n</dmn:definitions>"} |
