{{/*
Create a fully qualified extended-services name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "extended_services.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name .Values.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
These can be overriden by the base chart.
*/}}
{{- define "selectorLabels" -}}
{{- end -}}
{{- define "labels" -}}
{{- end -}}

{{- define "extended_services.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{ include "selectorLabels" . }}
{{- end -}}

{{- define "extended_services.labels" -}}
{{ include "extended_services.selectorLabels" . }}
{{ include "labels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the extended-services component
*/}}
{{- define "extended_services.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "extended_services.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
