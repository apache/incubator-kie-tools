package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

public class CorrelationsEditorValidationItem {

    private final Correlation correlation;
    private final boolean emptyID;
    private final boolean emptyName;
    private final boolean emptyPropertyID;
    private final boolean emptyPropertyName;
    private final boolean emptyPropertyType;
    private final boolean duplicateID;
    private final boolean divergingName;
    private final boolean duplicatedPropertyID;

    protected CorrelationsEditorValidationItem(final Correlation correlation,
                                               final boolean duplicateID,
                                               final boolean divergingName,
                                               final boolean duplicatedPropertyID) {
        this.correlation = correlation;
        emptyID = isNullOrEmpty(correlation.getId());
        emptyName = isNullOrEmpty(correlation.getName());
        emptyPropertyID = isNullOrEmpty(correlation.getPropertyId());
        emptyPropertyName = isNullOrEmpty(correlation.getPropertyName());
        emptyPropertyType = isNullOrEmpty(correlation.getPropertyType());
        this.duplicateID = duplicateID;
        this.divergingName = divergingName;
        this.duplicatedPropertyID = duplicatedPropertyID;
    }

    public Correlation getCorrelation() {
        return correlation;
    }

    public boolean isEmptyID() {
        return emptyID;
    }

    public boolean isEmptyName() {
        return emptyName;
    }

    public boolean isEmptyPropertyID() {
        return emptyPropertyID;
    }

    public boolean isEmptyPropertyName() {
        return emptyPropertyName;
    }

    public boolean isEmptyPropertyType() {
        return emptyPropertyType;
    }

    public boolean isDuplicateID() {
        return duplicateID;
    }

    public boolean isDivergingName() {
        return divergingName;
    }

    public boolean isDuplicatePropertyID() {
        return duplicatedPropertyID;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
