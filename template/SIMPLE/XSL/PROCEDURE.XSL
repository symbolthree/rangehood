<!--
/******************************************************************************
 *
 * SYMPLiK RANGEHOOD
 * Copyright (C) 2010 Christopher Ho / SYMPLiK Tech. Co. Ltd.
 * All Rights Reserved, http://www.symplik.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: christopher.ho@symplik.com
 *
 * ================================================
 *
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/PROCEDURE.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:59p $
 * $Revision: 2 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <HTML>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <HEAD>
        <TITLE>
          <xsl:text>Procedure </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="/DBObject/@name"/>
        </TITLE>
        <LINK REL="stylesheet" TYPE="text/css" HREF="../../HTML/css/rangehood.css" TITLE="Style"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/header/node()"/>
        <div class="level1">
      	  <span class="objType">PROCEDURE</span>
          <xsl:value-of select="/DBObject/@name"/>
        </div>
        <pre class="comment">
          <xsl:value-of select="/DBObject/comment/content"/>
        </pre>
        <div class="inlineComment">
          <table>
            <xsl:for-each select="/DBObject/comment/tags/tag[@name='param']">
              <tr>
                <td width="150px">
                  <xsl:value-of select="@value"/>
                </td>
                <td width="500px">
                  <xsl:value-of select="@comment"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </div>
        <div class="separator"/>
        <table cellpadding="5" border="0" cellspacing="0">
          <tr class="tableTitle">
		    <th width="30px">No</th>
            <th width="150px">Argument</th>
            <th width="100px">In / Out</th>
            <th width="150px">Data Type</th>
            <th width="200px">Default</th>
          </tr>
          <xsl:for-each select="/DBObject/args/argument">
            <xsl:variable name="bgcolor">
              <xsl:choose>
                <xsl:when test="position() mod 2">
                  <xsl:value-of select="'evenCell'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'oddCell'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <tr class="{$bgcolor}">
			  <td class="num">
                <xsl:value-of select="@position"/>
			  </td>	
              <td>
                <xsl:value-of select="@name"/>
              </td>
              <td>
                <xsl:value-of select="@inout"/>
              </td>
              <td>
                <xsl:value-of select="@type"/>
              </td>
              <td>
                <xsl:value-of select="@defaultValue"/>
              </td>
            </tr>
          </xsl:for-each>
        </table>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footer/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
