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
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenterActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<MetaFileEditorPresenterActivity> { public Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority() {
    super(new FactoryHandleImpl(MetaFileEditorPresenterActivity.class, "Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenterActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority", Dependent.class, false, "MetaFileTextEditor", true));
    handle.setAssignableTypes(new Class[] { MetaFileEditorPresenterActivity.class, AbstractWorkbenchEditorActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchEditorActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.createNamed("MetaFileTextEditor"), new AssociatedResources() {
        public Class annotationType() {
          return AssociatedResources.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.AssociatedResources(value=[class org.uberfire.client.workbench.type.DotResourceType])";
        }
        public Class[] value() {
          return new Class[] { DotResourceType.class };
        }
      }, new Priority() {
        public Class annotationType() {
          return Priority.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.Priority(value=2147483547)";
        }
        public int value() {
          return 2147483547;
        }
    } });
  }

  public MetaFileEditorPresenterActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final MetaFileEditorPresenterActivity instance = new MetaFileEditorPresenterActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final MetaFileEditorPresenter MetaFileEditorPresenterActivity_realPresenter = (MetaFileEditorPresenter) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_m_MetaFileEditorPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MetaFileEditorPresenterActivity_realPresenter);
    MetaFileEditorPresenterActivity_MetaFileEditorPresenter_realPresenter(instance, MetaFileEditorPresenterActivity_realPresenter);
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

  native static MetaFileEditorPresenter MetaFileEditorPresenterActivity_MetaFileEditorPresenter_realPresenter(MetaFileEditorPresenterActivity instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenterActivity::realPresenter;
  }-*/;

  native static void MetaFileEditorPresenterActivity_MetaFileEditorPresenter_realPresenter(MetaFileEditorPresenterActivity instance, MetaFileEditorPresenter value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.metafile.MetaFileEditorPresenterActivity::realPresenter = value;
  }-*/;
}