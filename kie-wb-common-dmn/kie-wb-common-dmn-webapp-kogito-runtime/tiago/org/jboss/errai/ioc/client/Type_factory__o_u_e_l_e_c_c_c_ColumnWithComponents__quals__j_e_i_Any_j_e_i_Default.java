package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents.View;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

public class Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponents__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnWithComponents> { public Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponents__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ColumnWithComponents.class, "Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponents__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ColumnWithComponents.class, Object.class, Column.class, LayoutEditorElement.class, LayoutElementWithProperties.class });
  }

  public ColumnWithComponents createInstance(final ContextManager contextManager) {
    final DnDManager _dndManager_2 = (DnDManager) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_DnDManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<LockRequiredEvent> _lockRequiredEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final View _view_0 = (ColumnWithComponentsView) contextManager.getInstance("Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default");
    final Event<ColumnResizeEvent> _columnResizeEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ColumnResizeEvent.class }, new Annotation[] { });
    final Instance<Row> _rowInstance_1 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { Row.class }, new Annotation[] { });
    final LayoutDragComponentHelper _layoutDragComponentHelper_3 = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    final ColumnWithComponents instance = new ColumnWithComponents(_view_0, _rowInstance_1, _dndManager_2, _layoutDragComponentHelper_3, _columnResizeEvent_4, _lockRequiredEvent_5);
    registerDependentScopedReference(instance, _lockRequiredEvent_5);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _columnResizeEvent_4);
    registerDependentScopedReference(instance, _rowInstance_1);
    registerDependentScopedReference(instance, _layoutDragComponentHelper_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ColumnWithComponents) instance, contextManager);
  }

  public void destroyInstanceHelper(final ColumnWithComponents instance, final ContextManager contextManager) {
    instance.preDestroy();
  }

  public void invokePostConstructs(final ColumnWithComponents instance) {
    instance.post();
  }
}