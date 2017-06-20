/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.screens.widgets;

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated
public class WidgetsShowcaseView implements UberElement<WidgetsShowcasePresenter> {

    @Inject
    private Logger logger;

    @Inject
    @DataField
    private Div root;

    @Inject
    @DataField
    private Popover popover;

    @Inject
    @DataField("dynamic-popover")
    private Div dynamicPopover;

    @Inject
    @DataField("popover-override")
    private Popover popoverOverride;

    @Inject
    @DataField("datetimepicker")
    private DateRangePicker dateRangePicker;

    @Inject
    private ManagedInstance<Popover> popoversProvider;

    @Override
    public void init(final WidgetsShowcasePresenter presenter) {
        //Ex 2
        final Popover newPopover = popoversProvider.get();
        newPopover.setContent("dynamic text");
        newPopover.setTrigger("click");
        newPopover.setPlacement("bottom");
        newPopover.setContainer("body");
        final Anchor anchor = (Anchor) getDocument().createElement("a");
        anchor.setAttribute("data-toggle",
                            "popover");
        anchor.setTextContent("View popover");
        newPopover.getElement().appendChild(anchor);
        dynamicPopover.appendChild(newPopover.getElement());
        //Ex 3
        popoverOverride.setContent("New content!");
        //Date range ex
        final DateRangePickerOptions options = getDateRangePickerOptions();

        dateRangePicker.setup(options,
                              getDateRangePickerCallback());
        dateRangePicker.addApplyListener((e, p) -> {
            logger.info("picker apply listener");
            logger.info("picker start date: {}",
                        p.getStartDate());
            logger.info("picker end date: {}",
                        p.getEndDate());
        });
    }

    protected DateRangePicker.DateRangePickerCallback getDateRangePickerCallback() {
        return (start, end, label) -> {
            logger.info("picker callback");
            logger.info("picker start date: {}", start);
            logger.info("picker start date as java.util.Date: {}", start.asDate());
            logger.info("picker start date valueof: {}", new Date(start.valueOf().longValue()));
            logger.info("picker start date aslong: {}", new Date(start.asLong()));
            logger.info("picker end date: {}", end);
            logger.info("picker end date as java.util.Date: {}", end.asDate());
            logger.info("picker label: {}", label);
        };
    }

    protected DateRangePickerOptions getDateRangePickerOptions() {
        final DateRangePickerOptions options = DateRangePickerOptions.create();
        options.setAutoUpdateInput(true);
        options.setAutoApply(true);
        options.setTimePicker(true);
        options.setTimePickerIncrement(30);
        options.setMaxDate(moment().endOf("day"));
        options.addRange("Today",
                         moment().startOf("day"),
                         moment().endOf("day"));

        options.addRange("Last Hour",
                         moment().subtract(1,
                                           "hours"),
                         moment().endOf("day"));
        options.addRange("Last 24 Hours",
                         moment().subtract(24,
                                           "hours"),
                         moment().endOf("day"));
        return options;
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }
}
