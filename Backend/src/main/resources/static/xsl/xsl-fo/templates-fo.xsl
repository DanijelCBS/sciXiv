<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    version="2.0">
    
    <xsl:template name="TempAuthor">
        <xsl:param name="author"/>
        <fo:block>
            <fo:block><xsl:value-of select="$author/sp:name"/></fo:block>
            <fo:block><xsl:value-of select="$author/sp:affiliation"/></fo:block>
            <fo:block><xsl:value-of select="$author/sp:city"/>, <xsl:value-of select="$author/sp:state"/></fo:block>
            <fo:block><xsl:value-of select="$author/sp:email"/></fo:block>
        </fo:block>
    </xsl:template>
    
    <xsl:template name="TempChapter">
        <fo:block space-after="16px" space-before="16px">
            <xsl:choose>
                <xsl:when test="@level=1">
                    <fo:block font-size="18px" space-after="7px">
                        <xsl:value-of select="./sp:title"/>
                    </fo:block>
                </xsl:when>
                <xsl:when test="@level=2">
                    <fo:block font-size="16px" space-after="7px">
                        <xsl:value-of select="./sp:title"/>
                    </fo:block>
                </xsl:when>
                <xsl:when test="@level=3">
                    <fo:block font-size="14px" space-after="7px">
                        <xsl:value-of select="./sp:title"/>
                    </fo:block>
                </xsl:when>
            </xsl:choose>
            <xsl:for-each select="./sp:paragraph">
                <fo:block>
                    <xsl:apply-templates/>
                </fo:block>
            </xsl:for-each>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="sp:emphasizedText">
        <fo:inline font-style="italic"><xsl:apply-templates/></fo:inline>
    </xsl:template>
    
    <xsl:template match="sp:boldText">
        <fo:inline font-weight="bold"><xsl:apply-templates/></fo:inline>
    </xsl:template>
    
    <xsl:template match="sp:quote">
        <fo:inline>
            <xsl:value-of select="./sp:quoteContent"/> - <fo:inline font-style="italic"><xsl:value-of select="./sp:source"/></fo:inline>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="sp:figure">
        <fo:block text-align="center" space-after="10px">
            <fo:block><fo:external-graphic>
                <xsl:attribute name="src">data:image/jpeg;base64,<xsl:value-of select="./sp:image"/></xsl:attribute>
                <xsl:attribute name="content-width">
                    <xsl:value-of select="./@width"/>
                </xsl:attribute>
                <xsl:attribute name="content-height">
                    <xsl:value-of select="./@height"/>
                </xsl:attribute>
            </fo:external-graphic></fo:block>
            <fo:inline font-style="italic" font-size="10px"><xsl:value-of select="./sp:description"/></fo:inline>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="sp:list">
        <xsl:choose>
            <xsl:when test="@ordered='true'">
                <fo:list-block space-before="16px">
                    <xsl:for-each select="./*">
                        <fo:list-item>
                            <fo:list-item-label>
                                <fo:block>
                                    <xsl:number value="position()" format="1"/>.
                                </fo:block>
                            </fo:list-item-label>
                            <fo:list-item-body start-indent="10px">
                                <fo:block><xsl:apply-templates select="."/></fo:block>
                            </fo:list-item-body>
                        </fo:list-item>
                    </xsl:for-each>
                </fo:list-block>
            </xsl:when>
            <xsl:otherwise>
                <fo:list-block space-before="16px">
                    <xsl:for-each select="./*">
                        <fo:list-item>
                            <fo:list-item-label>
                                <fo:block>*</fo:block>
                            </fo:list-item-label>
                            <fo:list-item-body start-indent="10px">
                                <fo:block>
                                    <xsl:apply-templates select="."/>
                                </fo:block>
                            </fo:list-item-body>
                        </fo:list-item>
                    </xsl:for-each>
                </fo:list-block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="sp:code">
        <fo:block font-family="monospace">
            <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="sp:table">
        <fo:block margin="10px" text-align="center">
            <fo:table break-before="page" table-layout="fixed" inline-progression-dimension="100%">
                <fo:table-body>
                    <xsl:for-each select="./sp:tableRow">
                        <fo:table-row>
                            <xsl:for-each select="./sp:tableCell">
                                <fo:table-cell border="1px solid black" display-align="center">
                                    <fo:block>
                                        <xsl:value-of select="."/>
                                    </fo:block>
                                </fo:table-cell>
                            </xsl:for-each>
                        </fo:table-row>
                    </xsl:for-each>
                </fo:table-body>
            </fo:table>
            <fo:inline font-style="italic"><xsl:value-of select="./sp:tableDescription"/></fo:inline>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="sp:referencePointer">
        <xsl:value-of select="."/>
        <fo:basic-link>
            <xsl:attribute name="internal-destination"><xsl:value-of select="./@refId"/>
            </xsl:attribute>
            <fo:inline vertical-align="super" font-size="smaller">[<xsl:value-of select="./@refId"/>]</fo:inline>
        </fo:basic-link>
    </xsl:template>         
    
    <xsl:template match="sp:mathExpression">
        <fo:block space-before="12px">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="sp:integral">
        <fo:inline space-before="12px">$$\int_{<xsl:value-of select="./sp:begin"/>}^{<xsl:value-of select="./sp:end"/>} <xsl:value-of select="./sp:content"/>$$</fo:inline>
    </xsl:template>
    
    <xsl:template match="sp:sum">
        <fo:inline space-before="12px">$$\sum_{<xsl:value-of select="./sp:counter"/>=<xsl:value-of select="./sp:begin"/>}^{<xsl:value-of select="./sp:end"/>} <xsl:value-of select="./sp:content"/>$$</fo:inline>
    </xsl:template>
    
    <xsl:template match="sp:limit">
        <fo:inline space-before="12px">$$\lim_{<xsl:value-of select="./sp:variable"/>\to<xsl:value-of select="./sp:target"/>} <xsl:value-of select="./sp:content"/>$$</fo:inline>
    </xsl:template>
    
    <xsl:template name="TempReference">
        <fo:block>
            <xsl:attribute name="id">
                <xsl:value-of select="@refPartId"/>
            </xsl:attribute>
            <fo:inline><xsl:value-of select="@refPartId"/>.
                <xsl:for-each select="./sp:referenceAuthors/sp:referenceAuthor">
                    <xsl:value-of select="."/><xsl:if test="not(position()=last())">, </xsl:if>
                </xsl:for-each>; <xsl:value-of select="./sp:referenceTitle"/>, <xsl:if test="./sp:publisherName"><xsl:value-of select="./sp:publisherName"/>, </xsl:if><xsl:value-of select="./sp:yearIssued"/>
            </fo:inline>
        </fo:block>
    </xsl:template>
    
</xsl:stylesheet>
