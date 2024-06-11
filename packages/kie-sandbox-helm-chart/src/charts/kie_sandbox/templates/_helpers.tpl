{{/*
Create a fully qualified sandbox name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "kie_sandbox.fullname" -}}
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

{{- define "kie_sandbox.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{ include "selectorLabels" . }}
{{- end -}}

{{- define "kie_sandbox.labels" -}}
{{ include "kie_sandbox.selectorLabels" . }}
{{ include "labels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the sandbox component
*/}}
{{- define "kie_sandbox.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "kie_sandbox.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}