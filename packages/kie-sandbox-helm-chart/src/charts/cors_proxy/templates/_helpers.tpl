{{/*
Create a fully qualified cors-proxy name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "cors_proxy.fullname" -}}
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

{{- define "cors_proxy.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{ include "selectorLabels" . }}
{{- end -}}

{{- define "cors_proxy.labels" -}}
{{ include "cors_proxy.selectorLabels" . }}
{{ include "labels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the cors-proxy component
*/}}
{{- define "cors_proxy.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "cors_proxy.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
