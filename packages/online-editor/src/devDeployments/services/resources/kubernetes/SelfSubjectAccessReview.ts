export const createSelfSubjectAccessReviewYaml = `
kind: SelfSubjectAccessReview
apiVersion: authorization.k8s.io/v1
spec:
  resourceAttributes:
    resource: \${{ resource }}
    verb: "*"
    namespace: \${{ namespace }}
`;

export const getSelfSubjectAccessReviewApiPath = () => `apis/authorization.k8s.io/v1/selfsubjectaccessreviews`;
