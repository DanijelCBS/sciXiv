<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    version="2.0">
    <xsl:import href="templates-fo.xsl"/>
    
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master
                    master-name="sciPubPage"
                    page-height="29.7cm"
                    page-width="21cm"
                    margin-top="1cm"
                    margin-bottom="2cm"
                    margin-left="2.5cm"
                    margin-right="2.5cm">
                    <fo:region-body margin-top="2cm" margin-bottom="2cm"/>
                    <fo:region-before extent="2cm"/>
                    <fo:region-after extent="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            
            <fo:page-sequence master-reference="sciPubPage">
                <fo:flow flow-name="xsl-region-body" font-size="12px" font-family="Times" text-align="justify">
                    
                    <fo:block text-align="center" font-size="22px" space-after="16px">
                        <xsl:value-of select="sp:scientificPublication/sp:metadata/sp:title"/>
                    </fo:block>
                    
                    <fo:block space-after="18px">
                        <fo:table width="100%" table-layout="fixed" inline-progression-dimension="100%">
                            <fo:table-body>
                                <fo:table-row>
                                    <xsl:for-each select="sp:scientificPublication/sp:metadata/sp:authors/sp:author">
                                        <fo:table-cell text-align="center">
                                            <xsl:call-template name="TempAuthor">
                                                <xsl:with-param name="author" select = "." />
                                            </xsl:call-template>
                                        </fo:table-cell>
                                    </xsl:for-each>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    
                    <fo:block space-after="14px" space-before="16px">
                        <fo:block font-size="14px" space-after="5px" font-weight="bold">
                            Abstract
                        </fo:block>
                        <xsl:for-each select="sp:scientificPublication/sp:abstract/sp:paragraph">
                            <xsl:apply-templates/>
                        </xsl:for-each>
                    </fo:block>
                    <fo:block space-after="14px">
                        <fo:inline font-weight="bold">
                            Keywords:
                        </fo:inline>
                        <fo:inline font-style="italic"><xsl:for-each select="sp:scientificPublication/sp:metadata/sp:keywords/sp:keyword">
                            <xsl:value-of select="."/><xsl:if test="not(position()=last())">, </xsl:if>
                        </xsl:for-each></fo:inline>
                    </fo:block>
                    
                    <xsl:for-each select="sp:scientificPublication/sp:chapter">
                        <xsl:call-template name="TempChapter"/>
                    </xsl:for-each>
                    
                    <fo:block font-size="14px" space-after="5px" space-before="30px" font-weight="bold">
                        References
                    </fo:block>
                    <fo:block>
                        <xsl:for-each select="sp:scientificPublication/sp:references/sp:reference">
                            <xsl:call-template name="TempReference"/>
                        </xsl:for-each>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    
</xsl:stylesheet>
