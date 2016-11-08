/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.labelManaged;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.AbstractFormGroupDisplayerTest;

@RunWith( GwtMockitoTestRunner.class )
public class LabelManagedFormGroupDisplayerTest extends AbstractFormGroupDisplayerTest<LabelManagedFormGroupDisplayer, LabelManagedFormGroupDisplayerView> {
    @Override
    protected Class<LabelManagedFormGroupDisplayerView> getViewClass() {
        return LabelManagedFormGroupDisplayerView.class;
    }

    @Override
    protected LabelManagedFormGroupDisplayer getDisplayerInstance( LabelManagedFormGroupDisplayerView view ) {
        return new LabelManagedFormGroupDisplayer( view );
    }
}
