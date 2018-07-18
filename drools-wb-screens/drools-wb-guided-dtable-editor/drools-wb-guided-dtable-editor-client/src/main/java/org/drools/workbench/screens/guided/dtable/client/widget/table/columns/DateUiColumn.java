/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Text;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerSingletonDOMElementFactory;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

public class DateUiColumn extends BaseSingletonDOMElementUiColumn<Date, DatePicker, DatePickerDOMElement, DatePickerSingletonDOMElementFactory> {

    private static final String droolsDateFormat = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( droolsDateFormat );

    public DateUiColumn( final List<HeaderMetaData> headerMetaData,
                         final double width,
                         final boolean isResizable,
                         final boolean isVisible,
                         final GuidedDecisionTablePresenter.Access access,
                         final DatePickerSingletonDOMElementFactory factory ) {
        super( headerMetaData,
               new CellRenderer<Date, DatePicker, DatePickerDOMElement>( factory ) {
                   @Override
                   protected void doRenderCellContent( final Text t,
                                                       final Date value,
                                                       final GridBodyCellRenderContext context ) {
                       t.setText( dateTimeFormat.format( value ) );
                   }
               },
               width,
               isResizable,
               isVisible,
               access,
               factory );
    }

    @Override
    public void doEdit( final GridCell<Date> cell,
                        final GridBodyCellRenderContext context,
                        final Consumer<GridCellValue<Date>> callback ) {
        factory.attachDomElement(context,
                                 ConsumerFactory.makeOnCreationCallback(cell ),
                                 ConsumerFactory.makeOnDisplayDatePickerCallback() );
    }

}
