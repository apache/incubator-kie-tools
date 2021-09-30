/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.kafka.metrics;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.dashbuilder.dataprovider.kafka.model.KafkaMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects the mapped simple attributes of a MBean 
 *
 */
public class MBeanMetricCollector implements KafkaMetricCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBeanMetricCollector.class);

    private static final String[] NAME_KEYS = {"name", "request", "delayedOperation"};

    private String name;
    private String[] attributes;

    public static MBeanMetricCollector metricCollector(String name) {
        return metricCollector(name, new String[0]);
    }

    public static MBeanMetricCollector metricCollector(String name, String[] attributes) {
        MBeanMetricCollector collector = new MBeanMetricCollector();
        collector.name = name;
        collector.attributes = attributes;
        return collector;
    }

    private MBeanMetricCollector() {
        // do nothing
    }

    @Override
    public List<KafkaMetric> collect(MBeanServerConnection mbsc) {
        ObjectName objectName;
        try {
            objectName = new ObjectName(this.name);
        } catch (MalformedObjectNameException e) {
            LOGGER.warn("Not able to access MBean {}", this.name);
            LOGGER.debug("Not able to access MBean", e);
            return Collections.emptyList();
        }
        if (attributes == null || attributes.length == 0) {
            attributes = readAllAttributes(mbsc, objectName);
        }
        return Arrays.stream(this.attributes)
                     .map(attr -> buildMetric(mbsc, objectName, attr))
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .collect(Collectors.toList());
    }

    private Optional<KafkaMetric> buildMetric(MBeanServerConnection mbsc, ObjectName objectName, String attrName) {
        Object attrValue = readAttribute(mbsc, objectName, attrName);
        return Optional.ofNullable(attrValue)
                       .map(attr -> KafkaMetric.from(objectName.getDomain(),
                                                     objectName.getKeyProperty("type"),
                                                     buildName(objectName),
                                                     attrName,
                                                     attr));
    }

    @Override
    public String getName() {
        return name;
    }

    private String[] readAllAttributes(MBeanServerConnection mbsc, ObjectName objectName) {
        try {
            return Arrays.stream(mbsc.getMBeanInfo(objectName).getAttributes())
                         .filter(MBeanAttributeInfo::isReadable)
                         .map(MBeanFeatureInfo::getName).toArray(String[]::new);
        } catch (Exception e) {
            LOGGER.info("Not able to read attributes for MBean {}", this.name);
            LOGGER.debug("Not able read MBean attributes", e);
            return new String[0];
        }
    }

    private String buildName(ObjectName objectName) {
        String metricName = Arrays.stream(NAME_KEYS)
                                  .map(objectName::getKeyProperty)
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.joining());

        return metricName.trim().isEmpty() ? objectName.getKeyProperty("type") : metricName;
    }

    private Object readAttribute(MBeanServerConnection mbsc, ObjectName name, String attr) {
        try {
            return mbsc.getAttribute(name, attr);
        } catch (Exception e) {
            LOGGER.info("Not able to read MBean {} attribute {}", this.name, attr);
            LOGGER.debug("Not able to read MBean attribute", e);
            return null;
        }
    }

    public String[] getAttributes() {
        return attributes;
    }

}
