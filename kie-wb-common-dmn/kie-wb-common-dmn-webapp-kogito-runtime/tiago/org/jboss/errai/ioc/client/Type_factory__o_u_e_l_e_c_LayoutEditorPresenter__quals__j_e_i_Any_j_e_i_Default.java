package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter.View;
import org.uberfire.ext.layout.editor.client.LayoutEditorView;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGenerator;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;

public class Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPresenter> { public Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorPresenter.class, "Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorPresenter.class, Object.class });
  }

  public LayoutEditorPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (LayoutEditorView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default");
    final Container _container_1 = (Container) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default");
    final LayoutGenerator _layoutGenerator_2 = (BootstrapLayoutGenerator) contextManager.getInstance("Type_factory__o_u_e_l_e_c_g_BootstrapLayoutGenerator__quals__j_e_i_Any_j_e_i_Default");
    final LayoutEditorPresenter instance = new LayoutEditorPresenter(_view_0, _container_1, _layoutGenerator_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _container_1);
    registerDependentScopedReference(instance, _layoutGenerator_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onLayoutPropertyChangedEventSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent", new AbstractCDIEventCallback<LayoutElementPropertyChangedEvent>() {
      public void fireEvent(final LayoutElementPropertyChangedEvent event) {
        LayoutEditorPresenter_onLayoutPropertyChangedEvent_LayoutElementPropertyChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onClearAllPropertiesEventSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent", new AbstractCDIEventCallback<LayoutElementClearAllPropertiesEvent>() {
      public void fireEvent(final LayoutElementClearAllPropertiesEvent event) {
        LayoutEditorPresenter_onClearAllPropertiesEvent_LayoutElementClearAllPropertiesEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutEditorPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutEditorPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onLayoutPropertyChangedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onClearAllPropertiesEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final LayoutEditorPresenter instance) {
    instance.initNew();
  }

  public native static void LayoutEditorPresenter_onClearAllPropertiesEvent_LayoutElementClearAllPropertiesEvent(LayoutEditorPresenter instance, LayoutElementClearAllPropertiesEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPresenter::onClearAllPropertiesEvent(Lorg/uberfire/ext/layout/editor/client/event/LayoutElementClearAllPropertiesEvent;)(a0);
  }-*/;

  public native static void LayoutEditorPresenter_onLayoutPropertyChangedEvent_LayoutElementPropertyChangedEvent(LayoutEditorPresenter instance, LayoutElementPropertyChangedEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPresenter::onLayoutPropertyChangedEvent(Lorg/uberfire/ext/layout/editor/client/event/LayoutElementPropertyChangedEvent;)(a0);
  }-*/;
}