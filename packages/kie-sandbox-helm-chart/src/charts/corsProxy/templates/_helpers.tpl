{{/*
Create a fully qualified cors-proxy name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "corsProxy.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name .Values.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "corsProxy.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{- end -}}

{{- define "corsProxy.labels" -}}
{{ include "corsProxy.selectorLabels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the cors-proxy component
*/}}
{{- define "corsProxy.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "corsProxy.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
