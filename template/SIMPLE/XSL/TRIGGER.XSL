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
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/TRIGGER.XSL $
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
        <title>
          <xsl:text>Trigger </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="/DBObject/@name"/>
        </title>
        <link type="text/css" href="../../HTML/css/rangehood.css" rel="stylesheet"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/header/node()"/>
        <div class="level1">
		  <span class="objType">TRIGGER</span>
          <xsl:value-of select="/DBObject/@name"/>
        </div>
        <pre class="comment">
          <xsl:value-of select="/DBObject/comment/content"/>
        </pre>
        <div class="separator"/>
        <table cellpadding="5" border="0" cellspacing="0">
          <tr>
            <td class="evenCell" width="150px">Triggering Event</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="/DBObject/event"/>
            </td>
          </tr>
          <tr>
            <td class="evenCell" width="150px">Trigger Type</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="/DBObject/type"/>
            </td>
          </tr>
          <tr>
            <td class="evenCell" width="150px">Base Object Type</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="/DBObject/baseObjectType"/>
            </td>
          </tr>
          <tr>
            <td class="evenCell" width="150px">Table Name</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="concat(/DBObject/tableOwner, '.', /DBObject/tableName)"/>
            </td>
          </tr>
          <tr>
            <td class="evenCell" width="150px">Referencing Names</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="/DBObject/referencingNames"/>
            </td>
          </tr>
          <tr>
            <td class="evenCell" width="150px">When Clause</td>
            <td class="oddCell" width="300px">
              <xsl:value-of select="/DBObject/whenClause"/>
            </td>
          </tr>
        </table>
        <div class="separator"/>
        <div class="level2">
          <a name="source">Trigger Source</a>
        </div>
        <pre>
          <xsl:value-of select="/DBObject/body"/>
        </pre>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footer/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
