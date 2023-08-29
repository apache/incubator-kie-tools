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

package org.kie.workbench.common.forms.fields.test;

import java.util.Collection;

import org.kie.workbench.common.forms.fields.shared.AbstractFieldManager;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.provider.CheckBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.provider.DatePickerFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.provider.DecimalBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.provider.IntegerBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider.ListBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.provider.RadioGroupFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.provider.SliderFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.provider.TextAreaFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.provider.TextBoxFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.provider.MultipleSubFormFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.provider.SubFormFieldProvider;

public class TestFieldManager extends AbstractFieldManager {

    public TestFieldManager() {
        super(new TestMetaDataEntryManager());
        registerFieldProvider(new TextBoxFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new TextAreaFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new IntegerBoxFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new DecimalBoxFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new CheckBoxFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new ListBoxFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new RadioGroupFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new DatePickerFieldProvider() {
            {
                doRegisterFields();
            }
        });
        registerFieldProvider(new SliderFieldProvider() {
            {
                doRegisterFields();
            }
        });

        registerFieldProvider(new MultipleSelectorProvider() {
            {
                doRegisterFields();
            }
        });

        registerFieldProvider(new MultipleInputProvider() {
            {
                doRegisterFields();
            }
        });

        registerFieldProvider(new SubFormFieldProvider());
        registerFieldProvider(new MultipleSubFormFieldProvider());
    }

    public Collection<BasicTypeFieldProvider> getAllBasicTypeProviders() {
        return basicProviders;
    }

    public Collection<BasicTypeFieldProvider> getAllBasicMultipleTypeProviders() {
        return basicMultipleProviders;
    }
}
