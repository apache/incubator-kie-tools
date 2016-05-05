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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@Service
@ApplicationScoped
public class LayoutServicesImpl implements LayoutServices {

    private Gson gson;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String convertLayoutToString(LayoutTemplate layoutTemplate) {
        String layoutContent = gson.toJson(layoutTemplate);
        return layoutContent;
    }

    @Override
    public LayoutTemplate convertLayoutFromString(String layoutEditorModel) {
        try {
            if (needsUpgrade(layoutEditorModel)) {
                return LayoutUpgradeTool.convert(layoutEditorModel);
            } else {
                LayoutTemplate layoutTemplate = gson.fromJson(layoutEditorModel, LayoutTemplate.class);
                return layoutTemplate;
            }

        } catch (Exception e) {
            return LayoutTemplate.defaultLayout("");
        }
    }

    private boolean needsUpgrade(String layoutEditorModel) {
        return !version1(layoutEditorModel);
    }

    private boolean version1(String layoutEditorModel) {
        return LayoutUpgradeTool.isVersion1(layoutEditorModel);
    }

}