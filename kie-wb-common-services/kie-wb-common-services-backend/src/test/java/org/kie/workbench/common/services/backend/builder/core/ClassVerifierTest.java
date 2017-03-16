/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClassVerifierTest {
	
	@Mock
	private KieModuleMetaData kieModuleMetaData;
	@Mock
	private TypeSourceResolver typeSourceResolver;

	@Before
	public void setUp() throws Exception{
		when(kieModuleMetaData.getPackages()).thenReturn(Arrays.asList("org.kie.workbench.common.services.backend.builder"));
		when(kieModuleMetaData.getClasses("org.kie.workbench.common.services.backend.builder")).thenReturn(Arrays.asList("SomeClass"));
		
		when(kieModuleMetaData.getClass("org.kie.workbench.common.services.backend.builder", "SomeClass")).thenThrow(
				new IllegalAccessError("The access to the class is not allowed"));				
	}
	
	@Test
	public void testVerifyClass(){
		WhiteList whiteList = new WhiteList();	
		whiteList.add("org.kie.workbench.common.services.backend.builder");
		
		ClassVerifier classVerifier = new ClassVerifier(kieModuleMetaData, typeSourceResolver);
		List<BuildMessage> messages  = classVerifier.verify(whiteList);
		
		assertEquals(messages.size(), 1);
		assertEquals("Verification of class org.kie.workbench.common.services.backend.builder.SomeClass failed and will not be available for authoring.\n"
				+ "Underlying system error is: The access to the class is not allowed. Please check the necessary external dependencies for this project are configured correctly.",
				messages.get(0).getText());
	}		
}