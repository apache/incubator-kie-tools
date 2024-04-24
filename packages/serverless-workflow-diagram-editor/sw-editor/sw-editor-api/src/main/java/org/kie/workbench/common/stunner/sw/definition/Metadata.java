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
