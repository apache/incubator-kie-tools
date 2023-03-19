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
package org.dashbuilder.navigation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.navigation.NavItemContext;

public class NavItemContextImpl implements NavItemContext {

    Map<String,String> ctxMap = new HashMap<>();

    public NavItemContextImpl() {
    }

    public NavItemContextImpl(String ctx) {
        init(ctx);
    }

    @Override
    public void init(String ctx) {
        ctxMap.clear();
        if (ctx != null && ctx.length() > 0) {
            // TODO: encoding
            String[] tokens = ctx.split(";");
            for(int i=0;i<tokens.length;i++) {
                String[] pair = tokens[i].split("=");
                if (pair.length == 2) {
                    ctxMap.put(pair[0], pair[1]);
                }
            }
        }
    }

    @Override
    public Collection<String> getPropertyIds() {
        return ctxMap.keySet();
    }

    @Override
    public String getProperty(String id) {
        return ctxMap.get(id);
    }

    @Override
    public String removeProperty(String id) {
        return ctxMap.remove(id);
    }

    @Override
    public NavItemContext setProperty(String id, String value) {
        ctxMap.put(id, value);
        return this;
    }

    @Override
    public boolean includesPropertiesOf(NavItemContext ctx) {
        for (String p : ctx.getPropertyIds()) {
            String v1 = this.getProperty(p);
            String v2 = ctx.getProperty(p);
            if (v1 == null && v2 != null) {
                return false;
            }
            if (v1 != null && !v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        ctxMap.keySet().forEach(k -> {
            out.append(k).append("=").append(ctxMap.get(k)).append(";");
        });
        return out.toString();
    }
}
