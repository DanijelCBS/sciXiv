<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    version="2.0">
    
    <xsl:template name="TempAuthor">
        <xsl:param name="author"/>
        <div>
            <xsl:value-of select="$author/sp:name"/><br/>
            <xsl:value-of select="$author/sp:affiliation"/><br/>
            <xsl:value-of select="$author/sp:city"/>, <xsl:value-of select="$author/sp:state"/><br/>
            <xsl:value-of select="$author/sp:email"/>
        </div>
    </xsl:template>
    
    <xsl:template name="TempChapter">
        <xsl:choose>
            <xsl:when test="@level=1">
                <h2>
                    <xsl:value-of select="./sp:title"/>
                </h2>
            </xsl:when>
            <xsl:when test="@level=2">
                <h3>
                    <xsl:value-of select="./sp:title"/>
                </h3>
            </xsl:when>
            <xsl:when test="@level=3">
                <h4>
                    <xsl:value-of select="./sp:title"/>
                </h4>
            </xsl:when>
        </xsl:choose>
        <xsl:for-each select="./sp:paragraph">
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="sp:paragraph/text()">
        <xsl:copy-of select="." />
    </xsl:template>
    
    <xsl:template match="sp:emphasizedText">
        <em>
            <xsl:copy-of select="." />
        </em>
    </xsl:template>
    
    <xsl:template match="sp:boldText">
        <b>
            <xsl:copy-of select="." />
        </b>
    </xsl:template>
    
    <xsl:template match="sp:quote">
        <blockquote>
            <xsl:value-of select="./sp:quoteContent"/> - <xsl:value-of select="./sp:source"/>
        </blockquote>
    </xsl:template>
    
    <xsl:template match="sp:figure">
        <div style="text-align:center"><br/>
            <img>
                <xsl:attribute name="src">data:image/jpeg;base64,<xsl:value-of select="./sp:image"/></xsl:attribute>
                <xsl:attribute name="width">
                    <xsl:value-of select="./@width"/>
                </xsl:attribute>
                <xsl:attribute name="height">
                    <xsl:value-of select="./@height"/>
                </xsl:attribute>
            </img><br/><span><i><xsl:value-of select="./sp:description"/></i></span>
        </div><br/>
    </xsl:template>
    
    <xsl:template match="sp:list">
        <xsl:choose>
            <xsl:when test="@ordered='true'">
                <ol>
                    <xsl:for-each select="./*">
                        <li>  
                            <xsl:apply-templates select="."></xsl:apply-templates>
                        </li>
                    </xsl:for-each>
                </ol>
            </xsl:when>
            <xsl:otherwise>
                <ul>
                    <xsl:for-each select="./*">
                        <li>  
                            <xsl:apply-templates select="."></xsl:apply-templates>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="sp:code">
        <code>
            <xsl:value-of select="."/>
        </code>
    </xsl:template>
    
    <xsl:template match="sp:table">
        <div style="text-align: center;"><br/>
            <table border="1" style="width:30%; text-align:center; margin: 0 auto;">
                <xsl:for-each select="./sp:tableRow">
                    <tr>
                        <xsl:for-each select="./sp:tableCell">
                            <td>
                                <xsl:apply-templates></xsl:apply-templates>
                            </td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </table>
            <span><i><xsl:value-of select="./sp:tableDescription"/></i></span>
        </div><br/>
    </xsl:template>
                
    <xsl:template match="sp:referencePointer">
        <span><xsl:value-of select="."/></span>
        <a>
            <xsl:attribute name="href">#<xsl:value-of select="./@refId"/>
            </xsl:attribute>
            <sup>[<xsl:value-of select="./@refId"/>]</sup>
        </a>
    </xsl:template>         
    
    <xsl:template match="sp:mathExpression">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="sp:integral">
        $$\int_{<xsl:value-of select="./sp:begin"/>}^{<xsl:value-of select="./sp:end"/>} <xsl:value-of select="./sp:content"/>$$
    </xsl:template>
    
    <xsl:template match="sp:sum">
        $$\sum_{<xsl:value-of select="./sp:counter"/>=<xsl:value-of select="./sp:begin"/>}^{<xsl:value-of select="./sp:end"/>} <xsl:value-of select="./sp:content"/>$$
    </xsl:template>
    
    <xsl:template match="sp:limit">
        $$\lim_{<xsl:value-of select="./sp:variable"/>\to<xsl:value-of select="./sp:target"/>} <xsl:value-of select="./sp:content"/>$$
    </xsl:template>
    
    <xsl:template name="TempReference">
        <div>
            <xsl:attribute name="id">
                <xsl:value-of select="@refPartId"/>
            </xsl:attribute>
            <xsl:value-of select="@refPartId"/>. <a>
                <xsl:attribute name="href">http://localhost:8080/scientificPublication/xhtml?title=<xsl:value-of select="./sp:referenceTitle"/></xsl:attribute>
                <xsl:for-each select="./sp:referenceAuthors/sp:referenceAuthor">
                    <xsl:value-of select="."/><xsl:if test="not(position()=last())">, </xsl:if>
                </xsl:for-each>; <xsl:value-of select="./sp:referenceTitle"/>, <xsl:if test="./sp:publisherName"><xsl:value-of select="./sp:publisherName"/>, </xsl:if><xsl:value-of select="./sp:yearIssued"/>
            </a>
        </div><br/>
    </xsl:template>
       
</xsl:stylesheet>
