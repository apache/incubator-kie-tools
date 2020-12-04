package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn.View;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnPart;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

public class Type_factory__o_u_e_l_e_c_c_c_ComponentColumn__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumn> { public Type_factory__o_u_e_l_e_c_c_c_ComponentColumn__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ComponentColumn.class, "Type_factory__o_u_e_l_e_c_c_c_ComponentColumn__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ComponentColumn.class, Object.class, Column.class, LayoutEditorElement.class, LayoutElementWithProperties.class });
  }

  public ComponentColumn createInstance(final ContextManager contextManager) {
    final Event<LayoutEditorElementSelectEvent> _columnSelectEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementSelectEvent.class }, new Annotation[] { });
    final Event<LockRequiredEvent> _lockRequiredEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final DnDManager _dndManager_1 = (DnDManager) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<ColumnResizeEvent> _columnResizeEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ColumnResizeEvent.class }, new Annotation[] { });
    final LayoutDragComponentHelper _layoutDragComponentHelper_2 = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ComponentColumnPart> _componentColumnManagedInstance_7 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ComponentColumnPart.class }, new Annotation[] { });
    final Event<LayoutEditorElementUnselectEvent> _columnUnselectEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementUnselectEvent.class }, new Annotation[] { });
    final View _view_0 = (ComponentColumnView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default");
    final ComponentColumn instance = new ComponentColumn(_view_0, _dndManager_1, _layoutDragComponentHelper_2, _columnResizeEvent_3, _columnSelectEvent_4, _columnUnselectEvent_5, _lockRequiredEvent_6, _componentColumnManagedInstance_7);
    registerDependentScopedReference(instance, _columnSelectEvent_4);
    registerDependentScopedReference(instance, _lockRequiredEvent_6);
    registerDependentScopedReference(instance, _columnResizeEvent_3);
    registerDependentScopedReference(instance, _layoutDragComponentHelper_2);
    registerDependentScopedReference(instance, _componentColumnManagedInstance_7);
    registerDependentScopedReference(instance, _columnUnselectEvent_5);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onDragEndSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent", new AbstractCDIEventCallback<DragComponentEndEvent>() {
      public void fireEvent(final DragComponentEndEvent event) {
        instance.onDragEnd(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ComponentColumn) instance, contextManager);
  }

  public void destroyInstanceHelper(final ComponentColumn instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onDragEndSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final ComponentColumn instance) {
    instance.post();
  }
}