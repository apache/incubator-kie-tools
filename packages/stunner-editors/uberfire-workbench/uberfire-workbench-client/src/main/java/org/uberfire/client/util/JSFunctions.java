package org.uberfire.client.util;

import elemental2.dom.DomGlobal;
import org.uberfire.client.mvp.EditorActivity;

public class JSFunctions {

    private JSFunctions() {
        // Empty
    }

    public static void nativeRegisterGwtEditorProvider() {
        DomGlobal.console.log(JSFunctions.class.getCanonicalName()+".nativeRegisterGwtEditorProvider");
    }/*-{

        console.log("registerGWTEditorProvider");

        $wnd.gwtEditorBeans = new Map();
        $wnd.resolveEditor = function (id) {
            return $wnd.gwtEditorBeans.get(id);
        };

        $wnd.GWTEditor = function (instance) {
            this.instance = instance;
        };

        $wnd.GWTEditor.prototype.onOpen = function () {
            this.instance.@org.uberfire.client.mvp.EditorActivity::onOpen()();
        };

        $wnd.GWTEditor.prototype.setContent = function (path, value) {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::setContent(Ljava/lang/String;Ljava/lang/String;)(path, value);
        };

        $wnd.GWTEditor.prototype.getContent = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getContent()();
        };

        $wnd.GWTEditor.prototype.getPreview = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getPreview()();
        };

        $wnd.GWTEditor.prototype.getView = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::getWidgetElement()();
        };

         $wnd.GWTEditor.prototype.validate = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::validate()();
        };

        $wnd.GWTEditorSupplier = function (bean) {
            this.bean = bean;
        };

        $wnd.GWTEditorSupplier.prototype.get = function () {
            return new $wnd.GWTEditor(this.bean.@org.jboss.errai.ioc.client.container.SyncBeanDef::newInstance()());
        }

        $wnd.GWTEditor.prototype.undo = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::undo()();
        };

        $wnd.GWTEditor.prototype.redo = function () {
            return this.instance.@org.uberfire.client.mvp.EditorActivity::redo()();
        };

    }-*/;

    //public static native void nativeRegisterGwtClientBean(final String id, final SyncBeanDef<EditorActivity> bean) /*-{

    public static void nativeRegisterGwtClientBean(final String id, final Object bean) {
        DomGlobal.console.log(JSFunctions.class.getCanonicalName()+".nativeRegisterGwtEditorProvider BLOCKED");
    }/*-{
        $wnd.gwtEditorBeans.set(id, new $wnd.GWTEditorSupplier(bean));
    }-*/;

    public static void notifyJSReady() {
        DomGlobal.console.log(JSFunctions.class.getCanonicalName()+".nativeRegisterGwtEditorProvider");
    }/*-{
        if ($wnd.appFormerGwtFinishedLoading) {
            $wnd.appFormerGwtFinishedLoading();
        }
    }-*/;
}
