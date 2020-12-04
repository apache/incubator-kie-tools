package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm.View;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferenceForm> { public Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPreferenceForm.class, "Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPreferenceForm.class, Object.class });
  }

  public DefaultPreferenceForm createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DefaultPreferenceFormView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPreferenceForm instance = new DefaultPreferenceForm(_view_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "hierarchyItemFormInitializationEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent", new AbstractCDIEventCallback<HierarchyItemFormInitializationEvent>() {
      public void fireEvent(final HierarchyItemFormInitializationEvent event) {
        instance.hierarchyItemFormInitializationEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent []";
      }
    }));
    thisInstance.setReference(instance, "propertyChangedSubscription", CDI.subscribeLocal("org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent", new AbstractCDIEventCallback<PropertyEditorChangeEvent>() {
      public void fireEvent(final PropertyEditorChangeEvent event) {
        instance.propertyChanged(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent []";
      }
    }));
    thisInstance.setReference(instance, "saveEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent", new AbstractCDIEventCallback<PreferencesCentralSaveEvent>() {
      public void fireEvent(final PreferencesCentralSaveEvent event) {
        instance.saveEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent []";
      }
    }));
    thisInstance.setReference(instance, "undoEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent", new AbstractCDIEventCallback<PreferencesCentralUndoChangesEvent>() {
      public void fireEvent(final PreferencesCentralUndoChangesEvent event) {
        instance.undoEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultPreferenceForm) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultPreferenceForm instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "hierarchyItemFormInitializationEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "propertyChangedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "saveEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "undoEventSubscription", Subscription.class)).remove();
  }
}