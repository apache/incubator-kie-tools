export enum KnativeApiVersions {
  KAFKA_SOURCE = "sources.knative.dev/v1beta1",
  SERVICE = "serving.knative.dev/v1",
}

export const KnativeLabelNames = {
  SERVICE: "serving.knative.dev/service",
};

export const KAFKA_SOURCE_FINALIZER = "kafkasources.sources.knative.dev";
export const KAFKA_SOURCE_CLIENT_ID_KEY = "kafka-source-client-id";
export const KAFKA_SOURCE_CLIENT_SECRET_KEY = "kafka-source-client-secret";
export const KAFKA_SOURCE_CLIENT_MECHANISM_KEY = "kafka-source-client-mechanism";
export const KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN = "PLAIN";
