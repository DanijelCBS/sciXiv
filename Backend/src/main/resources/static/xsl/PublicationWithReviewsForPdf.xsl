<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:sp="http://ftn.uns.ac.rs/scientificPublication"
    xmlns:rvw="http://ftn.uns.ac.rs/review"
    version="2.0">
    <xsl:import href="templates.xsl"/>
    
    <xsl:template match="/">
        <xsl:variable name="publication" select="sp:scientificPublication"/>
        <html>
            <head>
                <title>
                    <xsl:value-of select="sp:scientificPublication/sp:metadata/sp:title"/>
                </title>
                <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
                <script type="text/javascript" id="MathJax-script" src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js"></script>
            </head>
            <body style="text-align: justify; text-justify: inter-word;">
                <xsl:variable name="title"  select="sp:scientificPublication/sp:metadata/sp:title"/>
                <h1 align="center">
                    <xsl:value-of select="$title"/>
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
                
                <!-- Rules for abstract -->
                <xsl:variable name="abstractProposals" select="sp:scientificPublication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID=concat($title, '/abstract')]]"/>
                <xsl:choose>
                    <xsl:when test="count($abstractProposals) &gt; 0">
                        
                        <div style="padding: 15px; border-style: solid; margin: 5px;">
                            <div>
                                <xsl:call-template name="TempApstractKeywords"></xsl:call-template> 
                            </div>
                            
                            <div style="margin-top: 10px;">
                                <b>Reviewer comments:</b>
                                <ul>
                                    <xsl:for-each select="$abstractProposals">
                                        <li style="margin-bottom: 5px;">
                                            <dt><xsl:value-of select="./rvw:metadata/rvw:reviewer"/>:</dt>
                                            <dd><xsl:value-of select="rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID=concat($title, '/abstract')]"/></dd>
                                        </li>
                                    </xsl:for-each>
                                </ul>
                            </div>
                            
                        </div>
                        
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="TempApstractKeywords"></xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
                
                
                <xsl:for-each select="sp:scientificPublication/sp:chapter">
                    <xsl:variable name="chapterId" select="@sp:id"/>
                    <xsl:variable name="chapterReviews" select="$publication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $chapterId]]"/>

                    <xsl:choose>
                        <xsl:when test="count($chapterReviews) &gt; 0">
                            <div style="padding: 15px; border-style: solid; margin: 5px;">
                                <div>
                                    <xsl:call-template name="chapter-content">
                                    </xsl:call-template>
                                </div>
                                
                                <div>
                                    <xsl:call-template name="reviewComments">
                                        <xsl:with-param name="reviews" select="$chapterReviews"></xsl:with-param>
                                        <xsl:with-param name="componentId" select="$chapterId"></xsl:with-param>
                                    </xsl:call-template>
                                </div>
                            </div>
                        </xsl:when>
                        
                        <xsl:otherwise>
                            <xsl:call-template name="chapter-content">
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
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
                <hr/>
                <h2>Reviewer grades:</h2><br/>
                
                <dl>
                	<xsl:for-each select="sp:scientificPublication/rvw:review">
                		<dt style="margin-top: 5px;">
                			<xsl:value-of select="rvw:metadata/rvw:reviewer"/>
                		</dt>
                		
                		<dd>
                			<ul>
                				<li>
                					Relevance of research problem: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:relevanceOfResearchProblem"/>
                				</li>
                				<li>
                					Introduction: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:introduction"/>
                				</li>
                				<li>
                					Conceptual quality: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:conceptualQuality"/>
                				</li>
                				<li>
                					Methodological quality: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:methodologicalQuality"/>
                				</li>
                				<li>
                					Results: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:results"/>
                				</li>
                				<li>
                					Discussion: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:discussion"/>
                				</li>
                				<li>
                					Readability: 
                					<xsl:value-of select="rvw:body/rvw:criteriaEvaluation/rvw:readability"/>
                				</li>
                				<li>
                					Overall: 
                					<xsl:value-of select="rvw:body/rvw:overallEvaluation"/>
                				</li>
                			</ul>
                		</dd>
                	</xsl:for-each>
                </dl>
                
                
                <h2>Free comments from reviewers:</h2>
                <table border="1" style="width: 100%;">
                    <thead>
                        <tr>
                            <th>Reviewer</th>
                            <th>Comment</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                        <xsl:for-each select="sp:scientificPublication/rvw:review">
                            <tr>
                                <td>
                                    <xsl:value-of select="rvw:metadata/rvw:reviewer"/>
                                </td>
                                <td>
                                    <xsl:value-of select="rvw:body/rvw:commentsToEditor"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
                <br/><br/>
                
            </body>
        </html>
    </xsl:template>
    
    <xsl:template name="TempApstractKeywords">
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
    </xsl:template>
    
    
    <xsl:template name="chapter-content">
        <xsl:apply-templates></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="sp:chapter/sp:title">
        <h2>
            <xsl:value-of select="."/>
        </h2>
    </xsl:template>
    
    <xsl:template match="sp:figure">
        <xsl:variable name="figureId" select="@sp:id"/>
        <xsl:variable name="figureReviews" select="ancestor::sp:scientificPublication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $figureId]]"/>
        <xsl:choose>
            <xsl:when test="count($figureReviews) &gt; 0">
                <div style="padding: 15px; border-style: solid; margin: 5px;">
                    <div>
                        <xsl:call-template name="figure-content">
                            <xsl:with-param name="figure" select="."></xsl:with-param>
                        </xsl:call-template> 
                    </div>
                    
                    <xsl:call-template name="reviewComments">
                        <xsl:with-param name="reviews" select="$figureReviews"></xsl:with-param>
                        <xsl:with-param name="componentId" select="$figureId"></xsl:with-param>
                    </xsl:call-template>
                </div>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:call-template name="figure-content">
                    <xsl:with-param name="figure" select="."></xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template name="figure-content">
        <xsl:param name="figure"/>
        <div style="text-align:center"><br/>
            <img>
                <xsl:attribute name="src">data:image/jpeg;charset=utf-8;base64,<xsl:value-of select="$figure/sp:image"/></xsl:attribute>
                <xsl:attribute name="width">
                    <xsl:value-of select="$figure/@width"/>
                </xsl:attribute>
                <xsl:attribute name="height">
                    <xsl:value-of select="$figure/@height"/>
                </xsl:attribute>
            </img><br/><span><i><xsl:value-of select="$figure/sp:description"/></i></span>
        </div><br/>
    </xsl:template>
    
    <xsl:template match="sp:table">
        <xsl:variable name="tebleId" select="@sp:id"/>
        <xsl:variable name="tableReviews" select="ancestor::sp:scientificPublication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $tebleId]]"/>
        <xsl:choose>
            <xsl:when test="count($tableReviews) &gt; 0">
                <div style="padding: 15px; border-style: solid; margin: 5px;">
                    <div>
                        <xsl:call-template name="table-content">
                            <xsl:with-param name="table" select="."></xsl:with-param>
                        </xsl:call-template>
                    </div>
                    
                    <div>
                        <xsl:call-template name="reviewComments">
                            <xsl:with-param name="reviews" select="$tableReviews"></xsl:with-param>
                            <xsl:with-param name="componentId" select="$tebleId"></xsl:with-param>
                        </xsl:call-template>
                    </div>
                </div>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:call-template name="table-content">
                    <xsl:with-param name="table" select="."></xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template name="table-content">
        <xsl:param name="table"></xsl:param>
        <div style="text-align: center;"><br/>
            <table border="1" align="center" style="width:80%">
                <xsl:for-each select="$table/sp:tableRow">
                    <tr>
                        <xsl:for-each select="./sp:tableCell">
                            <td>
                                <xsl:apply-templates></xsl:apply-templates>
                            </td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
            </table>
            <span><i><xsl:value-of select="$table/sp:tableDescription"/></i></span>
        </div><br/>
    </xsl:template>
    
    <xsl:template match="sp:paragraph">
        <xsl:variable name="paragraphId" select="@sp:id"/>
        <xsl:variable name="paragraphReviews" select="ancestor::sp:scientificPublication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $paragraphId]]"/>
        <xsl:choose>
            <xsl:when test="count($paragraphReviews) &gt; 0">
                <div style="padding: 15px; border-style: solid; margin: 5px;">
                    <div>
                        <xsl:call-template name="paragraph-content">
                        </xsl:call-template>
                    </div>
                    
                    <div>
                        <xsl:call-template name="reviewComments">
                            <xsl:with-param name="reviews" select="$paragraphReviews"></xsl:with-param>
                            <xsl:with-param name="componentId" select="$paragraphId"></xsl:with-param>
                        </xsl:call-template>
                    </div>
                </div>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:call-template name="paragraph-content"></xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="paragraph-content">
            <p>
                <xsl:apply-templates></xsl:apply-templates>
            </p>
    </xsl:template>
    
    <xsl:template match="sp:list">
        <xsl:variable name="listId" select="@sp:id"/>
        <xsl:variable name="listReviews" select="ancestor::sp:scientificPublication/rvw:review[rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $listId]]"/>
        <xsl:choose>
            <xsl:when test="count($listReviews) &gt; 0">
                <div style="padding: 15px; border-style: solid; margin: 5px;">
                    <div>
                        <xsl:call-template name="list-content">
                            <xsl:with-param name="list" select="."></xsl:with-param>
                        </xsl:call-template>
                    </div>
                    
                    <div>
                        <xsl:call-template name="reviewComments">
                            <xsl:with-param name="reviews" select="$listReviews"></xsl:with-param>
                            <xsl:with-param name="componentId" select="$listId"></xsl:with-param>
                        </xsl:call-template>
                    </div>
                </div>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:call-template name="list-content">
                    <xsl:with-param name="list" select="."></xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    
    <xsl:template name="list-content">
        <xsl:param name="list"></xsl:param>
        <xsl:choose>
            <xsl:when test="$list[@ordered='true']">
                <ol>
                    <xsl:for-each select="$list/*">
                        <li>  
                            <xsl:apply-templates select="."></xsl:apply-templates>
                        </li>
                    </xsl:for-each>
                </ol>
            </xsl:when>
            <xsl:otherwise>
                <ul>
                    <xsl:for-each select="$list/*">
                        <li>  
                            <xsl:apply-templates select="."></xsl:apply-templates>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="reviewComments">
        <xsl:param name="reviews"></xsl:param>
        <xsl:param name="componentId"></xsl:param>
        <div style="margin-top: 10px;">
            <b>Reviewer comments:</b>
            <ul>
                <xsl:for-each select="$reviews">
                    <li style="margin-bottom: 5px;">
                        <dt><xsl:value-of select="rvw:metadata/rvw:reviewer"/>:</dt>
                        <dd><xsl:value-of select="rvw:body/rvw:commentsToAuthor/rvw:proposedChange[@partID = $componentId]"/></dd>
                    </li>
                </xsl:for-each>
            </ul>
        </div>
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
    
</xsl:stylesheet>
