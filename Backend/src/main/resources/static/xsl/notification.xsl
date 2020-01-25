<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:n="http://ftn.uns.ac.rs/notification" version="2.0">
    
    <xsl:template match="/">
        <html>
            <head>
                <title>Notification XMLServices</title>
            </head>
            <body>
                <xsl:variable name="scientificId" select="document(concat('http://', n:notification/n:publicationName))"/>
                <h3>
                    Notification regarding scientific publication 
                    "<xsl:value-of select="$scientificId/n:publicationName"/>"<br/>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
