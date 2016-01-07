/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.security.management;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.UserSystemManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>Base test class for users/groups/roles manager based services.</p>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {

    @Mock
    protected UserSystemManager userSystemManager;

    protected AbstractEntityManager.SearchRequest buildSearchRequestMock(String pattern, int page, int pageSize) {
        AbstractEntityManager.SearchRequest request = mock(AbstractEntityManager.SearchRequest.class);
        when(request.getSearchPattern()).thenReturn(pattern);
        when(request.getPage()).thenReturn(page);
        when(request.getPageSize()).thenReturn(pageSize);
        return request;
    }

}