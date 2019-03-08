/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.properties.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyAgent;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.PropertyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { BindableProxyFactory.class })
public class DeepCloneHelperTest {
    
    @Before
    public void prepare() {
        mockStatic(BindableProxyFactory.class);
    }
    
    @Test
    public void listWithBindableTypesTest() {
        PowerMockito.when(BindableProxyFactory.getBindableProxy(Obj.class)).thenReturn(new BindableProxyImpl());
        Obj obj = new Obj("test");
        List<Obj> asList = Arrays.asList(obj);
        DeepCloneHelper.doDeepCloneList(asList);
        verifyStatic(BindableProxyFactory.class, times(1));
        BindableProxyFactory.getBindableProxy(Obj.class);
        
        verifyStatic(BindableProxyFactory.class, times(1));
        BindableProxyFactory.getBindableProxy(obj);
    }
    
    @Test
    public void listWithoutBindableTypesTest() {
        PowerMockito.when(BindableProxyFactory.getBindableProxy(any(String.class))).thenReturn(null);
        String test = "test";
        List<String> asList = Arrays.asList(test);
        DeepCloneHelper.doDeepCloneList(asList);
        verifyStatic(BindableProxyFactory.class, times(1));
        BindableProxyFactory.getBindableProxy(String.class);
        
        verifyStatic(BindableProxyFactory.class, times(0));
        BindableProxyFactory.getBindableProxy(test);
    }
    
    public static class BindableProxyImpl extends Obj implements BindableProxy<Obj> {

        @Override
        public Object unwrap() {
            return null;
        }

        @Override
        public Object get(String propertyName) {
            return null;
        }

        @Override
        public void set(String propertyName, Object value) {
            
        }

        @Override
        public Map<String, PropertyType> getBeanProperties() {
            return Collections.emptyMap();
        }

        @Override
        public BindableProxyAgent<Obj> getBindableProxyAgent() {
            return null;
        }

        @Override
        public void updateWidgets() {
        }

        @Override
        public Obj deepUnwrap() {
            return null;
        }
        
    }
    
    public static class Obj {
        private String attr;
        
        public Obj() {
        }

        public Obj(String attr) {
            super();
            this.attr = attr;
        }

        public String getAttr() {
            return attr;
        }
        public void setAttr(String attr) {
            this.attr = attr;
        }
        
    }
}
