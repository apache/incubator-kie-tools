/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.sw.definition;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.j2cl.tools.utils.GwtIncompatible;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.MetadataJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.MetadataYamlSerializer;

@JsType
@GWT3Export
@JsonbTypeSerializer(MetadataJsonSerializer.class)
@JsonbTypeDeserializer(MetadataJsonSerializer.class)
@YamlTypeSerializer(MetadataYamlSerializer.class)
@YamlTypeDeserializer(MetadataYamlSerializer.class)
public class Metadata extends GWTMetadata {

    public String name;

    public String type;

    public String icon;

    public Metadata() {
    }

    public void setName(String name) {
        this.name = name;
    }

    @GwtIncompatible
    public String getName() {
        return name;
    }

    @GwtIncompatible
    public String getType() {
        return type;
    }

    @GwtIncompatible
    public void setType(String type) {
        this.type = type;
    }

    @GwtIncompatible
    public String getIcon() {
        return icon;
    }

    @GwtIncompatible
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
