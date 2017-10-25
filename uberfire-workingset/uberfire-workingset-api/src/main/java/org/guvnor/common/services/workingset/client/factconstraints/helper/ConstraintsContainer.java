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

package org.guvnor.common.services.workingset.client.factconstraints.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.workingset.client.factconstraints.ConstraintConfiguration;
import org.guvnor.common.services.workingset.client.factconstraints.config.SimpleConstraintConfigurationImpl;

public class ConstraintsContainer {

    private static final Map<String, ConstraintConfiguration> constraintConfigs = new HashMap<String, ConstraintConfiguration>();

    static {
        ConstraintConfiguration config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("NotNull");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("IntegerConstraint");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("RangeConstraint");
        config.setArgumentValue("Min.value",
                                "0");
        config.setArgumentValue("Max.value",
                                "0");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("NotMatches");
        config.setArgumentValue("matches",
                                "");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("Matches");
        config.setArgumentValue("matches",
                                "");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("IvalidFieldConstraint");
        constraintConfigs.put(config.getConstraintName(),
                              config);

        config = new SimpleConstraintConfigurationImpl();
        config.setConstraintName("MandatoryFieldConstraint");
        constraintConfigs.put(config.getConstraintName(),
                              config);
    }

    private Map<String, List<ConstraintConfiguration>> constraints = new HashMap<String, List<ConstraintConfiguration>>();

    public ConstraintsContainer(ConstraintConfiguration[] constraints) {
        this(Arrays.asList(constraints));
    }

    public ConstraintsContainer(Collection<ConstraintConfiguration> constraints) {
        if (constraints != null && !constraints.isEmpty()) {
            for (ConstraintConfiguration c : constraints) {
                addConstraint(c);
            }
        }
    }

    public ConstraintsContainer() {

    }

    public List<ConstraintConfiguration> removeConstraint(ConstraintConfiguration c) {
        List<ConstraintConfiguration> list = constraints.get(c.getFactType());
        if (list != null) {
            list.remove(c);
        }
        return list;
    }

    public void addConstraint(ConstraintConfiguration c) {
        List<ConstraintConfiguration> list = constraints.get(c.getFactType());
        if (list == null) {
            list = new LinkedList<ConstraintConfiguration>();
            constraints.put(c.getFactType(),
                            list);
        }
        list.add(c);
    }

    public List<ConstraintConfiguration> getConstraints(String factType) {
        return Collections.unmodifiableList(constraints.get(factType));
    }

    public List<ConstraintConfiguration> getConstraints(String factType,
                                                        String fieldName) {

        List<ConstraintConfiguration> list = constraints.get(factType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<ConstraintConfiguration> res = new LinkedList<ConstraintConfiguration>();
        for (ConstraintConfiguration c : list) {
            if (fieldName.equals(c.getFieldName())) {
                res.add(c);
            }
        }
        return res;
    }

    public boolean hasConstraints(String FactType) {
        return constraints.containsKey(FactType);
    }

    public static Map<String, ConstraintConfiguration> getAllConfigurations() {
        return constraintConfigs;
    }

    public static ConstraintConfiguration getEmptyConfiguration(String constraintName) {
        return copyConfig(getAllConfigurations().get(constraintName));
    }

    private static ConstraintConfiguration copyConfig(ConstraintConfiguration constraintConfiguration) {
        return new SimpleConstraintConfigurationImpl(constraintConfiguration);
    }
}
