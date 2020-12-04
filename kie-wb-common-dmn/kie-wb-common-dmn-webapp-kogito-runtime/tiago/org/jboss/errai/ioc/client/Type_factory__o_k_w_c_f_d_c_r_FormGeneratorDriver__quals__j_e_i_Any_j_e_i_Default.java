package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormGeneratorDriver;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.generator.LayoutGeneratorDriver;

public class Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGeneratorDriver> { public Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormGeneratorDriver.class, "Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormGeneratorDriver.class, Object.class, LayoutGeneratorDriver.class });
  }

  public FormGeneratorDriver createInstance(final ContextManager contextManager) {
    final FormsElementWrapperWidgetUtil _wrapperWidgetUtil_2 = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    final Document _document_3 = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<LayoutDragComponent> _instance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LayoutDragComponent.class }, new Annotation[] { });
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final FormGeneratorDriver instance = new FormGeneratorDriver(_beanManager_0, _instance_1, _wrapperWidgetUtil_2, _document_3);
    registerDependentScopedReference(instance, _document_3);
    registerDependentScopedReference(instance, _instance_1);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}