export const createSelfSubjectAccessReviewYaml = `
kind: SelfSubjectAccessReview
spec:
  resourceAttributes:
    resource: \${{ resource }},
    verb: "*",
    namespace: \${{ namespace }},
`;

export const getSelfSubjectAccessReviewApiPath = () => `/apis/authorization.k8s.io/v1/selfsubjectaccessreviews`;
