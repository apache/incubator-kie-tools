package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.perspective.JSNativePerspective;
import org.uberfire.client.perspective.JSWorkbenchPerspectiveActivity;
import org.uberfire.client.plugin.JSNativePlugin;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
/**
 * This class is developed to provide an temporary HTML5 DnD API for Selenium.
 * Unfortunately, Selenium lack of support for HTML5 DnD.
 * https://code.google.com/p/selenium/issues/detail?id=3604
 *
 * Usage: $('#origin').simulateDragDrop({ dropTarget: '#target'});
 *
 */
public class HTML5DndSeleniumSupport implements UberfireJSExporter {

    @Override
    public void export() {
        prepareDnd(this);
    }

    private native void prepareDnd(HTML5DndSeleniumSupport js)/*-{
        (function( $ ) {
            $.fn.simulateDragDrop = function(options) {
                return this.each(function() {
                    new $.simulateDragDrop(this, options);
                });
            };
            $.simulateDragDrop = function(elem, options) {
                this.options = options;
                this.simulateEvent(elem, options);
            };
            $.extend($.simulateDragDrop.prototype, {
                simulateEvent: function(elem, options) {
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
                createEvent: function(type) {
                    var event = document.createEvent("CustomEvent");
                    event.initCustomEvent(type, true, true, null);
                    event.dataTransfer = {
                        data: {
                        },
                        setData: function(type, val){
                            this.data[type] = val;
                        },
                        getData: function(type){
                            return this.data[type];
                        }
                    };
                    return event;
                },
                dispatchEvent: function(elem, type, event) {
                    if(elem.dispatchEvent) {
                        elem.dispatchEvent(event);
                    }else if( elem.fireEvent ) {
                        elem.fireEvent("on"+type, event);
                    }
                }
            });
        })($wnd.jQuery);
    }-*/;
}