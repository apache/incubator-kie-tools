package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

public interface CorrelationsEditorWidgetView {

    void init(final CorrelationsEditorWidgetView.Presenter presenter);

    Widget getWidget();

    List<Correlation> getCorrelations();

    void setCorrelations(final List<Correlation> correlations);

    void update(List<CorrelationsEditorValidationItem> validationItems);

    interface Presenter extends HasValueChangeHandlers<List<Correlation>> {

        Widget getWidget();

        List<Correlation> getCorrelations();

        void setCorrelations(final List<Correlation> correlations);

        void addCorrelation();

        void removeCorrelation(final Correlation correlation);

        void update(List<CorrelationsEditorValidationItem> validationItems);

        HandlerRegistration addValueChangeHandler(final ValueChangeHandler<List<Correlation>> handler);
    }
}
