<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    version="2.0">
    <xsl:import href="templates.xsl"/>
    
    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="sp:scientificPublication/sp:metadata/sp:title"/>
                </title>
                <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
                <script type="text/javascript" id="MathJax-script" src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js"></script>
            </head>
            <body style="background-color:#e8e8e8; text-align: justify; text-justify: inter-word; margin: auto 50px auto 50px;">
                <h1 align="center">
                    <xsl:value-of select="sp:scientificPublication/sp:metadata/sp:title"/>
                </h1><br/><br/>
                
                <b><i>Abstract</i></b>
                <div><br/>
                    <xsl:for-each select="sp:scientificPublication/sp:abstract/sp:paragraph">
                        <xsl:apply-templates/>
                    </xsl:for-each>
                </div><br/>
                <span>
                    <b><i>Keywords - </i></b>
                    <i><xsl:for-each select="sp:scientificPublication/sp:metadata/sp:keywords/sp:keyword">
                        <xsl:value-of select="."/><xsl:if test="not(position()=last())">, </xsl:if>
                    </xsl:for-each></i>
                </span>
                
                <xsl:for-each select="sp:scientificPublication/sp:chapter">
                    <xsl:call-template name="TempChapter">
                    </xsl:call-template>
                </xsl:for-each>
                
                <b><i>References</i></b>
                <p>
                    <xsl:for-each select="sp:scientificPublication/sp:references/sp:reference">
                        <xsl:sort select="@sp:id"></xsl:sort>
                        <xsl:call-template name="TempReference">
                            <xsl:with-param name="pos" select="position()"></xsl:with-param>
                        </xsl:call-template>
                    </xsl:for-each>
                </p>
                
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="sp:referencePointer">
        <span><xsl:value-of select="."/></span>
        <xsl:variable name="refId" select="@sp:refId"/>
        <a>
            <xsl:attribute name="href">#<xsl:value-of select="./@sp:refId"/>
            </xsl:attribute>
            <sup>[
                <xsl:for-each select="ancestor::sp:scientificPublication/sp:references/sp:reference">
                    <xsl:sort select="@sp:id"></xsl:sort>
                    <xsl:if test="@sp:id = $refId">
                        <xsl:value-of select="position()"/>
                    </xsl:if>
                </xsl:for-each>
                ]</sup>
        </a>
    </xsl:template>
    
    <xsl:template name="TempReference">
        <xsl:param name="pos"/>
        <div>
            <xsl:attribute name="id">
                <xsl:value-of select="@sp:id"/>
            </xsl:attribute>
            <xsl:value-of select="$pos"/>. <a>
                <xsl:attribute name="href">/<xsl:value-of select="./sp:referenceTitle"/></xsl:attribute>
                <xsl:for-each select="./sp:referenceAuthors/sp:referenceAuthor">
                    <xsl:value-of select="."/><xsl:if test="not(position()=last())">, </xsl:if>
                </xsl:for-each>; <xsl:value-of select="./sp:referenceTitle"/>, <xsl:if test="./sp:publisherName"><xsl:value-of select="./sp:publisherName"/>, </xsl:if><xsl:value-of select="./sp:yearIssued"/>
            </a>
        </div><br/>
    </xsl:template>
    
    <xsl:template match="sp:figure">
        <div style="text-align:center"><br/>
            <img>
                <xsl:attribute name="src">data:image/jpeg;charset=utf-8;base64,<xsl:value-of select="./sp:image"/></xsl:attribute>
                <xsl:attribute name="width">
                    <xsl:value-of select="./@width"/>
                </xsl:attribute>
                <xsl:attribute name="height">
                    <xsl:value-of select="./@height"/>
                </xsl:attribute>
            </img><br/><span><i><xsl:value-of select="./sp:description"/></i></span>
        </div><br/>
    </xsl:template>
    
</xsl:stylesheet>
