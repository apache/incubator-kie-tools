/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.workingset.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.workingset.client.events.OnWorkingSetApplied;
import org.guvnor.common.services.workingset.client.events.OnWorkingSetDisabled;
import org.guvnor.common.services.workingset.client.factconstraints.ConstraintConfiguration;
import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;
import org.guvnor.common.services.workingset.client.factconstraints.helper.CustomFormsContainer;
import org.guvnor.common.services.workingset.client.model.WorkingSetConfigData;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.Path;

//import org.uberfire.commons.data.Pair;

@ApplicationScoped
public class WorkingSetManager {

    private Map<Path, WorkingSetSettings> projectSettings = new HashMap<Path, WorkingSetSettings>();

    @Inject
    private Caller<ModuleService<? super Module>> moduleServiceCaller;

    /**
     * This attribute should be sever side. Maybe in some FactConstraintConfig
     * object.
     */
    private boolean autoVerifierEnabled = false;

    public void onWorkingSetApplied(@Observes final OnWorkingSetApplied event) {
//        final Pair<Path, WorkingSetSettings> projectReference = getProjectConfig( event.getResource() );
//        if ( projectReference != null && projectReference.getK2() == null ) {
//            moduleService.call( new RemoteCallback<WorkingSetSettings>() {
//                @Override
//                public void callback( final WorkingSetSettings response ) {
//                    projectSettings.put( projectReference.getK1(), response );
//                }
//            } ).loadWorkingSetConfig( projectReference.getK1() );
//        }
    }

    public void onWorkingSetDisabled(@Observes final OnWorkingSetDisabled event) {
        final WorkingSetSettings settings = getActiveSettings(event.getResource());
        if (settings != null) {
            settings.removeWorkingSet(event.getWorkingSet());
        }
    }

    /**
     * Returns the active WorkingSets for a package, or null if any.
     * @param resource the resource - part of a project
     * @return the active WorkingSets for a package, or null if any.
     */
    public Collection<WorkingSetConfigData> getActiveWorkingSets(final Path resource) {
        final WorkingSetSettings result = getActiveSettings(resource);
        if (result == null) {
            return null;
        }
        return result.getConfigData();
    }

//    public Pair<Path, WorkingSetSettings> getProjectConfig( final Path resource ) {
//        final Path project = projectResources.getProject( resource );
//        if ( project == null ) {
//            return null;
//        }
//        return new Pair<Path, WorkingSetSettings>( project, projectSettings.get( project ) );
//    }

    public WorkingSetSettings getActiveSettings(final Path resource) {
//        final Path projectReference = projectResources.getProject( resource );
//        if ( projectReference == null ) {
//            return null;
//        }
//        return projectSettings.get( projectReference );
        return null;
    }

    public void removeWorkingSets(final Path resource) {
//        final Path projectReference = projectResources.getProject( resource );
//        if ( projectReference != null ) {
//            projectSettings.remove( projectReference );
//        }
    }

    /**
     * Returns whether the given (WorkingSet) RuleSet is active in a project or not.
     * @param resource the resource.
     * @param workingSet the (WorkingSet) RuleSet
     * @return whether the given (WorkingSet) RuleSet is active in a project or not.
     */
    public boolean isWorkingSetActive(final Path resource,
                                      final Path workingSet) {
        final WorkingSetSettings result = getActiveSettings(resource);
        if (result == null) {
            return false;
        }

        return result.getResources().contains(workingSet);
    }

    /**
     * Returns a Set of Constraints for a Fact Type's field. This method uses
     * the active Working Sets of the project in order to get the Constraints.
     * @param resource the resource.
     * @param factType the Fact Type (Short class name)
     * @param fieldName the field name
     * @return a Set of Constraints for a Fact Type's field.
     */
    public Set<ConstraintConfiguration> getFieldContraints(final Path resource,
                                                           final String factType,
                                                           final String fieldName) {

        final Set<ConstraintConfiguration> result = new HashSet<ConstraintConfiguration>();

        //TODO: Change this with a centralized way of Constraint Administration.
        final Collection<WorkingSetConfigData> activeConfig = this.getActiveWorkingSets(resource);
        if (activeConfig != null) {
            for (final WorkingSetConfigData configData : activeConfig) {
                final List<ConstraintConfiguration> constraints = configData.getConstraints();
                if (constraints != null) {
                    for (final ConstraintConfiguration constraint : constraints) {
                        if (constraint.getFactType().equals(factType) && constraint.getFieldName().equals(fieldName)) {
                            result.add(constraint);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * TODO: We need to store/retrieve this value from repository
     * @return
     */
    public boolean isAutoVerifierEnabled() {
        return autoVerifierEnabled;
    }

    /**
     * TODO: We need to store/retrieve this value from repository
     */
    public void setAutoVerifierEnabled(boolean autoVerifierEnabled) {
        this.autoVerifierEnabled = autoVerifierEnabled;
    }

    /**
     * Returns the associated CustomFormConfiguration for a given FactType and FieldName.
     * Because CustomFormConfiguration is stored inside a WorkingSet, the
     * packageName attribute is used to retrieve all the active WorkingSets.
     * If more than one active WorkingSet contain a CustomFormConfiguration for
     * the given FactType and FieldName the first to be found (in any specific
     * nor deterministic order) will be returned.
     * @param resource the resource (part of of a project). Used to get the active
     * working sets of the active resource project
     * @param factType The short class name of the Fact Type
     * @param fieldName The field name
     * @return the associated CustomFormConfiguration for the given FactType and
     * FieldName in the active working sets or null if any.
     */
    public CustomFormConfiguration getCustomFormConfiguration(final Path resource,
                                                              final String factType,
                                                              final String fieldName) {
        final Collection<WorkingSetConfigData> packageWorkingSets = this.getActiveWorkingSets(resource);
        if (packageWorkingSets != null) {
            final List<CustomFormConfiguration> configs = new ArrayList<CustomFormConfiguration>();
            for (final WorkingSetConfigData workingSetConfigData : packageWorkingSets) {
                if (workingSetConfigData.getCustomForms() != null && !workingSetConfigData.getCustomForms().isEmpty()) {
                    configs.addAll(workingSetConfigData.getCustomForms());
                }
            }
            final CustomFormsContainer cfc = new CustomFormsContainer(configs);

            if (cfc.containsCustomFormFor(factType,
                                          fieldName)) {
                return cfc.getCustomForm(factType,
                                         fieldName);
            }
        }

        return null;
    }
}
