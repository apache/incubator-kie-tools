{{/*
Create a fully qualified name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "managementConsole.fullname" -}}
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

{{- define "managementConsole.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{ include "selectorLabels" . }}
{{- end -}}

{{- define "managementConsole.labels" -}}
{{ include "managementConsole.selectorLabels" . }}
{{ include "labels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the management console component
*/}}
{{- define "managementConsole.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "managementConsole.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
