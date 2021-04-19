<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="programlisting">
    <programlisting>
      <xsl:choose>
        <xsl:when test="matches(text(), '&lt;/[-a-zA-Z0-9]+') or matches(text(), '/&gt;')">
          <xsl:attribute name="language">xml</xsl:attribute>
        </xsl:when>
        <xsl:when test="matches(text(), '\\\n') or matches(text(), '\n#')">
          <xsl:attribute name="language">ini</xsl:attribute> <!-- Actually a .properties file, but close enough -->
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="language">java</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="node()"/>
    </programlisting>
  </xsl:template>
  
  <!-- TODO properties file: match on lines ending with \ -->
  
  <!-- TODO java listing (the default?) -->
</xsl:stylesheet>
