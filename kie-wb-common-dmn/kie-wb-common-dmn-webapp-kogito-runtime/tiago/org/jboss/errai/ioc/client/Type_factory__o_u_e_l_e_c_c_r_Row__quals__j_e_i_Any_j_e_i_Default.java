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
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.Row.View;
import org.uberfire.ext.layout.editor.client.components.rows.RowView;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementUnselectEvent;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController;
import org.uberfire.ext.layout.editor.client.infra.RowResizeEvent;

public class Type_factory__o_u_e_l_e_c_c_r_Row__quals__j_e_i_Any_j_e_i_Default extends Factory<Row> { public Type_factory__o_u_e_l_e_c_c_r_Row__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Row.class, "Type_factory__o_u_e_l_e_c_c_r_Row__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Row.class, Object.class, LayoutEditorElement.class, LayoutElementWithProperties.class });
  }

  public Row createInstance(final ContextManager contextManager) {
    final LayoutEditorCssHelper _layoutCssHelper_5 = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    final DnDManager _dndManager_3 = (DnDManager) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default");
    final Instance<ColumnWithComponents> _columnWithComponentsInstance_2 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ColumnWithComponents.class }, new Annotation[] { });
    final Event<ComponentRemovedEvent> _componentRemovedEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ComponentRemovedEvent.class }, new Annotation[] { });
    final Instance<ComponentColumn> _columnInstance_1 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ComponentColumn.class }, new Annotation[] { });
    final Event<LayoutEditorElementSelectEvent> _rowSelectEvent_9 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementSelectEvent.class }, new Annotation[] { });
    final LayoutDragComponentHelper _layoutDragComponentHelper_4 = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    final Event<RowResizeEvent> _rowResizeEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RowResizeEvent.class }, new Annotation[] { });
    final View _view_0 = (RowView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default");
    final Event<ComponentDropEvent> _componentDropEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ComponentDropEvent.class }, new Annotation[] { });
    final Event<LayoutEditorElementUnselectEvent> _rowUnselectEvent_10 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LayoutEditorElementUnselectEvent.class }, new Annotation[] { });
    final LayoutEditorFocusController _layoutEditorFocusController_11 = (LayoutEditorFocusController) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default");
    final Row instance = new Row(_view_0, _columnInstance_1, _columnWithComponentsInstance_2, _dndManager_3, _layoutDragComponentHelper_4, _layoutCssHelper_5, _componentDropEvent_6, _componentRemovedEvent_7, _rowResizeEvent_8, _rowSelectEvent_9, _rowUnselectEvent_10, _layoutEditorFocusController_11);
    registerDependentScopedReference(instance, _columnWithComponentsInstance_2);
    registerDependentScopedReference(instance, _componentRemovedEvent_7);
    registerDependentScopedReference(instance, _columnInstance_1);
    registerDependentScopedReference(instance, _rowSelectEvent_9);
    registerDependentScopedReference(instance, _layoutDragComponentHelper_4);
    registerDependentScopedReference(instance, _rowResizeEvent_8);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _componentDropEvent_6);
    registerDependentScopedReference(instance, _rowUnselectEvent_10);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "resizeColumnsSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent", new AbstractCDIEventCallback<ColumnResizeEvent>() {
      public void fireEvent(final ColumnResizeEvent event) {
        instance.resizeColumns(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent []";
      }
    }));
    thisInstance.setReference(instance, "resizeEventIsinThisRowSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent", new AbstractCDIEventCallback<ColumnResizeEvent>() {
      public void fireEvent(final ColumnResizeEvent event) {
        Row_resizeEventIsinThisRow_ColumnResizeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((Row) instance, contextManager);
  }

  public void destroyInstanceHelper(final Row instance, final ContextManager contextManager) {
    instance.preDestroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeColumnsSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeEventIsinThisRowSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final Row instance) {
    instance.post();
  }

  public native static boolean Row_resizeEventIsinThisRow_ColumnResizeEvent(Row instance, ColumnResizeEvent a0) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.Row::resizeEventIsinThisRow(Lorg/uberfire/ext/layout/editor/client/infra/ColumnResizeEvent;)(a0);
  }-*/;
}