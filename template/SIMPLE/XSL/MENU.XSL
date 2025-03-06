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
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/MENU.XSL $
 * $Author: Christopher Ho $
 * $Date: 26/09/10 2:12p $
 * $Revision: 2 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <link rel="stylesheet" type="text/css" href="../HTML/css/dtree.css"/>
      </head>
      <body>
          <div class="dtree">
          	<div id="level1">
          		<a href="DBOBJECTS.XML" target="main" id="level0">Database Objects</a>
          	</div>
          	<br/>
            <xsl:for-each select="/dbObjectTree/dbObjectTypes/dbObjectType">
            	<xsl:sort select="@typeName"/>
              <xsl:variable name="objectType">
                <xsl:value-of select="@typeName"/>
              </xsl:variable>
              <div id="level2">
                <a href="{$objectType}/{$objectType}S.XML" target="main">
                  <xsl:value-of select="concat($objectType,'S')"/>
                </a>
              </div>
              <table>
              	<xsl:for-each select="./dbObjectNames/name">
              		<xsl:sort select="."/>
		              <xsl:variable name="dbObject">
		                <xsl:value-of select="."/>
		              </xsl:variable>
              	  <tr>
              	  	<td id="level3">
              	  		<a href="{$objectType}/{$dbObject}.XML" target="main" class="node"><xsl:value-of select="$dbObject"/></a>
              	  	</td>
              	  </tr>
              	</xsl:for-each>  
              </table>
              <div class="separator">&#160;</div>
            </xsl:for-each>
          </div>        	
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
