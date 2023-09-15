// Reference: https://www.w3.org/TR/REC-xml-names/#NT-QName

export type XmlQName = {
  prefix?: string;
  localPart: string;
};

export function parseXmlQName(qName: string): XmlQName {
  const split = qName.split(":");

  if (split.length <= 1) {
    return { localPart: qName };
  }

  if (split.length > 2) {
    throw new Error(`XML QNames can't have colons on neither the prefix or the localPart. Alledged QName: '${qName}'`);
  }

  return { prefix: split[0], localPart: split[1] };
}

export function buildXmlQName({ prefix, localPart }: XmlQName) {
  return prefix ? `${prefix}:${localPart}` : localPart;
}
