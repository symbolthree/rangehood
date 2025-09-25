<!--
/******************************************************************************
 * ≡ RANGEHOOD ≡
 * Copyright (C) 2009-2025 Christopher Ho
 * All Rights Reserved, http://www.symbolthree.com
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
 * E-mail: christopher.ho@symbolthree.com
******************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz__'"/>
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ /'"/>
  <xsl:template match="/">  
    <html>
      <head>
	    <title>All Database Objects</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>	  
        <link rel="stylesheet" type="text/css" href="../HTML/css/dtree.css"/>
        <script type="text/javascript" src="../HTML/js/dtree.js"><span>&#160;</span></script>
      </head>
      <body>
        <div class="dtree2">
          <div id="level1">
            <img src="../HTML/gif/object/object.png"/>
            <a href="DBOBJECTS.HTML" target="main"> Database Objects</a>
          </div>
          <br/>
          <xsl:for-each select="/dbObjectTree/dbObjectTypes/dbObjectType">
            <xsl:sort select="@typeName"/>
            <xsl:variable name="objectType">
              <xsl:value-of select="@typeName"/>
            </xsl:variable>
            <xsl:variable name="objectImg">
              <xsl:value-of select="concat('../HTML/gif/object/', translate(@typeName, $uppercase, $lowercase), '.png')"/>
            </xsl:variable>
            <div id="level2">
              <img src="../HTML/gif/tree/folder.gif"/>
              <a href="{$objectType}/{$objectType}S.HTML" target="main">
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
                  <td><img src="../HTML/gif/tree/join.gif"/><img src="{$objectImg}"/></td>
                  <td>
                    <a href="{$objectType}/{$dbObject}.HTML" target="main" class="node"><xsl:value-of select="$dbObject"/></a>
                  </td>
                </tr>
              </xsl:for-each>
            </table>
            <div class="separator"/>
          </xsl:for-each>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
