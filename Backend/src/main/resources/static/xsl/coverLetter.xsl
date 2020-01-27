<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cl="http://ftn.uns.ac.rs/coverLetter"
    version="2.0">
    
    <xsl:template match="/">
        <html>
            <head>
                <title>
                    Cover letter for publication:  "<xsl:value-of select="cl:coverLetter/cl:publicationTitle"/>"
                </title>
                <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
            </head>
            <body style="background-color:#e8e8e8; text-align: justify; text-justify: inter-word; margin: auto 50px auto 50px;">
                <h1 align="center">
                    Cover letter for publication: "<xsl:value-of select="cl:coverLetter/cl:publicationTitle"/>" version No. <xsl:value-of select="cl:coverLetter/cl:version"/>
                </h1>
                
                <table align="center" style="width:100%" border="1">
                    <tbody>
                        <tr>
                            <td style="width: 395px;" colspan="2"><div align="center">Informations about author</div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">Name</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:name"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">Education title</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:educationTitle"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">Affiliation</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:affiliation"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">City</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:city"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">State</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:state"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">Phone number</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:phoneNumber"/></div></td>
                        </tr>
                        <tr>
                            <td style="width: 194.5px;"><div align="center">Email</div></td>
                            <td style="width: 194.5px;"><div align="center"><xsl:value-of select="cl:coverLetter/cl:author/cl:email"/></div></td>
                        </tr>
                    </tbody>
                </table>
                <div align = "right"><font color="red">Editor: <xsl:value-of select="cl:coverLetter/cl:targetPublisher/cl:editor"/>, from journal: <xsl:value-of select="cl:coverLetter/cl:targetPublisher/cl:journal"/></font></div>
                <br/><br/>
                
                <div>
                    <xsl:apply-templates select="cl:coverLetter/cl:content/cl:paragraph"></xsl:apply-templates>
                </div>
                
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="cl:paragraph">
        <p>
            <xsl:apply-templates></xsl:apply-templates>
        </p>
    </xsl:template>
    
    <xsl:template match="cl:boldText">
        <b><xsl:value-of select="."/></b>
    </xsl:template>
    
    <xsl:template match="cl:emphasizedText">
        <em><xsl:value-of select="."/></em>
    </xsl:template>
    
    <xsl:template match="cl:quote">
        <div>
            <blockquote cite="https://www.huxley.net/bnw/four.html">
                <p><xsl:value-of select="./cl:quoteContent"/></p>
                <footer><cite><xsl:value-of select="./cl:source"/></cite></footer>
            </blockquote>
        </div>
    </xsl:template>
    
    <xsl:template match="cl:list">
        <xsl:if test="@ordered='true'">
            <ol>
                <xsl:apply-templates select="cl:listItem"></xsl:apply-templates>
            </ol>
        </xsl:if>
        <xsl:if test="@ordered='false'">
            <ul>
                <xsl:apply-templates select="cl:listItem"></xsl:apply-templates>
            </ul>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="cl:listItem">
        <li><xsl:value-of select="."/></li>
    </xsl:template>
    
</xsl:stylesheet>