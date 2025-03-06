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
 * $Archive: /TOOL/RANGEHOOD/template/FANCY/XSL/VIEW.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:55p $
 * $Revision: 3 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz__'"/>
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ /'"/> 
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <TITLE>
          <xsl:text>View </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="/DBObject/@name"/>
        </TITLE>
        <link type="text/css" href="../../HTML/css/rangehood.css" rel="stylesheet"/>
        <link type="text/css" href="../../HTML/css/shCore.css" rel="stylesheet"/>
        <link type="text/css" href="../../HTML/css/shThemeDefault.css" rel="stylesheet"/>
        <script type="text/javascript" src="../../HTML/js/shCore.js"/>
        <script type="text/javascript" src="../../HTML/js/shBrushSql.js"/>
        <script type="text/javascript">
        	<![CDATA[
		      SyntaxHighlighter.config.clipboardSwf = '../../HTML/js/clipboard.swf';
		      SyntaxHighlighter.all();
		      ]]>
		    </script>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/headers/header[@level='2']/node()"/>
        <div class="level1">
          <a name="view">
            <img border="0" src="../../HTML/gif/object/view.png" title="View"/>
            <img src="../../HTML/gif/object/empty.gif"/>
            <xsl:value-of select="/DBObject/@name"/>
          </a>
        </div>
        <pre class="comment">
          <xsl:value-of select="/DBObject/comment/content"/>
        </pre>
        <div class="separator"/>
        <div class="level2">
          <a name="column">
            <img border="0" src="../../HTML/gif/object/column.png"/>COLUMNS</a>
        </div>
        <table cellpadding="5" border="0" cellspacing="0">
          <tr>
            <th class="tableTitle" width="30px">No</th>
            <th class="tableTitle" width="100px">Name</th>
            <th class="tableTitle" width="100px">Type</th>
            <th class="tableTitle" width="50px">Length</th>
            <th class="tableTitle" width="100px">Mandatory</th>
            <th class="tableTitle" width="100px">Default</th>
            <th class="tableTitle" width="200px">Comment</th>
            <th class="tableTitle" width="200px">Source Column</th>
          </tr>
          <!-- columns for loop -->
          <xsl:apply-templates select="/DBObject/columns/column"/>
        </table>
        <div class="separator">&#160;</div>
        <div class="level2">
          <a name="source">
            <img border="0" src="../../HTML/gif/object/source.png"/>&#160;Source</a>
        </div>
        <div>
          <pre class="brush:sql">
            <xsl:value-of select="/DBObject/source"/>
          </pre>
        </div>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footers/footer[@level='2']/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="/DBObject/columns/column">
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
    <xsl:variable name="typePng">
      <xsl:value-of select="concat('../../HTML/gif/object/', translate(@type, $uppercase, $lowercase),'.png')"/>
    </xsl:variable>
    <xsl:variable name="typeLength">
      <xsl:choose>
        <xsl:when test="@type='VARCHAR2'">
          <xsl:value-of select="concat('(', @length ,')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="''"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr class="{$bgcolor}">
      <td class="num">
        <xsl:value-of select="@columnID"/>
      </td>
      <td>
        <img src="../../HTML/gif/object/column.png"/>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <img src="{$typePng}"/>
        <xsl:value-of select="@type"/>
      </td>
      <td>
        <xsl:value-of select="$typeLength"/>
      </td>
      <xsl:if test="@nullable='Y'">
        <td class="{$bgcolor}"/>
      </xsl:if>
      <xsl:if test="@nullable='N'">
        <td align="center">
          <img src="../../HTML/gif/object/check.png"/>
        </td>
      </xsl:if>
      <td>
        <xsl:value-of select="@defaultValue"/>
      </td>
      <td>
        <xsl:value-of select="comment"/>
      </td>
      <td>
        <xsl:value-of select="@sourceColumn"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
