{{/*
Create a fully qualified extended-services name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "extendedServices.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name .Values.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "extendedServices.selectorLabels" -}}
app.kubernetes.io/component: {{ .Values.name | quote }}
{{- end -}}

{{- define "extendedServices.labels" -}}
{{ include "extendedServices.selectorLabels" . }}
{{- end -}}

{{/*
Create the name of the service account to use for the extended-services component
*/}}
{{- define "extendedServices.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "extendedServices.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
