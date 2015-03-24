/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;

import static com.google.gwt.dom.client.BrowserEvents.*;

/**
 * A Cell that renders it's corresponding row index number only
 */
public class AnalysisCell extends AbstractCell<Analysis> {

    public AnalysisCell() {
        // Good citizen: AbstractCell does not initialise an empty set of
        // consumed events
        super(BrowserEvents.CLICK);
    }

    @Override
    public void render( Context context,
                        Analysis analysis,
                        SafeHtmlBuilder sb ) {
        sb.append( SafeHtmlUtils.fromTrustedString( analysis.firstRowToHtmlString() ) );
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               Analysis value,
                               NativeEvent event,
                               ValueUpdater<Analysis> valueUpdater) {
        super.onBrowserEvent(context,
                             parent,
                             value,
                             event,
                             valueUpdater);
        if (CLICK.equals(event.getType())) {
            onEnterKeyDown(context,
                           parent,
                           value,
                           event,
                           valueUpdater);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context,
                                  Element parent,
                                  Analysis value,
                                  NativeEvent event,
                                  ValueUpdater<Analysis> valueUpdater) {

        AnalysisPopup popup = new AnalysisPopup();
        popup.setHTML(value.toHtmlString());
        popup.show();

    }
}
