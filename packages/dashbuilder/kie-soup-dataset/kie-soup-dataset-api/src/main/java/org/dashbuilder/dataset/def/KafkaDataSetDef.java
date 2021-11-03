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
package org.dashbuilder.dataset.def;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.validation.groups.KafkaDataSetDefValidation;

public class KafkaDataSetDef extends DataSetDef {

    public enum MetricsTarget {
        BROKER,
        PRODUCER,
        CONSUMER;
    }

    @NotNull(groups = {KafkaDataSetDefValidation.class})
    @Size(min = 1, groups = {KafkaDataSetDefValidation.class})
    protected String host;

    @NotNull(groups = {KafkaDataSetDefValidation.class})
    @Size(min = 4, max = 5, groups = {KafkaDataSetDefValidation.class})
    @Digits(fraction = 0, integer = 5, message = "Max number of digits is 5.", groups = {KafkaDataSetDefValidation.class})
    protected String port;

    protected MetricsTarget target;
    protected String filter;

    protected String clientId;
    protected String nodeId;
    protected String topic;
    protected String partition;

    public KafkaDataSetDef() {
        super.setProvider(DataSetProviderType.KAFKA);
        host = "localhost";
        port = "9999";
        target = MetricsTarget.BROKER;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public MetricsTarget getTarget() {
        return target;
    }

    public void setTarget(MetricsTarget target) {
        this.target = target;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public DataSetDef clone() {
        KafkaDataSetDef def = new KafkaDataSetDef();
        clone(def);
        def.setHost(this.host);
        def.setPort(this.port);
        def.setTarget(this.target);
        def.setClientId(this.clientId);
        def.setFilter(this.filter);
        def.setNodeId(this.nodeId);
        def.setTopic(this.topic);
        def.setPartition(this.partition);
        return def;
    }

    @Override
    public String toString() {
        return "KafkaDataSetDef [host=" + host + ", port=" + port + ", target=" + target +
               ", filter=" + filter + ", clientId=" + clientId + ", nodeId=" + nodeId +
               ", topic=" + topic + ", partition=" + partition + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((filter == null) ? 0 : filter.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        result = prime * result + ((partition == null) ? 0 : partition.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        KafkaDataSetDef other = (KafkaDataSetDef) obj;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (filter == null) {
            if (other.filter != null)
                return false;
        } else if (!filter.equals(other.filter))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (nodeId == null) {
            if (other.nodeId != null)
                return false;
        } else if (!nodeId.equals(other.nodeId))
            return false;
        if (partition == null) {
            if (other.partition != null)
                return false;
        } else if (!partition.equals(other.partition))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (target != other.target)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }

}
