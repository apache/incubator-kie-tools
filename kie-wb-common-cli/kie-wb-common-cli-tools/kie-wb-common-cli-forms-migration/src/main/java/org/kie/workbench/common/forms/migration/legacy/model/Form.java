/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.migration.legacy.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Form {

    public static final String DISPLAY_MODE_TEMPLATE = "template";

    private Long id;

    private String subject;

    private String name;

    private String displayMode;

    private String labelMode;

    private String showMode;

    private Long status;

    private Set<FormDisplayInfo> formDisplayInfos;

    private Set<Field> formFields = new TreeSet<Field>();

    private Set<DataHolder> holders;

    private int migrationStep = 0;

    public Form() {
        formDisplayInfos = new TreeSet<>();
        holders = new TreeSet<>();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayMode() {
        return this.displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public String getLabelMode() {
        return labelMode;
    }

    public void setLabelMode(String labelMode) {
        this.labelMode = labelMode;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public Long getStatus() {
        return this.status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Set<FormDisplayInfo> getFormDisplayInfos() {
        return this.formDisplayInfos;
    }

    public void setFormDisplayInfos(Set<FormDisplayInfo> formDisplayInfos) {
        this.formDisplayInfos = formDisplayInfos;
    }

    public Set<Field> getFormFields() {
        return this.formFields;
    }

    public void setFormFields(Set<Field> formFields) {
        this.formFields = formFields;
    }

    public Set<Field> getFieldsForDataHolder(DataHolder dataHolder) {
        return new TreeSet<>(formFields.stream()
                                     .filter(field -> dataHolder.ownsField(field)).collect(Collectors.toSet()));
    }

    public void setDataHolder(DataHolder holder) {
        if (holder == null) {
            return;
        }

        if ((holder.getInputId() == null || holder.getInputId().trim().length() == 0) && (holder.getOuputId() == null || holder.getOuputId().trim().length() == 0)) {
            return;
        }

        if (getDataHolderById(holder.getInputId()) != null || getDataHolderById(holder.getOuputId()) != null) {
            holders.remove(holder);
        }
        holders.add(holder);
    }

    public DataHolder getDataHolderById(String srcId) {
        if (srcId == null || srcId.trim().length() == 0) {
            return null;
        }
        if (getHolders() != null) {
            for (DataHolder dataHolder : holders) {
                if (srcId.equals(dataHolder.getUniqeId())) {
                    return dataHolder;
                }
            }
        }
        return null;
    }

    /**
     * Get field by name
     * @param name Desired field name, must be not null
     * @return field by given name or null if it doesn't exist.
     */
    public Field getField(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        if (getFormFields() != null) {

            for (Field field : formFields) {
                if (name.equals(field.getFieldName())) {
                    return field;
                }
            }
        }
        return null;
    }

    protected String getDisplayModeText(String selector) {
        String text = null;
        if (getFormDisplayInfos() != null) {
            for (Iterator it = getFormDisplayInfos().iterator(); it.hasNext(); ) {
                FormDisplayInfo dInfo = (FormDisplayInfo) it.next();
                if (selector.equals(dInfo.getDisplayMode())) {
                    text = dInfo.getDisplayData();
                    break;
                }
            }
        }
        return text;
    }

    protected void setDisplayModeText(final String selector, final String data) {
        if (getFormDisplayInfos() == null) {
            setFormDisplayInfos(new HashSet());
        }
        FormDisplayInfo theTemplateInfo = null;

        for (Iterator it = getFormDisplayInfos().iterator(); it.hasNext(); ) {
            FormDisplayInfo dInfo = (FormDisplayInfo) it.next();
            if (selector.equals(dInfo.getDisplayMode())) {
                theTemplateInfo = dInfo;
                break;
            }
        }

        if (theTemplateInfo == null) {
            theTemplateInfo = new FormDisplayInfo();
            getFormDisplayInfos().add(theTemplateInfo);
        }
        theTemplateInfo.setDisplayData(data);
        theTemplateInfo.setDisplayMode(selector);
    }

    public Set<DataHolder> getHolders() {
        return holders;
    }

    public String getFormTemplate() {
        return getDisplayModeText(DISPLAY_MODE_TEMPLATE);
    }

    public void setFormTemplate(final String data) {
        setDisplayModeText(DISPLAY_MODE_TEMPLATE, data);
    }

    public int getMigrationStep() {
        return migrationStep;
    }

    public void setMigrationStep(int migrationStep) {
        this.migrationStep = migrationStep;
    }
}
