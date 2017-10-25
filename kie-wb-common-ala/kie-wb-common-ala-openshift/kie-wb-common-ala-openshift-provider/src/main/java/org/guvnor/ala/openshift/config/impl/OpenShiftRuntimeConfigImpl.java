/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * Cloneable implementation of OpenShiftRuntimeConfig.
 */
public class OpenShiftRuntimeConfigImpl implements OpenShiftRuntimeConfig, CloneableConfig<OpenShiftRuntimeConfig> {

    private String runtimeName;
    private ProviderId providerId;
    private String applicationName;
    private String kieServerContainerDeployment;
    private String projectName;
    private String resourceSecretsUri;
    private String resourceStreamsUri;
    private String resourceTemplateName;
    private String resourceTemplateParamDelimiter;
    private String resourceTemplateParamAssigner;
    private String resourceTemplateParamValues;
    private String resourceTemplateUri;
    private String serviceName;

    public OpenShiftRuntimeConfigImpl() {
    }

    public OpenShiftRuntimeConfigImpl(
            String runtimeName,
            ProviderId providerId,
            String applicationName,
            String kieServerContainerDeployment,
            String projectName,
            String resourceSecretsUri,
            String resourceStreamsUri,
            String resourceTemplateName,
            String resourceTemplateParamDelimiter,
            String resourceTemplateParamAssigner,
            String resourceTemplateParamValues,
            String resourceTemplateUri,
            String serviceName) {
        this.runtimeName = runtimeName;
        this.providerId = providerId;
        this.applicationName = applicationName;
        this.kieServerContainerDeployment = kieServerContainerDeployment;
        this.projectName = projectName;
        this.resourceSecretsUri = resourceSecretsUri;
        this.resourceStreamsUri = resourceStreamsUri;
        this.resourceTemplateName = resourceTemplateName;
        this.resourceTemplateParamDelimiter = resourceTemplateParamDelimiter;
        this.resourceTemplateParamAssigner = resourceTemplateParamAssigner;
        this.resourceTemplateParamValues = resourceTemplateParamValues;
        this.resourceTemplateUri = resourceTemplateUri;
        this.serviceName = serviceName;
    }

    @Override
    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    public void setProviderId(ProviderId providerId) {
        this.providerId = providerId;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getKieServerContainerDeployment() {
        return kieServerContainerDeployment;
    }

    public void setKieServerContainerDeployment(String kieServerContainerDeployment) {
        this.kieServerContainerDeployment = kieServerContainerDeployment;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getResourceSecretsUri() {
        return resourceSecretsUri;
    }

    public void setResourceSecretsUri(String resourceSecretsUri) {
        this.resourceSecretsUri = resourceSecretsUri;
    }

    @Override
    public String getResourceStreamsUri() {
        return resourceStreamsUri;
    }

    public void setResourceStreamsUri(String resourceStreamsUri) {
        this.resourceStreamsUri = resourceStreamsUri;
    }

    @Override
    public String getResourceTemplateName() {
        return resourceTemplateName;
    }

    public void setResourceTemplateName(String resourceTemplateName) {
        this.resourceTemplateName = resourceTemplateName;
    }

    @Override
    public String getResourceTemplateParamDelimiter() {
        return resourceTemplateParamDelimiter;
    }

    public void setResourceTemplateParamDelimiter(String resourceTemplateParamDelimiter) {
        this.resourceTemplateParamDelimiter = resourceTemplateParamDelimiter;
    }

    @Override
    public String getResourceTemplateParamAssigner() {
        return resourceTemplateParamAssigner;
    }

    public void setResourceTemplateParamAssigner(String resourceTemplateParamAssigner) {
        this.resourceTemplateParamAssigner = resourceTemplateParamAssigner;
    }

    @Override
    public String getResourceTemplateParamValues() {
        return resourceTemplateParamValues;
    }

    public void setResourceTemplateParamValues(String resourceTemplateParamValues) {
        this.resourceTemplateParamValues = resourceTemplateParamValues;
    }

    @Override
    public String getResourceTemplateUri() {
        return resourceTemplateUri;
    }

    public void setResourceTemplateUri(String resourceTemplateUri) {
        this.resourceTemplateUri = resourceTemplateUri;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public OpenShiftRuntimeConfig asNewClone(final OpenShiftRuntimeConfig source) {
        return new OpenShiftRuntimeConfigImpl(
                source.getRuntimeName(),
                source.getProviderId(),
                source.getApplicationName(),
                source.getKieServerContainerDeployment(),
                source.getProjectName(),
                source.getResourceSecretsUri(),
                source.getResourceStreamsUri(),
                source.getResourceTemplateName(),
                source.getResourceTemplateParamDelimiter(),
                source.getResourceTemplateParamAssigner(),
                source.getResourceTemplateParamValues(),
                source.getResourceTemplateUri(),
                source.getServiceName());
    }

    @Override
    public String toString() {
        return "OpenShiftRuntimeConfigImpl{" +
            ", runtimeName=" + runtimeName +
            ", providerId=" + providerId +
            ", applicationName=" + applicationName +
            ", kieServerContainerDeployment=" + kieServerContainerDeployment +
            ", projectName=" + projectName +
            ", resourceSecretsUri=" + resourceSecretsUri +
            ", resourceStreamsUri=" + resourceStreamsUri +
            ", resourceTemplateName=" + resourceTemplateName +
            ", resourceTemplateParamDelimiter=" + resourceTemplateParamDelimiter +
            ", resourceTemplateParamAssigner=" + resourceTemplateParamAssigner +
            ", resourceTemplateParamValues=" + resourceTemplateParamValues +
            ", resourceTemplateUri=" + resourceTemplateUri +
            ", serviceName=" + serviceName +
            "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        result = prime * result + ((kieServerContainerDeployment == null) ? 0 : kieServerContainerDeployment.hashCode());
        result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
        result = prime * result + ((resourceSecretsUri == null) ? 0 : resourceSecretsUri.hashCode());
        result = prime * result + ((resourceStreamsUri == null) ? 0 : resourceStreamsUri.hashCode());
        result = prime * result + ((resourceTemplateName == null) ? 0 : resourceTemplateName.hashCode());
        result = prime * result + ((resourceTemplateParamAssigner == null) ? 0 : resourceTemplateParamAssigner.hashCode());
        result = prime * result + ((resourceTemplateParamDelimiter == null) ? 0 : resourceTemplateParamDelimiter.hashCode());
        result = prime * result + ((resourceTemplateParamValues == null) ? 0 : resourceTemplateParamValues.hashCode());
        result = prime * result + ((resourceTemplateUri == null) ? 0 : resourceTemplateUri.hashCode());
        result = prime * result + ((runtimeName == null) ? 0 : runtimeName.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OpenShiftRuntimeConfigImpl)) {
            return false;
        }
        OpenShiftRuntimeConfigImpl other = (OpenShiftRuntimeConfigImpl) obj;
        if (applicationName == null) {
            if (other.applicationName != null) {
                return false;
            }
        } else if (!applicationName.equals(other.applicationName)) {
            return false;
        }
        if (kieServerContainerDeployment == null) {
            if (other.kieServerContainerDeployment != null) {
                return false;
            }
        } else if (!kieServerContainerDeployment.equals(other.kieServerContainerDeployment)) {
            return false;
        }
        if (projectName == null) {
            if (other.projectName != null) {
                return false;
            }
        } else if (!projectName.equals(other.projectName)) {
            return false;
        }
        if (providerId == null) {
            if (other.providerId != null) {
                return false;
            }
        } else if (!providerId.equals(other.providerId)) {
            return false;
        }
        if (resourceSecretsUri == null) {
            if (other.resourceSecretsUri != null) {
                return false;
            }
        } else if (!resourceSecretsUri.equals(other.resourceSecretsUri)) {
            return false;
        }
        if (resourceStreamsUri == null) {
            if (other.resourceStreamsUri != null) {
                return false;
            }
        } else if (!resourceStreamsUri.equals(other.resourceStreamsUri)) {
            return false;
        }
        if (resourceTemplateName == null) {
            if (other.resourceTemplateName != null) {
                return false;
            }
        } else if (!resourceTemplateName.equals(other.resourceTemplateName)) {
            return false;
        }
        if (resourceTemplateParamAssigner == null) {
            if (other.resourceTemplateParamAssigner != null) {
                return false;
            }
        } else if (!resourceTemplateParamAssigner.equals(other.resourceTemplateParamAssigner)) {
            return false;
        }
        if (resourceTemplateParamDelimiter == null) {
            if (other.resourceTemplateParamDelimiter != null) {
                return false;
            }
        } else if (!resourceTemplateParamDelimiter.equals(other.resourceTemplateParamDelimiter)) {
            return false;
        }
        if (resourceTemplateParamValues == null) {
            if (other.resourceTemplateParamValues != null) {
                return false;
            }
        } else if (!resourceTemplateParamValues.equals(other.resourceTemplateParamValues)) {
            return false;
        }
        if (resourceTemplateUri == null) {
            if (other.resourceTemplateUri != null) {
                return false;
            }
        } else if (!resourceTemplateUri.equals(other.resourceTemplateUri)) {
            return false;
        }
        if (runtimeName == null) {
            if (other.runtimeName != null) {
                return false;
            }
        } else if (!runtimeName.equals(other.runtimeName)) {
            return false;
        }
        if (serviceName == null) {
            if (other.serviceName != null) {
                return false;
            }
        } else if (!serviceName.equals(other.serviceName)) {
            return false;
        }
        return true;
    }

}
