<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# Stunner - BPMN - Client side marshalling

## Introduction

The goal for this module is to provide the capability to transform BPMN2 Eclipse models into the Stunner BPMN ones, and vice-versa.

This way, by relying on top of the [Eclipse EMF/XMI - GWT support modules](../kie-wb-common-stunner-bpmn-emf), the Stunner's BPMN2 domain un/marshalling can be properly exposed to the GWT compiler, so making it also available on client side.

## Implementation details

This module contains the necessary services and marshalling dependencies to perform end to end client side marshalling for the BPMN2 domain.

Considerations:

- Most stuff has been created from the already existing code for the BPMN marshallers, which are properly working for backend side (see [kie-wb-common-stunner-bpmn-backend](https://github.com/kiegroup/kie-wb-common/tree/main/kie-wb-common-stunner/kie-wb-common-stunner-bpmn-backend)), but applying the necessary changes for making it available on client side
- It means that this module **MUST BE MANUALLY synchronized** and up to date with any changes on the same code for server side (see [kie-wb-common-stunner-bpmn-backend](https://github.com/kiegroup/kie-wb-common/tree/main/kie-wb-common-stunner/kie-wb-common-stunner-bpmn-backend))

Last synchronization from main has been done on top of the following commit (included):

    From TiagoD
    Aug 20, 2019
    https://github.com/kiegroup/kie-wb-common/commit/d35034c145755e4c9dc5435f97e41068bafdb904

**IMPORTANT**: Please remember to update the above commit information, if necessary, to keep track of changes and be able to properly synchronize the codebase.
