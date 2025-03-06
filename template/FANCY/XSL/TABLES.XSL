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
 * $Archive: /TOOL/RANGEHOOD/template/FANCY/XSL/TABLES.XSL $
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
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <HEAD>
        <TITLE>All Tables</TITLE>
        <LINK REL="stylesheet" TYPE="text/css" HREF="../../HTML/css/rangehood.css" TITLE="Style"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/headers/header[@level='2']/node()"/>
        <div class="level1">
			<img border="0" src="../../HTML/gif/object/table.png" title="Table"/>
            <img border="0" src="../../HTML/gif/object/empty.gif"/>			
			<xsl:text>TABLES</xsl:text>
        </div>
        <table>
          <xsl:for-each select="/dbObjects/dbObjectNames/TABLE">
          	<xsl:sort select="."/>
            <xsl:variable name="href">
              <xsl:value-of select="concat(., '.XML')"/>
            </xsl:variable>
            <tr>
              <td class="oddCell">
                <a href="{$href}">
                  <xsl:value-of select="."/>
                </a>
              </td>
            </tr>
          </xsl:for-each>
        </table>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footers/footer[@level='2']/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
