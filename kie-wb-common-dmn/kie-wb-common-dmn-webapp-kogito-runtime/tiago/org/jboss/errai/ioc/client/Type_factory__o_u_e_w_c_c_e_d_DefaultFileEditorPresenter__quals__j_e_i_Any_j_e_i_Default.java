package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView;

public class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultFileEditorPresenter> { public Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultFileEditorPresenter.class, "Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultFileEditorPresenter.class, Object.class });
  }

  public DefaultFileEditorPresenter createInstance(final ContextManager contextManager) {
    final DefaultFileEditorPresenter instance = new DefaultFileEditorPresenter();
    setIncompleteInstance(instance);
    final Caller DefaultFileEditorPresenter_vfsServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, DefaultFileEditorPresenter_vfsServices);
    DefaultFileEditorPresenter_Caller_vfsServices(instance, DefaultFileEditorPresenter_vfsServices);
    final DefaultFileEditorView DefaultFileEditorPresenter_view = (DefaultFileEditorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultFileEditorPresenter_view);
    instance.view = DefaultFileEditorPresenter_view;
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller DefaultFileEditorPresenter_Caller_vfsServices(DefaultFileEditorPresenter instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter::vfsServices;
  }-*/;

  native static void DefaultFileEditorPresenter_Caller_vfsServices(DefaultFileEditorPresenter instance, Caller<VFSService> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter::vfsServices = value;
  }-*/;
}