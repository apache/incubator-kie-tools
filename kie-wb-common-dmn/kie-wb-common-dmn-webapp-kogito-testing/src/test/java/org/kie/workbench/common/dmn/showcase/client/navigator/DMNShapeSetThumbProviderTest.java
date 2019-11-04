/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.navigator;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GwtMockitoTestRunner.class)
public class DMNShapeSetThumbProviderTest {

    private DMNShapeSetThumbProvider provider;

    @Before
    public void setup() {
        this.provider = new DMNShapeSetThumbProvider();
    }

    @Test
    public void testGetThumbnailUri() {
        final SafeUri uri = provider.getThumbnailUri();

        assertThat(uri.asString()).isEqualTo(DMNShapeSetThumbProvider.THUMBNAIL_URI);
    }

    @Test
    public void testGetSourceType() {
        assertThat(provider.getSourceType()).isEqualTo(String.class);
    }

    @Test
    public void testThumbFor() {
        assertThat(provider.thumbFor("")).isTrue();
        assertThat(provider.thumbFor(null)).isTrue();
        assertThat(provider.thumbFor("anything")).isTrue();
    }
}
