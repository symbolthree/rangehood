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
 * $Archive: /TOOL/RANGEHOOD/template/FANCY/XSL/MENU.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:55p $
 * $Revision: 3 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz__'"/>
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ /'"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <link rel="stylesheet" type="text/css" href="../HTML/css/dtree.css"/>
        <script type="text/javascript" src="../HTML/js/dtree.js"></script>
      </head>
      <body>
        <xsl:if test="system-property('xsl:vendor')='Microsoft'">      	
	        <div class="dtree">
	          <p>
	            <a href="javascript: d.openAll();">
	              <img src="../HTML/gif/object/expand.png"/> open all</a> |
	            <a href="javascript: d.closeAll();">
	              <img src="../HTML/gif/object/collapse.png"/> close all</a>
	          </p>
	          <script type="text/javascript"><![CDATA[
	            var d = new dTree('d');
	            d.config.target='main';
	            d.add(0,-1,'Database Objects','DBOBJECTS.XML','Database Objects');
	            ]]>
		        <xsl:for-each select="/dbObjectTree/dbObjectTypes/dbObjectType">
		        	<xsl:sort select="@typeName"/>
	              <xsl:variable name="objectImg">
	                <xsl:value-of select="concat('../HTML/gif/object/', translate(@typeName, $uppercase, $lowercase), '.png')"/>
	              </xsl:variable>
	              <xsl:variable name="objectType">
	                <xsl:value-of select="@typeName"/>
	              </xsl:variable>
	              <xsl:variable name="objectTypeLower">
	                <xsl:value-of select="translate(@typeName, $uppercase, $lowercase)"/>
	              </xsl:variable>
	              <xsl:variable name="nodeID">
	                <xsl:value-of select="position()"/>
	              </xsl:variable>
	              <xsl:text>d.add(</xsl:text>
	              <xsl:value-of select="$nodeID"/>
	              <xsl:text>,0,'</xsl:text>
	              <xsl:value-of select="concat($objectType,'S')"/>
	              <xsl:text>','</xsl:text>
	              <xsl:value-of select="$objectType"/>
	              <xsl:text>/</xsl:text>
	              <xsl:value-of select="$objectType"/>
	              <xsl:text>S.XML','</xsl:text>
	              <xsl:value-of select="$objectTypeLower"/>
	              <xsl:text>');</xsl:text>
	              <xsl:for-each select="./dbObjectNames/name">
   			        	<xsl:sort select="."/>
	                <xsl:text>d.add(</xsl:text>
	                <xsl:value-of select="position() + 500*$nodeID"/>
	                <xsl:text>,</xsl:text>
	                <xsl:value-of select="$nodeID"/>
	                <xsl:text>,'</xsl:text>
	                <xsl:value-of select="."/>
	                <xsl:text>','</xsl:text>
	                <xsl:value-of select="$objectType"/>
	                <xsl:text>/</xsl:text>
	                <xsl:value-of select="."/>
	                <xsl:text>.XML','</xsl:text>
	                <xsl:value-of select="."/>
	                <xsl:text>','','../HTML/gif/object/</xsl:text>
	                <xsl:value-of select="$objectTypeLower"/>
	                <xsl:text>.png');</xsl:text>
	              </xsl:for-each>
	            </xsl:for-each>
	            <xsl:text>document.write(d);</xsl:text>
	            </script>
	        </div>
        </xsl:if>
		<!-- Firefox specific -->
        <xsl:if test="system-property('xsl:vendor')='Transformiix'">
          <div class="dtree2">
          	<div id="level1">
          		<img src="../HTML/gif/object/object.png"/>
          		<a href="DBOBJECTS.XML" target="main"> Database Objects</a>
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
              	  	<td><img src="../HTML/gif/tree/join.gif"/><img src="{$objectImg}"/></td>
              	  	<td>
              	  		<a href="{$objectType}/{$dbObject}.XML" target="main" class="node"><xsl:value-of select="$dbObject"/></a>
              	  	</td>
              	  </tr>
              	</xsl:for-each>  
              </table>
              <div class="separator"/>
            </xsl:for-each>
          </div>        	
        </xsl:if>        
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
