package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructurePresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView;
import org.uberfire.ext.preferences.client.central.tree.TreeView;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView extends Factory<TreeHierarchyStructurePresenter> { public Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView() {
    super(new FactoryHandleImpl(TreeHierarchyStructurePresenter.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructurePresenter__quals__j_e_i_Any_o_u_e_p_c_c_t_TreeView", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyStructurePresenter.class, Object.class, HierarchyStructurePresenter.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new TreeView() {
        public Class annotationType() {
          return TreeView.class;
        }
        public String toString() {
          return "@org.uberfire.ext.preferences.client.central.tree.TreeView()";
        }
    } });
  }

  public TreeHierarchyStructurePresenter createInstance(final ContextManager contextManager) {
    final PreferenceFormBeansInfo _preferenceFormBeansInfo_8 = (PreferenceFormBeansInfo) contextManager.getInstance("Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<TreeHierarchyInternalItemPresenter> _treeHierarchyInternalItemPresenterProvider_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeHierarchyInternalItemPresenter.class }, new Annotation[] { });
    final PlaceManager _placeManager_5 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<TreeHierarchyLeafItemPresenter> _treeHierarchyLeafItemPresenterProvider_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeHierarchyLeafItemPresenter.class }, new Annotation[] { });
    final Caller<PreferenceBeanServerStore> _preferenceBeanServerStoreCaller_1 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { PreferenceBeanServerStore.class }, new Annotation[] { });
    final Event<NotificationEvent> _notification_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final View _view_0 = (TreeHierarchyStructureView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default");
    final Event<HierarchyItemFormInitializationEvent> _hierarchyItemFormInitializationEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { HierarchyItemFormInitializationEvent.class }, new Annotation[] { });
    final PreferenceBeanStore _store_6 = (PreferenceBeanStoreClientImpl) contextManager.getInstance("Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default");
    final TreeHierarchyStructurePresenter instance = new TreeHierarchyStructurePresenter(_view_0, _preferenceBeanServerStoreCaller_1, _treeHierarchyInternalItemPresenterProvider_2, _treeHierarchyLeafItemPresenterProvider_3, _hierarchyItemFormInitializationEvent_4, _placeManager_5, _store_6, _notification_7, _preferenceFormBeansInfo_8);
    registerDependentScopedReference(instance, _treeHierarchyInternalItemPresenterProvider_2);
    registerDependentScopedReference(instance, _treeHierarchyLeafItemPresenterProvider_3);
    registerDependentScopedReference(instance, _preferenceBeanServerStoreCaller_1);
    registerDependentScopedReference(instance, _notification_7);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _hierarchyItemFormInitializationEvent_4);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "saveEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent", new AbstractCDIEventCallback<PreferencesCentralSaveEvent>() {
      public void fireEvent(final PreferencesCentralSaveEvent event) {
        instance.saveEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyStructurePresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyStructurePresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "saveEventSubscription", Subscription.class)).remove();
  }
}