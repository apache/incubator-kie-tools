package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.container.Container.View;
import org.uberfire.ext.layout.editor.client.components.container.ContainerView;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController;
import org.uberfire.ext.layout.editor.client.infra.RowResizeEvent;

public class Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default extends Factory<Container> { public Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Container.class, "Type_factory__o_u_e_l_e_c_c_c_Container__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Container.class, Object.class, LayoutEditorElement.class, LayoutElementWithProperties.class });
  }

  public Container createInstance(final ContextManager contextManager) {
    final Event<LockRequiredEvent> _lockRequiredEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final Event<ComponentDropEvent> _componentDropEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ComponentDropEvent.class }, new Annotation[] { });
    final Instance<EmptyDropRow> _emptyDropRowInstance_3 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { EmptyDropRow.class }, new Annotation[] { });
    final Event<LayoutEditorElementUnselectEvent> _containerUnselectEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementUnselectEvent.class }, new Annotation[] { });
    final DnDManager _dndManager_8 = (DnDManager) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (ContainerView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default");
    final Instance<Row> _rowInstance_2 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { Row.class }, new Annotation[] { });
    final Event<LayoutEditorElementSelectEvent> _containerSelectEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementSelectEvent.class }, new Annotation[] { });
    final LayoutEditorCssHelper _layoutCssHelper_1 = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    final LayoutEditorFocusController _layoutEditorFocusController_9 = (LayoutEditorFocusController) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default");
    final Container instance = new Container(_view_0, _layoutCssHelper_1, _rowInstance_2, _emptyDropRowInstance_3, _componentDropEvent_4, _containerSelectEvent_5, _containerUnselectEvent_6, _lockRequiredEvent_7, _dndManager_8, _layoutEditorFocusController_9);
    registerDependentScopedReference(instance, _lockRequiredEvent_7);
    registerDependentScopedReference(instance, _componentDropEvent_4);
    registerDependentScopedReference(instance, _emptyDropRowInstance_3);
    registerDependentScopedReference(instance, _containerUnselectEvent_6);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _rowInstance_2);
    registerDependentScopedReference(instance, _containerSelectEvent_5);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "swapRowsSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent", new AbstractCDIEventCallback<RowDnDEvent>() {
      public void fireEvent(final RowDnDEvent event) {
        Container_swapRows_RowDnDEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent []";
      }
    }));
    thisInstance.setReference(instance, "lookForBeginningRowSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent", new AbstractCDIEventCallback<RowDnDEvent>() {
      public void fireEvent(final RowDnDEvent event) {
        Container_lookForBeginningRow_RowDnDEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent []";
      }
    }));
    thisInstance.setReference(instance, "resizeRowsSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.RowResizeEvent", new AbstractCDIEventCallback<RowResizeEvent>() {
      public void fireEvent(final RowResizeEvent event) {
        instance.resizeRows(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.RowResizeEvent []";
      }
    }));
    thisInstance.setReference(instance, "resizeEventIsinThisContainerSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.RowResizeEvent", new AbstractCDIEventCallback<RowResizeEvent>() {
      public void fireEvent(final RowResizeEvent event) {
        Container_resizeEventIsinThisContainer_RowResizeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.RowResizeEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((Container) instance, contextManager);
  }

  public void destroyInstanceHelper(final Container instance, final ContextManager contextManager) {
    instance.preDestroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "swapRowsSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "lookForBeginningRowSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeRowsSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeEventIsinThisContainerSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final Container instance) {
    instance.setup();
  }

  public native static Row Container_lookForBeginningRow_RowDnDEvent(Container instance, RowDnDEvent a0) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.Container::lookForBeginningRow(Lorg/uberfire/ext/layout/editor/client/components/rows/RowDnDEvent;)(a0);
  }-*/;

  public native static boolean Container_resizeEventIsinThisContainer_RowResizeEvent(Container instance, RowResizeEvent a0) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.Container::resizeEventIsinThisContainer(Lorg/uberfire/ext/layout/editor/client/infra/RowResizeEvent;)(a0);
  }-*/;

  public native static void Container_swapRows_RowDnDEvent(Container instance, RowDnDEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.Container::swapRows(Lorg/uberfire/ext/layout/editor/client/components/rows/RowDnDEvent;)(a0);
  }-*/;
}