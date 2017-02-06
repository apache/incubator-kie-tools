/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.bs3;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.AbstractIconRendererTest;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BS3IconRendererTest extends AbstractIconRendererTest<BS3IconRenderer, IconType, BS3IconRendererView> {

    @Override
    protected BS3IconRenderer getRendererIncance(BS3IconRendererView view) {
        return new BS3IconRenderer(view);
    }

    @Override
    protected Class<BS3IconRendererView> getViewClass() {
        return BS3IconRendererView.class;
    }
}
