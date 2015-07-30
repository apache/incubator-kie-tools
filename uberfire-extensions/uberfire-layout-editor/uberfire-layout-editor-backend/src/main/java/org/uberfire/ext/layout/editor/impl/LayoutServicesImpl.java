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