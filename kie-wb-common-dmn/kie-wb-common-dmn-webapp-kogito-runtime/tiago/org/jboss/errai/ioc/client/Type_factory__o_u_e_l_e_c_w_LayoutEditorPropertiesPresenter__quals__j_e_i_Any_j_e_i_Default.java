package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementPart;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

public class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesPresenter> { private class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutEditorPropertiesPresenter implements Proxy<LayoutEditorPropertiesPresenter> {
    private final ProxyHelper<LayoutEditorPropertiesPresenter> proxyHelper = new ProxyHelperImpl<LayoutEditorPropertiesPresenter>("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutEditorPropertiesPresenter instance) {

    }

    public LayoutEditorPropertiesPresenter asBeanType() {
      return this;
    }

    public void setInstance(final LayoutEditorPropertiesPresenter instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public UberElement getView() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final UberElement retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public LayoutEditor getLayoutEditor() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final LayoutEditor retVal = proxiedInstance.getLayoutEditor();
        return retVal;
      } else {
        return super.getLayoutEditor();
      }
    }

    @Override public LiveSearchService getSearchService() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final LiveSearchService retVal = proxiedInstance.getSearchService();
        return retVal;
      } else {
        return super.getSearchService();
      }
    }

    @Override public void setSelectionHandler(SingleLiveSearchSelectionHandler selectionHandler) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setSelectionHandler(selectionHandler);
      } else {
        super.setSelectionHandler(selectionHandler);
      }
    }

    @Override public void edit(LayoutEditor layoutEditor) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.edit(layoutEditor);
      } else {
        super.edit(layoutEditor);
      }
    }

    @Override public void edit(LayoutEditorElement layoutElement) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.edit(layoutElement);
      } else {
        super.edit(layoutElement);
      }
    }

    @Override public void edit(LayoutEditorElementPart layoutElementPart) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.edit(layoutElementPart);
      } else {
        super.edit(layoutElementPart);
      }
    }

    @Override public void dispose() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.dispose();
      } else {
        super.dispose();
      }
    }

    @Override public String getDisplayPosition(LayoutEditorElement element) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDisplayPosition(element);
        return retVal;
      } else {
        return super.getDisplayPosition(element);
      }
    }

    @Override public String getElementName(LayoutEditorElement element) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getElementName(element);
        return retVal;
      } else {
        return super.getElementName(element);
      }
    }

    @Override public void clearElementProperties() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearElementProperties();
      } else {
        super.clearElementProperties();
      }
    }

    @Override public void reset() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.reset();
      } else {
        super.reset();
      }
    }

    @Override public void onPartSelected(String partId) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartSelected(partId);
      } else {
        super.onPartSelected(partId);
      }
    }

    @Override protected void fillElementParts(LayoutEditorElement element) {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        LayoutEditorPropertiesPresenter_fillElementParts_LayoutEditorElement(proxiedInstance, element);
      } else {
        super.fillElementParts(element);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorPropertiesPresenter.class, "Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorPropertiesPresenter.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent", new AbstractCDIEventCallback<LayoutElementPropertyChangedEvent>() {
      public void fireEvent(final LayoutElementPropertyChangedEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onLayoutPropertyChangedEvent_LayoutElementPropertyChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent", new AbstractCDIEventCallback<LayoutElementClearAllPropertiesEvent>() {
      public void fireEvent(final LayoutElementClearAllPropertiesEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onClearAllPropertiesEvent_LayoutElementClearAllPropertiesEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent", new AbstractCDIEventCallback<LayoutEditorElementSelectEvent>() {
      public void fireEvent(final LayoutEditorElementSelectEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onLayoutElementSelected_LayoutEditorElementSelectEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.api.ComponentDropEvent", new AbstractCDIEventCallback<ComponentDropEvent>() {
      public void fireEvent(final ComponentDropEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onComponentDropped_ComponentDropEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.api.ComponentDropEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent", new AbstractCDIEventCallback<ComponentRemovedEvent>() {
      public void fireEvent(final ComponentRemovedEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onComponentRemoved_ComponentRemovedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent", new AbstractCDIEventCallback<RowDnDEvent>() {
      public void fireEvent(final RowDnDEvent event) {
        final LayoutEditorPropertiesPresenter instance = Factory.maybeUnwrapProxy((LayoutEditorPropertiesPresenter) context.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default"));
        LayoutEditorPropertiesPresenter_onRowsSwap_RowDnDEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent []";
      }
    });
  }

  public LayoutEditorPropertiesPresenter createInstance(final ContextManager contextManager) {
    final LiveSearchDropDown _elementSelector_2 = (LiveSearchDropDown) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (LayoutEditorPropertiesView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<LayoutElementPropertiesPresenter> _layoutElementPropertiesPresenterInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LayoutElementPropertiesPresenter.class }, new Annotation[] { });
    final LayoutEditorPropertiesPresenter instance = new LayoutEditorPropertiesPresenter(_view_0, _layoutElementPropertiesPresenterInstance_1, _elementSelector_2);
    registerDependentScopedReference(instance, _elementSelector_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _layoutElementPropertiesPresenterInstance_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final LayoutEditorPropertiesPresenter instance) {
    LayoutEditorPropertiesPresenter_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutEditorPropertiesPresenter> proxyImpl = new Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void LayoutEditorPropertiesPresenter_onClearAllPropertiesEvent_LayoutElementClearAllPropertiesEvent(LayoutEditorPropertiesPresenter instance, LayoutElementClearAllPropertiesEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onClearAllPropertiesEvent(Lorg/uberfire/ext/layout/editor/client/event/LayoutElementClearAllPropertiesEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_onRowsSwap_RowDnDEvent(LayoutEditorPropertiesPresenter instance, RowDnDEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onRowsSwap(Lorg/uberfire/ext/layout/editor/client/components/rows/RowDnDEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_onComponentDropped_ComponentDropEvent(LayoutEditorPropertiesPresenter instance, ComponentDropEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onComponentDropped(Lorg/uberfire/ext/layout/editor/client/api/ComponentDropEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_init(LayoutEditorPropertiesPresenter instance) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::init()();
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_onLayoutPropertyChangedEvent_LayoutElementPropertyChangedEvent(LayoutEditorPropertiesPresenter instance, LayoutElementPropertyChangedEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onLayoutPropertyChangedEvent(Lorg/uberfire/ext/layout/editor/client/event/LayoutElementPropertyChangedEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_fillElementParts_LayoutEditorElement(LayoutEditorPropertiesPresenter instance, LayoutEditorElement a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::fillElementParts(Lorg/uberfire/ext/layout/editor/client/api/LayoutEditorElement;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_onComponentRemoved_ComponentRemovedEvent(LayoutEditorPropertiesPresenter instance, ComponentRemovedEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onComponentRemoved(Lorg/uberfire/ext/layout/editor/client/api/ComponentRemovedEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPropertiesPresenter_onLayoutElementSelected_LayoutEditorElementSelectEvent(LayoutEditorPropertiesPresenter instance, LayoutEditorElementSelectEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter::onLayoutElementSelected(Lorg/uberfire/ext/layout/editor/client/event/LayoutEditorElementSelectEvent;)(a0);
  }-*/;
}