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
 * $Archive: /TOOL/RANGEHOOD/template/FANCY/XSL/SEQUENCE.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/09/10 7:09a $
 * $Revision: 1 $
 *****************************************************************************/
-->
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <TITLE>
          <xsl:text>Table </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="/DBObject/@name"/>
        </TITLE>
        <LINK REL="stylesheet" TYPE="text/css" HREF="../../HTML/css/rangehood.css" TITLE="Style"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/headers/header[@level='2']/node()"/>
        <div class="level1">
          <a name="table">
            <img border="0" src="../../HTML/gif/object/sequence.png" title="Sequence"/>
            <img border="0" src="../../HTML/gif/object/empty.gif"/>
            <xsl:value-of select="/DBObject/@name"/>
          </a>
        </div>
        <table>
        	<tr><td class="evenCell">Min Value</td><td class="oddCell"><xsl:value-of select="/DBObject/minValue"/></td></tr>
        	<tr><td class="evenCell">Max Value</td><td class="oddCell"><xsl:value-of select="/DBObject/maxValue"/></td></tr>
        	<tr><td class="evenCell">Increment By</td><td class="oddCell"><xsl:value-of select="/DBObject/incrementBy"/></td></tr>
        	<tr><td class="evenCell">Cycle Flag</td><td class="oddCell"><xsl:value-of select="/DBObject/cycleFlag"/></td></tr>
        	<tr><td class="evenCell">Order Flag</td><td class="oddCell"><xsl:value-of select="/DBObject/orderFlag"/></td></tr>
        	<tr><td class="evenCell">Cache Size</td><td class="oddCell"><xsl:value-of select="/DBObject/cacheSize"/></td></tr>
        	<tr><td class="evenCell">Last Number</td><td class="oddCell"><xsl:value-of select="/DBObject/lastNumber"/></td></tr>
        </table>
        <div class="separator"/>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footers/footer[@level='2']/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>      