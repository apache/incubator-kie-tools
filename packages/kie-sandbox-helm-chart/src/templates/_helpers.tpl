{{/*
Expand the name of the chart.
*/}}
{{- define "kie-sandbox-helm-chart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "kie-sandbox-helm-chart.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create a fully qualified cors-proxy name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "kie-sandbox-helm-chart.corsProxy.fullname" -}}
{{- if .Values.corsProxy.fullnameOverride -}}
{{- .Values.corsProxy.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- printf "%s-%s" .Release.Name .Values.corsProxy.name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s-%s" .Release.Name $name .Values.corsProxy.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create a fully qualified extended-services name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "kie-sandbox-helm-chart.extendedServices.fullname" -}}
{{- if .Values.extendedServices.fullnameOverride -}}
{{- .Values.extendedServices.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- printf "%s-%s" .Release.Name .Values.extendedServices.name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s-%s" .Release.Name $name .Values.extendedServices.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create a fully qualified sandbox name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "kie-sandbox-helm-chart.sandbox.fullname" -}}
{{- if .Values.sandbox.fullnameOverride -}}
{{- .Values.sandbox.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- printf "%s-%s" .Release.Name .Values.sandbox.name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s-%s" .Release.Name $name .Values.sandbox.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "kie-sandbox-helm-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "kie-sandbox-helm-chart.labels" -}}
helm.sh/chart: {{ include "kie-sandbox-helm-chart.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: {{ include "kie-sandbox-helm-chart.name" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "kie-sandbox-helm-chart.selectorLabels" -}}
app.kubernetes.io/name: {{ include "kie-sandbox-helm-chart.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "kie-sandbox-helm-chart.corsProxy.labels" -}}
{{ include "kie-sandbox-helm-chart.corsProxy.selectorLabels" . }}
{{ include "kie-sandbox-helm-chart.labels" . }}
{{- end -}}

{{- define "kie-sandbox-helm-chart.corsProxy.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.corsProxy.name | quote }}
{{ include "kie-sandbox-helm-chart.selectorLabels" . }}
{{- end -}}

{{- define "kie-sandbox-helm-chart.extendedServices.labels" -}}
{{ include "kie-sandbox-helm-chart.extendedServices.selectorLabels" . }}
{{ include "kie-sandbox-helm-chart.labels" . }}
{{- end -}}

{{- define "kie-sandbox-helm-chart.extendedServices.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.extendedServices.name | quote }}
{{ include "kie-sandbox-helm-chart.selectorLabels" . }}
{{- end -}}

{{- define "kie-sandbox-helm-chart.sandbox.labels" -}}
{{ include "kie-sandbox-helm-chart.sandbox.selectorLabels" . }}
{{ include "kie-sandbox-helm-chart.labels" . }}
{{- end -}}

{{- define "kie-sandbox-helm-chart.sandbox.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.sandbox.name | quote }}
{{ include "kie-sandbox-helm-chart.selectorLabels" . }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "kie-sandbox-helm-chart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "kie-sandbox-helm-chart.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the name of the service account to use for the cors-proxy component
*/}}
{{- define "kie-sandbox-helm-chart.corsProxy.serviceAccountName" -}}
{{- if .Values.corsProxy.serviceAccount.create -}}
    {{ default (include "kie-sandbox-helm-chart.corsProxy.fullname" .) .Values.corsProxy.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.corsProxy.serviceAccount.name }}
{{- end -}}
{{- end -}}

{{/*
Create the name of the service account to use for the extended-services component
*/}}
{{- define "kie-sandbox-helm-chart.extendedServices.serviceAccountName" -}}
{{- if .Values.extendedServices.serviceAccount.create -}}
    {{ default (include "kie-sandbox-helm-chart.extendedServices.fullname" .) .Values.extendedServices.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.extendedServices.serviceAccount.name }}
{{- end -}}
{{- end -}}

{{/*
Create the name of the service account to use for the sandbox component
*/}}
{{- define "kie-sandbox-helm-chart.sandbox.serviceAccountName" -}}
{{- if .Values.sandbox.serviceAccount.create -}}
    {{ default (include "kie-sandbox-helm-chart.sandbox.fullname" .) .Values.sandbox.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.sandbox.serviceAccount.name }}
{{- end -}}
{{- end -}}
