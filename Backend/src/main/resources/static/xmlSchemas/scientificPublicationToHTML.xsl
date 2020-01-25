<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    version="2.0">
    <xsl:import href="./templates.xsl"/>
    
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
                </h1>
                <table style="width:100%">
                    <tr>
                        <xsl:for-each select="sp:scientificPublication/sp:metadata/sp:authors/sp:author">
                            <td>
                                <div align="center">
                                    <xsl:call-template name="TempAuthor">
                                        <xsl:with-param name="author" select = "." />
                                    </xsl:call-template>
                                </div>
                            </td>
                        </xsl:for-each>
                    </tr>
                </table><br/><br/>
                
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
                        <xsl:call-template name="TempReference"/>
                    </xsl:for-each>
                </p>
                
            </body>
        </html>
    </xsl:template>
    
</xsl:stylesheet>