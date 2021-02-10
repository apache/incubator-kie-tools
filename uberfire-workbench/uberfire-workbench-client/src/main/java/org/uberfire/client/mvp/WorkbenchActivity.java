/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.mvp;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.Position;

/**
 * WorkbenchActivity and its subinterfaces define the interface between UberFire framework behaviour and
 * application-defined behaviour.
 * <p>
 * In the model-view-presenter (MVP) sense, an Activity is essentially an application-provided Presenter: it has a view
 * (its widget) and it defines a set of operations that can affect that view.
 * <p>
 * Applications can implement an Activity interface directly, they can subclass one of the abstract Activity
 * implementations that come with the framework, or they may rely on UberFire's annotation processors to generate
 * Activity implementations from annotated Java objects.
 * <p>
 * For example, to define a new Screen in an application, you can implement {@link WorkbenchScreenActivity}, extend
 * {@link AbstractWorkbenchScreenActivity}, or annotate a class with {@link WorkbenchScreen} and follow the rules
 * associated with that annotation.
 * <p>
 * Similarly for {@link WorkbenchClientEditorActivity} and {@link WorkbenchClientEditor};
 * {@link PerspectiveActivity}, {@link AbstractWorkbenchPerspectiveActivity}, and {@link WorkbenchPerspective}; and so
 * on.
 */
@JsType
public interface WorkbenchActivity extends Activity {

    /**
     * Specifies custom positioning for this activity's view when the request to launch it comes without specific
     * positioning information of its own. For a newly launching activity, when this method returns a non-null value,
     * UberFire will attempt to create a new panel for this activity and add that new panel as a child of the current
     * perspective's root panel. The activity's view will then be added to the new panel.
     * <p>
     * If this method returns null, UberFire will attempt to add this activity's view directly to the root panel.
     * <p>
     * <b>Developers of cross-application reusable components should use caution with this feature.</b> If the component
     * declares a default position that is not supported by the root panel the application uses, the application's root
     * panel could throw a ClassCastException when the framework attempts to create the child panel at the given
     * position.
     * @return the Position to add a new child panel to the root that accommodates this activity's view, or null if this
     * activity's view should be added directly to the root panel.
     */
    Position getDefaultPosition();

    @JsIgnore
    IsWidget getWidget();

    default Element getWidgetElement() {
        IsWidget widget = getWidget();
        return (widget == null) ? null : widget.asWidget().getElement();
    }
}
