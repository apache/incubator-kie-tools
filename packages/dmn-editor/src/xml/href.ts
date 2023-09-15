export function buildXmlHref({ namespace, id }: { namespace?: string; id: string }) {
  return `${namespace ?? ""}#${id}`;
}

export function idFromHref(href: string | undefined) {
  if (!href) {
    return "";
  }

  if (href.startsWith("#")) {
    return href.substring(1);
  }

  return href;
}
