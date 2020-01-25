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
                    Notification regarding scientific publication titled: 
                    "<xsl:value-of select="n:notification/n:publicationName"/>"<br></br>
                </h3>
                Notification type: <i><xsl:value-of select="n:notification/n:notificationType"/></i>
                <br></br><br></br>
                <b>Information about sender/reciever:</b>
                    <table border="1">
                        <tr>
                            <th></th>
                            <th>Name</th>
                            <th>Role</th> 
                            <th>Email</th>
                        </tr>
                        <tr>
                            <td>Sender</td>
                            <td><xsl:value-of select="n:notification/n:sender/n:name"/></td>
                            <td><xsl:value-of select="n:notification/n:sender/n:role"/></td> 
                            <td><xsl:value-of select="n:notification/n:sender/n:email"/></td>
                        </tr>
                        <tr>
                            <td>Reciever</td>
                            <td><xsl:value-of select="n:notification/n:reciever/n:name"/></td>
                            <td><xsl:value-of select="n:notification/n:reciever/n:role"/></td> 
                            <td><xsl:value-of select="n:notification/n:reciever/n:email"/></td>
                        </tr>
                    </table>
                <br></br>
                <b>Content:</b>
                <p><xsl:value-of select="n:notification/n:content"/></p>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
