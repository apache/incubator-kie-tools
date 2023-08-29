/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import bpsim.BpsimFactory;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.jboss.drools.DroolsFactory;

public interface Factories {

    Bpmn2Factory bpmn2 = Bpmn2Factory.eINSTANCE;
    BpsimFactory bpsim = BpsimFactory.eINSTANCE;
    BpmnDiFactory di = BpmnDiFactory.eINSTANCE;
    DcFactory dc = DcFactory.eINSTANCE;
    ExtendedMetaData metaData = ExtendedMetaData.INSTANCE;
    DroolsFactory droolsFactory = DroolsFactory.eINSTANCE;
}
