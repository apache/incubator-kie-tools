package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;

public class Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default extends Factory<DragHelperComponentColumn> { public Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DragHelperComponentColumn.class, "Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DragHelperComponentColumn.class, Object.class });
  }

  public DragHelperComponentColumn createInstance(final ContextManager contextManager) {
    final DragHelperComponentColumn instance = new DragHelperComponentColumn();
    setIncompleteInstance(instance);
    final LayoutDragComponentHelper DragHelperComponentColumn_dragHelper = (LayoutDragComponentHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutDragComponentHelper__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DragHelperComponentColumn_dragHelper);
    DragHelperComponentColumn_LayoutDragComponentHelper_dragHelper(instance, DragHelperComponentColumn_dragHelper);
    final LayoutEditorCssHelper DragHelperComponentColumn_layoutCssHelper = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    DragHelperComponentColumn_LayoutEditorCssHelper_layoutCssHelper(instance, DragHelperComponentColumn_layoutCssHelper);
    final Document DragHelperComponentColumn_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DragHelperComponentColumn_document);
    DragHelperComponentColumn_Document_document(instance, DragHelperComponentColumn_document);
    setIncompleteInstance(null);
    return instance;
  }

  native static LayoutDragComponentHelper DragHelperComponentColumn_LayoutDragComponentHelper_dragHelper(DragHelperComponentColumn instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::dragHelper;
  }-*/;

  native static void DragHelperComponentColumn_LayoutDragComponentHelper_dragHelper(DragHelperComponentColumn instance, LayoutDragComponentHelper value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::dragHelper = value;
  }-*/;

  native static LayoutEditorCssHelper DragHelperComponentColumn_LayoutEditorCssHelper_layoutCssHelper(DragHelperComponentColumn instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::layoutCssHelper;
  }-*/;

  native static void DragHelperComponentColumn_LayoutEditorCssHelper_layoutCssHelper(DragHelperComponentColumn instance, LayoutEditorCssHelper value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::layoutCssHelper = value;
  }-*/;

  native static Document DragHelperComponentColumn_Document_document(DragHelperComponentColumn instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::document;
  }-*/;

  native static void DragHelperComponentColumn_Document_document(DragHelperComponentColumn instance, Document value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn::document = value;
  }-*/;
}