export type FeelQName = {
  type: "feel-qname"; // To differentiate from XmlQName
  importName?: string;
  localPart: string;
};

export function parseFeelQName(qName: string): FeelQName {
  const split = qName.split(".");

  if (split.length <= 1) {
    return { type: "feel-qname", localPart: qName };
  }

  if (split.length > 2) {
    throw new Error(
      `XML QNames can't have dots (.) on neither the importName or the localPart. Alledged QName: '${qName}'`
    );
  }

  return { type: "feel-qname", importName: split[0], localPart: split[1] };
}

export function buildFeelQName({ importName, localPart }: FeelQName) {
  return importName ? `${importName}.${localPart}` : localPart;
}
