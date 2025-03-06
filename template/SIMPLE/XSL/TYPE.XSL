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
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/TYPE.XSL $
 * $Author: Christopher Ho $
 * $Date: 24/09/10 2:41p $
 * $Revision: 1 $
 *****************************************************************************/
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <HTML>
      <HEAD>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <TITLE>
          <xsl:text>Type </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="/DBObject/@name"/>
        </TITLE>
        <link type="text/css" href="../../HTML/css/rangehood.css" rel="stylesheet"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/header/node()"/>
        <div class="level1">
          <a name="view"><xsl:value-of select="/DBObject/@name"/></a>
        </div>
        <div class="separator"/>
        <table>
        	<tr>
        		<td class="evenCell">Type Code</td>
        	  <td class="oddCell"><xsl:value-of select="/DBObject/typeCode"/></td>
        	</tr>
        	<tr>
        		<td class="evenCell">No. of Attribute</td>
        		<td class="oddCell"><xsl:value-of select="/DBObject/noOfAttribute"/></td>
        	</tr>
        	<tr>
        		<td class="evenCell">No. of Method</td>
        		<td class="oddCell"><xsl:value-of select="/DBObject/noOfMethod"/></td>
        	</tr>
        	<tr>
        		<td class="evenCell">Predefined</td>
        		<td class="oddCell"><xsl:value-of select="/DBObject/predefined"/></td>
        	</tr>
        	<tr>
        		<td class="evenCell">Final</td>
        		<td class="oddCell"><xsl:value-of select="/DBObject/isFinal"/></td>
        	</tr>
        	<tr>
        		<td class="evenCell">Instantiable</td>
        		<td class="oddCell"><xsl:value-of select="/DBObject/instantiable"/></td>
        	</tr>
          <xsl:choose>
	        	<xsl:when test="/DBObject/typeCode='COLLECTION'">
	        		<tr>
	          		<td class="evenCell">Table of </td>
		  	      	<td class="oddCell">
			        	<xsl:value-of select="/DBObject/elemTypeName"/></td>
			        </tr>
	       		</xsl:when>
          </xsl:choose>
       	</table>
        <div class="separator"/>

        <xsl:choose>
        	<xsl:when test="/DBObject/typeCode='OBJECT'">
		        <div class="level2">
		          <a name="attribute">ATTRIBUTES</a>
		        </div>
		        <table cellpadding="5" border="0" cellspacing="0">
		          <tr>
		            <th class="tableTitle" width="100px">Name</th>
		            <th class="tableTitle" width="100px">Type</th>
		            <th class="tableTitle" width="50px">Length</th>
		          </tr>
		          <!-- attribute for loop -->
		          <xsl:apply-templates select="/DBObject/attributes/column"/>
		        </table>
            
            <xsl:choose><xsl:when test="count(DBObject/types/type[@objectType='PL/SQL RECORD']) > 0">
		          <div class="separator"/>
		          <div class="level2">
		            <a name="method">METHODS</a>
		          </div>
		          <div class="separator"/>
			        <xsl:for-each select="/DBObject/methods/method">
				        <div class="level3">
				          <a name="method"><xsl:value-of select="@name"/>
				          </a>
				        </div>
  		          <table cellpadding="5" border="0" cellspacing="0">
				          <tr>
				            <th class="tableTitle" width="150px">Parameter</th>
				            <th class="tableTitle" width="150px">Type</th>
				          </tr>
			            <xsl:for-each select="args/argument">
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
			            		<td><xsl:value-of select="@name"/></td>
			            		<td><xsl:value-of select="@type"/></td>
			            	</tr>
			           	</xsl:for-each>
  	            </table>
		          </xsl:for-each>
		        </xsl:when></xsl:choose>
		        </xsl:when>
          </xsl:choose>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footer/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="/DBObject/attributes/column">
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
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="@type"/>
      </td>
      <td>
        <xsl:value-of select="$typeLength"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
