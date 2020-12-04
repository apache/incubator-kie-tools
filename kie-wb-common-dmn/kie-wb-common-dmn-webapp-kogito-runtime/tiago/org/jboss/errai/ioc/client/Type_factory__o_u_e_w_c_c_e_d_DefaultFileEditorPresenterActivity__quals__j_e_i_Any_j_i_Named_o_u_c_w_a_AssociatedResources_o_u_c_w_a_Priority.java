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
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenterActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<DefaultFileEditorPresenterActivity> { public Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority() {
    super(new FactoryHandleImpl(DefaultFileEditorPresenterActivity.class, "Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority", Dependent.class, false, "DefaultFileEditor", true));
    handle.setAssignableTypes(new Class[] { DefaultFileEditorPresenterActivity.class, AbstractWorkbenchEditorActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchEditorActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.createNamed("DefaultFileEditor"), new AssociatedResources() {
        public Class annotationType() {
          return AssociatedResources.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.AssociatedResources(value=[class org.uberfire.client.workbench.type.AnyResourceType])";
        }
        public Class[] value() {
          return new Class[] { AnyResourceType.class };
        }
      }, new Priority() {
        public Class annotationType() {
          return Priority.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.Priority(value=-2147483648)";
        }
        public int value() {
          return -2147483648;
        }
    } });
  }

  public DefaultFileEditorPresenterActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefaultFileEditorPresenterActivity instance = new DefaultFileEditorPresenterActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DefaultFileEditorPresenter DefaultFileEditorPresenterActivity_realPresenter = (DefaultFileEditorPresenter) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_d_DefaultFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultFileEditorPresenterActivity_realPresenter);
    DefaultFileEditorPresenterActivity_DefaultFileEditorPresenter_realPresenter(instance, DefaultFileEditorPresenterActivity_realPresenter);
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

  native static DefaultFileEditorPresenter DefaultFileEditorPresenterActivity_DefaultFileEditorPresenter_realPresenter(DefaultFileEditorPresenterActivity instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenterActivity::realPresenter;
  }-*/;

  native static void DefaultFileEditorPresenterActivity_DefaultFileEditorPresenter_realPresenter(DefaultFileEditorPresenterActivity instance, DefaultFileEditorPresenter value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenterActivity::realPresenter = value;
  }-*/;
}