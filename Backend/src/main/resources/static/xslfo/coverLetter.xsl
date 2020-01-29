<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:cl="http://ftn.uns.ac.rs/coverLetter"
    version="2.0">
    
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master
                    master-name="coverLetterPage"
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
            
            <fo:page-sequence master-reference="coverLetterPage">
                <fo:flow flow-name="xsl-region-body" font-size="12px" font-family="Times" text-align="justify">
                    <fo:block text-align="center" font-size="22px" space-after="16px">
                        <fo:inline font-weight="bold">
                            Cover letter for publication: "<xsl:value-of select="cl:coverLetter/cl:publicationTitle"/>" version No. <xsl:value-of select="cl:coverLetter/cl:version"/>
                        </fo:inline>
                    </fo:block>
                    
                    <fo:block>
                        <fo:footnote><fo:inline color="white">1</fo:inline>
                            <fo:footnote-body> 
                                <fo:block border-top="1px solid black"> 
                                    <fo:table margin="10px auto 10px auto" border="1px" font-size="10"> 
                                        <fo:table-column column-width="50%"/>
                                        <fo:table-column column-width="50%"/>
                                        <fo:table-body>  
                                            <fo:table-row >
                                                <fo:table-cell padding="2px" text-align-last="center">
                                                    <fo:block>
                                                        Organization:
                                                    </fo:block>
                                                    <fo:block> 

                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2px" text-align-last="center">
                                                    <fo:block>  

                                                    </fo:block>   
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>
                                </fo:block>
                            </fo:footnote-body>
                        </fo:footnote>
                    </fo:block>
                    
                </fo:flow>
            </fo:page-sequence>
            
        </fo:root>
    </xsl:template>
    
    <xsl:template match="b">
        <fo:inline font-weight="bold">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="blockquote">
        <fo:block start-indent="1.5cm" end-indent="1.5cm">
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="br">
        <fo:block> </fo:block>
    </xsl:template>
    
    <xsl:template match="centeredText">
        <fo:block text-align="center">
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="cite">
        <xsl:choose>
            <xsl:when test="parent::i">
                <fo:inline font-style="normal">
                    <xsl:apply-templates select="*|text()"/>
                </fo:inline>
            </xsl:when>
            <xsl:otherwise>
                <fo:inline font-style="italic">
                    <xsl:apply-templates select="*|text()"/>
                </fo:inline>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="em">
        <fo:inline font-style="italic">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="h1" name="h1Tag">
        <fo:block break-before="page">
            <fo:leader leader-pattern="rule"/>
        </fo:block> 
        <fo:block font-size="28pt" line-height="32pt"
            keep-with-next="always"
            space-after="22pt" font-family="serif">
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="@id">
                        <xsl:value-of select="@id"/>
                    </xsl:when>
                    <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                        preceding-sibling::*[1][@name]">
                        <xsl:value-of select="preceding-sibling::*[1]/@name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="generate-id()"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
</xsl:stylesheet>