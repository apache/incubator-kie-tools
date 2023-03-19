/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Service
@ApplicationScoped
public class LayoutServicesImpl implements LayoutServices {

    private Gson gson;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String toJson(LayoutTemplate layoutTemplate) {
        return gson.toJson(layoutTemplate);
    }

    @Override
    public LayoutTemplate fromJson(String layoutEditorModel) {
        try {
            return gson.fromJson(layoutEditorModel, LayoutTemplate.class);
        } catch (Exception e) {
            return new LayoutTemplate();
        }
    }

}
