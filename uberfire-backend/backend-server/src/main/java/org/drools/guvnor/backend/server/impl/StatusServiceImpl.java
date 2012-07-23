/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.backend.server.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.backend.server.StatusService;

@ApplicationScoped
public class StatusServiceImpl implements StatusService {

    @Inject private IsDirtyService dirtyService;

    @Override
    public boolean checkDirty(final String id) {
        System.out.println("StatusServiceImpl::checkDirty::OK");
        final Boolean result = dirtyService.isDirty(decode(id));
        if (result == null) {
            return false;
        }
        return result;
    }

    private String decode(String content) {
        try {
            return URLDecoder.decode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return content;
        }
    }
}

