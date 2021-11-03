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

package org.uberfire.client.views.pfly;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * This class is developed to provide an temporary HTML5 DnD API for Selenium.
 * Unfortunately, Selenium lack of support for HTML5 DnD.
 * https://code.google.com/p/selenium/issues/detail?id=3604
 *
 * Usage: $('#origin').simulateDragDrop({ dropTarget: '#target'});
 *
 */
@EntryPoint
public class HTML5DndSeleniumSupport {

    /*
     * Ensures that JQuery is loaded before this runs.
     */
    @Inject
    private PatternFlyEntryPoint entryPoint;

    @PostConstruct
    public void init() {
        prepareDnd(this);
    }

    private native void prepareDnd(HTML5DndSeleniumSupport js)/*-{
        (function ($) {
            $.fn.simulateDragDrop = function (options) {
                return this.each(function () {
                    new $.simulateDragDrop(this, options);
                });
            };
            $.simulateDragDrop = function (elem, options) {
                this.options = options;
                this.simulateEvent(elem, options);
            };
            $.extend($.simulateDragDrop.prototype, {
                simulateEvent: function (elem, options) {
                    var type = 'dragstart';
                    var event = this.createEvent(type);
                    this.dispatchEvent(elem, type, event);
                    type = 'drop';
                    var dropEvent = this.createEvent(type, {});
                    dropEvent.dataTransfer = event.dataTransfer;
                    this.dispatchEvent($(options.dropTarget)[0], type, dropEvent);
                    type = 'dragend';
                    var dragEndEvent = this.createEvent(type, {});
                    dragEndEvent.dataTransfer = event.dataTransfer;
                    this.dispatchEvent(elem, type, dragEndEvent);
                },
                createEvent: function (type) {
                    var event = document.createEvent("CustomEvent");
                    event.initCustomEvent(type, true, true, null);
                    event.dataTransfer = {
                        data: {},
                        setData: function (type, val) {
                            this.data[type] = val;
                        },
                        getData: function (type) {
                            return this.data[type];
                        }
                    };
                    return event;
                },
                dispatchEvent: function (elem, type, event) {
                    if (elem.dispatchEvent) {
                        elem.dispatchEvent(event);
                    } else if (elem.fireEvent) {
                        elem.fireEvent("on" + type, event);
                    }
                }
            });
        })($wnd.jQuery);
    }-*/;
}