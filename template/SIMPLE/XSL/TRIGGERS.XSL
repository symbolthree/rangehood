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
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/TRIGGERS.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:59p $
 * $Revision: 3 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <HTML>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <HEAD>
        <TITLE>All Triggers</TITLE>
        <LINK REL="stylesheet" TYPE="text/css" HREF="../../HTML/css/rangehood.css" TITLE="Style"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/header/node()"/>
        <div class="level1">
      	  <span class="objType">TRIGGERS</span>
        </div>
        <table>
          <xsl:for-each select="/dbObjects/dbObjectNames/TRIGGER">
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
        <xsl:copy-of select="$footer/footer/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
