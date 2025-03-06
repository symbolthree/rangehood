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
 * $Archive: /TOOL/RANGEHOOD/template/SIMPLE/XSL/TABLE.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:59p $
 * $Revision: 2 $
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
        <xsl:copy-of select="$header/header/node()"/>
        <div class="level1">
          	<span class="objType">TABLE</span>
            <xsl:value-of select="/DBObject/@name"/>
        </div>
        <pre class="comment">
          <xsl:value-of select="/DBObject/comment/content"/>
        </pre>
        <div class="separator"/>
        <div class="level2">
          <a name="column">COLUMNS</a>
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
          </tr>
          <!-- columns for loop -->
          <xsl:apply-templates select="/DBObject/columns/column"/>
        </table>
        <div class="separator">&#160;</div>
        <xsl:choose>
          <xsl:when test="count(DBObject/indices/index) > 0">
            <div class="level2">
              <a name="index">INDEX</a>
            </div>
            <table cellpadding="5" border="0" cellspacing="0">
              <tr>
                <th class="tableTitle">Name</th>
                <th class="tableTitle">Type</th>
                <th class="tableTitle">Uniqueness</th>
                <th class="tableTitle">Column</th>
              </tr>
              <!-- index for loop -->
              <xsl:apply-templates select="/DBObject/indices/index"/>
            </table>
            <div class="separator">&#160;</div>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="count(DBObject/constraints/constraint) > 0">
            <div class="level2">
              <a name="index">CONSTRAINT</a>
            </div>
            <table cellpadding="5" border="0" cellspacing="0">
              <tr>
                <th class="tableTitle" width="100px">Name</th>
                <th class="tableTitle" width="100px">Type</th>
                <th class="tableTitle" width="100px">Column</th>
                <th class="tableTitle" width="300px">Details</th>
              </tr>
              <!-- constraint for loop -->
              <xsl:apply-templates select="/DBObject/constraints/constraint"/>
            </table>
            <div class="separator">&#160;</div>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="count(DBObject/triggers/trigger) > 0">
            <div class="level2">
              <a name="trigger">TRIGGER</a>
            </div>
            <table cellpadding="5" border="0" cellspacing="0">
              <tr>
                <th class="tableTitle" width="100px">Name</th>
                <th class="tableTitle" width="30px">Owner</th>
                <th class="tableTitle" width="100px">Type</th>
                <th class="tableTitle" width="100px">Event</th>
              </tr>
              <!-- trigger for loop -->
              <xsl:apply-templates select="/DBObject/triggers/trigger"/>
            </table>
          </xsl:when>
        </xsl:choose>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footer/node()"/>
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
        <xsl:value-of select="@columnID"/>
	  </td>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="@type"/>
      </td>
      <td>
        <xsl:value-of select="$typeLength"/>
      </td>
      <xsl:if test="@nullable='Y'">
        <td class="{$bgcolor}"/>
      </xsl:if>
      <xsl:if test="@nullable='N'">
        <td align="center">Yes</td>
      </xsl:if>
      <td>
        <xsl:value-of select="@defaultValue"/>
      </td>
      <td>
        <xsl:value-of select="comment"/>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="/DBObject/indices/index">
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
      <td valign="top">
        <xsl:value-of select="@name"/>
      </td>
      <td valign="top">
        <xsl:value-of select="@type"/>
      </td>
      <td valign="top">
        <xsl:value-of select="@uniqueness"/>
      </td>
      <td>
        <table cellpadding="0" border="0" cellspacing="0">
          <xsl:for-each select="columns/string">
            <tr class="oddCell">
              <td>
                <xsl:value-of select="."/>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="/DBObject/constraints/constraint">
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
    <xsl:variable name="keyType">
      <xsl:choose>
        <xsl:when test="@type='P'">
          <xsl:value-of select="'Primary Key'"/>
        </xsl:when>
        <xsl:when test="@type='R'">
          <xsl:value-of select="'Foreign Key'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'Column'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr class="{$bgcolor}">
      <td valign="top">
<!--      	
        <xsl:choose>
          <xsl:when test="@type='P' or @type='R'">
            <img src="../../HTML/gif/object/key.png"/>
          </xsl:when>
          <xsl:otherwise>
            <img src="../../HTML/gif/object/constraint.png"/>
          </xsl:otherwise>
        </xsl:choose>
-->        
        <xsl:value-of select="@name"/>
      </td>
      <td valign="top">
        <xsl:value-of select="$keyType"/>
      </td>
      <td valign="top">
        <table cellpadding="0" border="0" cellspacing="0">
          <xsl:for-each select="columns/string">
            <tr class="oddCell">
              <td>
                <xsl:value-of select="."/>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </td>
      <td valign="top">
        <xsl:if test="@type='R'">
        	<!--
          <img src="../../HTML/gif/object/reference.png"/>
          -->
          <table cellpadding="0" border="0" cellspacing="0">
            <xsl:for-each select="rcolumn">
              <tr class="oddCell">
                <td>
                  <xsl:value-of select="string"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </xsl:if>
        <xsl:value-of select="details"/>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="/DBObject/triggers/trigger">
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
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="@schema"/>
      </td>
      <td>
        <xsl:value-of select="@type"/>
      </td>
      <td>
        <xsl:value-of select="@event"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
