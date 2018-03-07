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
import java.util.Random;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated
public class WidgetsShowcaseView implements UberElement<WidgetsShowcasePresenter> {

    @Inject
    private Logger logger;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Inject
    @DataField
    private Div root;

    @Inject
    @DataField
    private Anchor popover;

    @Inject
    @DataField("dynamic-popover")
    private Div dynamicPopover;

    @Inject
    @DataField("popover-override")
    private Anchor popoverOverride;

    @Inject
    @DataField("popover-button")
    private Button popoverButton;

    @Inject
    @DataField("datetimepicker")
    private DateRangePicker dateRangePicker;

    @Override
    public void init(final WidgetsShowcasePresenter presenter) {
        //Ex 1
        jQueryPopover.wrap(popover).popover();
        //Ex 2
        final PopoverOptions popoverOptions = new PopoverOptions();
        popoverOptions.setContent(e -> "dynamic content: " + new Random().nextInt());
        popoverOptions.setTrigger("click");
        popoverOptions.setPlacement("bottom");
        popoverOptions.setContainer("body");
        final Anchor anchor = (Anchor) getDocument().createElement("a");
        anchor.setAttribute("data-toggle",
                            "popover");
        anchor.setTextContent("View popover");
        dynamicPopover.appendChild(anchor);
        jQueryPopover.wrap(anchor).popover(popoverOptions);
        //Ex 3
        popoverOverride.setAttribute("data-content",
                                     "New content!");
        jQueryPopover.wrap(popoverOverride).popover();
        //Ex 4
        final PopoverOptions popoverButtonOptions = new PopoverOptions();
        popoverButtonOptions.setContent(e -> {
            final Element span = DomGlobal.document.createElement("span");
            span.textContent = "dynamic element text";
            return span;
        });
        final Popover popover = jQueryPopover.wrap(popoverButton);
        popover.popover(popoverButtonOptions);
        popover.addShowListener(() -> logger.info("popover show callback"));
        popover.addHideListener(() -> logger.info("popover hide callback"));
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
            logger.info("picker label: {}",
                        p.getChosenLabel());
        });
    }

    protected DateRangePicker.DateRangePickerCallback getDateRangePickerCallback() {
        return (start, end, label) -> {
            logger.info("picker callback");
            logger.info("picker start date: {}",
                        start);
            logger.info("picker start date as java.util.Date: {}",
                        start.asDate());
            logger.info("picker start date valueof: {}",
                        new Date(start.valueOf().longValue()));
            logger.info("picker start date aslong: {}",
                        new Date(start.asLong()));
            logger.info("picker end date: {}",
                        end);
            logger.info("picker end date as java.util.Date: {}",
                        end.asDate());
            logger.info("picker label: {}",
                        label);
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
