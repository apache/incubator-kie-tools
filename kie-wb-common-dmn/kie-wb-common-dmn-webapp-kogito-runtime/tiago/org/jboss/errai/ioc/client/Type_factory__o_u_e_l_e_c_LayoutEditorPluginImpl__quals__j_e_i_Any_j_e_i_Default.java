package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;

public class Type_factory__o_u_e_l_e_c_LayoutEditorPluginImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPluginImpl> { public Type_factory__o_u_e_l_e_c_LayoutEditorPluginImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorPluginImpl.class, "Type_factory__o_u_e_l_e_c_LayoutEditorPluginImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorPluginImpl.class, Object.class, LayoutEditorPlugin.class, LayoutEditor.class });
  }

  public LayoutEditorPluginImpl createInstance(final ContextManager contextManager) {
    final LayoutEditorPluginImpl instance = new LayoutEditorPluginImpl();
    setIncompleteInstance(instance);
    final Caller LayoutEditorPluginImpl_perspectiveServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { PerspectiveServices.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LayoutEditorPluginImpl_perspectiveServices);
    LayoutEditorPluginImpl_Caller_perspectiveServices(instance, LayoutEditorPluginImpl_perspectiveServices);
    final SavePopUpPresenter LayoutEditorPluginImpl_savePopUpPresenter = (SavePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_SavePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LayoutEditorPluginImpl_savePopUpPresenter);
    LayoutEditorPluginImpl_SavePopUpPresenter_savePopUpPresenter(instance, LayoutEditorPluginImpl_savePopUpPresenter);
    final LayoutEditorPresenter LayoutEditorPluginImpl_layoutEditorPresenter = (LayoutEditorPresenter) contextManager.getInstance("Type_factory__o_u_e_l_e_c_LayoutEditorPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LayoutEditorPluginImpl_layoutEditorPresenter);
    LayoutEditorPluginImpl_LayoutEditorPresenter_layoutEditorPresenter(instance, LayoutEditorPluginImpl_layoutEditorPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final LayoutEditorPluginImpl instance) {
    instance.setup();
  }

  native static SavePopUpPresenter LayoutEditorPluginImpl_SavePopUpPresenter_savePopUpPresenter(LayoutEditorPluginImpl instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::savePopUpPresenter;
  }-*/;

  native static void LayoutEditorPluginImpl_SavePopUpPresenter_savePopUpPresenter(LayoutEditorPluginImpl instance, SavePopUpPresenter value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::savePopUpPresenter = value;
  }-*/;

  native static Caller LayoutEditorPluginImpl_Caller_perspectiveServices(LayoutEditorPluginImpl instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::perspectiveServices;
  }-*/;

  native static void LayoutEditorPluginImpl_Caller_perspectiveServices(LayoutEditorPluginImpl instance, Caller<PerspectiveServices> value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::perspectiveServices = value;
  }-*/;

  native static LayoutEditorPresenter LayoutEditorPluginImpl_LayoutEditorPresenter_layoutEditorPresenter(LayoutEditorPluginImpl instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::layoutEditorPresenter;
  }-*/;

  native static void LayoutEditorPluginImpl_LayoutEditorPresenter_layoutEditorPresenter(LayoutEditorPluginImpl instance, LayoutEditorPresenter value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorPluginImpl::layoutEditorPresenter = value;
  }-*/;
}