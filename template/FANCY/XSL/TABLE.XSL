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
 * $Archive: /TOOL/RANGEHOOD/template/FANCY/XSL/TABLE.XSL $
 * $Author: Christopher Ho $
 * $Date: 11/10/10 1:55p $
 * $Revision: 4 $
 *****************************************************************************/
-->
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
  <xsl:output method="html" indent="yes"/>
  <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz__'"/>
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ /'"/> 
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
            <img border="0" src="../../HTML/gif/object/table.png" title="Table"/>
            <img border="0" src="../../HTML/gif/object/empty.gif"/>
            <xsl:value-of select="/DBObject/@name"/>
          </a>
        </div>
        <pre class="comment">
          <xsl:value-of select="/DBObject/comment/content"/>
        </pre>
        <div class="separator"/>
        <div class="level2">
          <a name="column">
            <img border="0" src="../../HTML/gif/object/column.png"/>&#160;COLUMNS</a>
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
        <div class="separator"/>
        <xsl:choose>
          <xsl:when test="count(DBObject/indices/index) > 0">
            <div class="level2">
              <a name="index">
                <img border="0" src="../../HTML/gif/object/index.png"/>&#160;INDEX</a>
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
              <a name="index">
                <img border="0" src="../../HTML/gif/object/constraint.png"/>&#160;CONSTRAINT</a>
            </div>
            <table cellpadding="5" border="0" cellspacing="0">
              <tr>
                <th class="tableTitle" width="100px">Name</th>
                <th class="tableTitle" width="100px">Type</th>
                <th class="tableTitle" width="100px">Column</th>
                <th class="tableTitle" width="350px">Details</th>
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
              <a name="trigger">
                <img border="0" src="../../HTML/gif/object/trigger.png"/>&#160;TRIGGER</a>
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
      <xsl:value-of select="concat('../../HTML/gif/object/', translate(@type, $uppercase, $lowercase), '.png')"/>
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
        <img src="../../HTML/gif/object/index.png"/>
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
        <xsl:choose>
          <xsl:when test="@type='P' or @type='R'">
            <img src="../../HTML/gif/object/key.png"/>
          </xsl:when>
          <xsl:otherwise>
            <img src="../../HTML/gif/object/constraint.png"/>
          </xsl:otherwise>
        </xsl:choose>
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
          <table cellpadding="0" border="0" cellspacing="0">
            <xsl:for-each select="rcolumns">
              <tr class="oddCell">
                <td>
                  <xsl:value-of select="string"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
          <img src="../../HTML/gif/object/reference.png"/>          
        </xsl:if>
        <xsl:if test="@type!='R'">
          <xsl:value-of select="details"/>
        </xsl:if>          
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
        <img src="../../HTML/gif/object/trigger.png"/>
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
