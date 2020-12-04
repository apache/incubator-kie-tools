package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;

public class Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MetaFileEditorPresenter> { public Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MetaFileEditorPresenter.class, "Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MetaFileEditorPresenter.class, Object.class });
  }

  public MetaFileEditorPresenter createInstance(final ContextManager contextManager) {
    final MetaFileEditorPresenter instance = new MetaFileEditorPresenter();
    setIncompleteInstance(instance);
    final TextEditorView MetaFileEditorPresenter_view = (TextEditorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MetaFileEditorPresenter_view);
    instance.view = MetaFileEditorPresenter_view;
    final Caller MetaFileEditorPresenter_vfsServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MetaFileEditorPresenter_vfsServices);
    MetaFileEditorPresenter_Caller_vfsServices(instance, MetaFileEditorPresenter_vfsServices);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller MetaFileEditorPresenter_Caller_vfsServices(MetaFileEditorPresenter instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenter::vfsServices;
  }-*/;

  native static void MetaFileEditorPresenter_Caller_vfsServices(MetaFileEditorPresenter instance, Caller<VFSService> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenter::vfsServices = value;
  }-*/;
}