package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.jsbridge.client.editor.JsWorkbenchEditorActivity;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader.Shadowed;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_j_c_e_JsWorkbenchEditorActivity__quals__j_e_i_Any_o_u_j_c_l_AppFormerJsActivityLoader_Shadowed extends Factory<JsWorkbenchEditorActivity> { public Type_factory__o_u_j_c_e_JsWorkbenchEditorActivity__quals__j_e_i_Any_o_u_j_c_l_AppFormerJsActivityLoader_Shadowed() {
    super(new FactoryHandleImpl(JsWorkbenchEditorActivity.class, "Type_factory__o_u_j_c_e_JsWorkbenchEditorActivity__quals__j_e_i_Any_o_u_j_c_l_AppFormerJsActivityLoader_Shadowed", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JsWorkbenchEditorActivity.class, AbstractWorkbenchEditorActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchEditorActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new Shadowed() {
        public Class annotationType() {
          return Shadowed.class;
        }
        public String toString() {
          return "@org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader.Shadowed()";
        }
    } });
  }

  public JsWorkbenchEditorActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final JsWorkbenchEditorActivity instance = new JsWorkbenchEditorActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final Instance AbstractWorkbenchEditorActivity_lockManagerProvider = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { LockManager.class }, new Annotation[] { });
    registerDependentScopedReference(instance, AbstractWorkbenchEditorActivity_lockManagerProvider);
    AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(instance, AbstractWorkbenchEditorActivity_lockManagerProvider);
    setIncompleteInstance(null);
    return instance;
  }

  native static Instance AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(AbstractWorkbenchEditorActivity instance) /*-{
    return instance.@org.uberfire.client.mvp.AbstractWorkbenchEditorActivity::lockManagerProvider;
  }-*/;

  native static void AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(AbstractWorkbenchEditorActivity instance, Instance<LockManager> value) /*-{
    instance.@org.uberfire.client.mvp.AbstractWorkbenchEditorActivity::lockManagerProvider = value;
  }-*/;
}