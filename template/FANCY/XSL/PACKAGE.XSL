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
  <xsl:variable name="objName" select="concat(/DBObject/@schema, '.' , /DBObject/@name)"/>
  <xsl:template match="/">
    <HTML>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <HEAD>
        <TITLE>
          <xsl:text>Package </xsl:text>
          <xsl:value-of select="/DBObject/@schema"/>
          <xsl:text>.</xsl:text>
		  <xsl:value-of select="$objName"/>
        </TITLE>
        <LINK REL="stylesheet" TYPE="text/css" HREF="../../HTML/css/rangehood.css"/>
      </HEAD>
      <BODY>
        <xsl:variable name="header" select="document('../../HTML/HEADER.XML',/)"/>
        <xsl:copy-of select="$header/headers/header[@level='2']/node()"/>
        <div class="level1">
          <a name="package">
            <img border="0" src="../../HTML/gif/object/package.png" title="Package"/>
            <img src="../../HTML/gif/object/empty.gif"/>
			<span class="objType">PACKAGE</span>
			<xsl:value-of select="$objName"/>
          </a>
          <a href="{$objName}.SQL" target="_new" class="source">
            <img class="text" src="../../HTML/gif/object/text.png"/>
          </a>
        </div>
        <div class="separator"/>
        <div class="level2">
          <a name="summary">
            <img border="0" src="../../HTML/gif/object/summary.png" title="Package Summary"/>
            <img src="../../HTML/gif/object/empty.gif"/>Package Summary
          </a>
        </div>
        <table cellpadding="5" border="0" cellspacing="0">
          <tr>
            <th class="tableTitle" width="150px">Subprogram Type</th>
            <th class="tableTitle" width="200px">Subprogram Name</th>
          </tr>
          <xsl:apply-templates select="/DBObject/subPrograms/subProgram"/>
        </table>
        <xsl:choose>
          <xsl:when test="count(DBObject/types/type[@objectType='PL/SQL RECORD']) > 0">
            <div class="separator"/>
            <table cellpadding="5" border="0" cellspacing="0">
              <tr>
                <th class="tableTitle" width="150px">Datatype</th>
                <th class="tableTitle" width="200px">Datatype Name</th>
              </tr>
              <xsl:apply-templates select="/DBObject/types/type"/>
            </table>
          </xsl:when>
        </xsl:choose>
        <div class="separator"/>
        <div class="level2">
          <a name="Details">
            <img border="0" src="../../HTML/gif/object/summary.png" title="Subprogram Details"/>
            <img src="../../HTML/gif/object/empty.gif"/>SubProgram Details
          </a>
        </div>
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            S U B P R O G R A M  D E T A I L S
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <xsl:for-each select="DBObject/subPrograms/subProgram">
          <xsl:variable name="subprogramType">
            <xsl:value-of select="@objectType"/>
          </xsl:variable>
          <xsl:variable name="typePng">
            <xsl:value-of select="concat('../../HTML/gif/object/', translate(@objectType, $uppercase, $lowercase),'.png')"/>
          </xsl:variable>
          <xsl:variable name="subprogramName">
            <xsl:value-of select="@name"/>
          </xsl:variable>
          <br/>
          <div class="level3">
            <a name="{$subprogramName}">
              <img border="0" src="{$typePng}" title=""/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@objectType"/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@name"/>
            </a>
          </div>
          <div class="comment">
            <xsl:value-of select="comment"/>
          </div>
          <div class="inlineComment">
            <table>
              <xsl:for-each select="comment/tags/tag[@name='param']">
                <tr>
                  <td width="150px">
                    <xsl:value-of select="@value"/>
                  </td>
                  <td width="500px">
                    <xsl:value-of select="@comment"/>
                  </td>
                </tr>
              </xsl:for-each>
            </table>
          </div>
          <br/>
          <table cellpadding="5" border="0" cellspacing="0">
            <tr class="tableTitle">
              <th width="30px">No</th>
              <th width="250px">Argument</th>
              <th width="100px">In / Out</th>
              <th width="275px">Data Type</th>
              <th width="200px">Defaulted</th>
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
              <xsl:variable name="typeIcon">
                <xsl:value-of select="concat('../../HTML/gif/object/', translate(@origType, $uppercase, $lowercase),'.png')"/>
              </xsl:variable>
              <xsl:variable name="inoutIcon">
                <xsl:value-of select="concat('../../HTML/gif/object/', translate(@inout, $uppercase, $lowercase),'.png')"/>
              </xsl:variable>
              <xsl:variable name="argPos">
                <xsl:choose>
                  <xsl:when test="@position > 0">
                    <xsl:value-of select="@position"/>
                   </xsl:when>
                </xsl:choose>
              </xsl:variable>
              <tr class="{$bgcolor}">
                <td class="num">
                  <xsl:value-of select="$argPos"/>
                </td>
                <td>
                  <xsl:value-of select="@name"/>
                </td>
                <td>
                  <img src="{$inoutIcon}"/>
                  <xsl:value-of select="@inout"/>
                </td>
                <td>
                  <img src="{$typeIcon}"/>
                  <img src="../../HTML/gif/object/empty.gif"/>
                  <xsl:value-of select="@type"/>
                </td>
                <td>
                  <xsl:value-of select="@defaulted"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </xsl:for-each>
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                       T Y P E
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <xsl:for-each select="DBObject/types/type[@objectType='PL/SQL RECORD']">
          <xsl:variable name="datatype">
            <xsl:value-of select="@objectType"/>
          </xsl:variable>
          <xsl:variable name="typePng">
            <xsl:value-of select="concat('../../HTML/gif/object/', translate(@objectType, $uppercase, $lowercase),'.png')"/>
          </xsl:variable>
          <xsl:variable name="typeName">
            <xsl:value-of select="@name"/>
          </xsl:variable>
          <br/>
          <div class="level3">
            <a name="{$typeName}">
              <img border="0" src="{$typePng}" title=""/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@objectType"/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@name"/>
            </a>
          </div>
          <table cellpadding="3" border="0" cellspacing="0">
            <tr class="tableTitle">
              <th width="250px">Column</th>
              <th width="250px">Type</th>
              <th width="100px">Length</th>
              <th width="150px">Default</th>
            </tr>
            <xsl:for-each select="columns/column">
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
              <xsl:variable name="typeIcon">
                <xsl:value-of select="concat('../../HTML/gif/object/', translate(@type, $uppercase, $lowercase),'.png')"/>
              </xsl:variable>
              <tr class="{$bgcolor}">
                <td>
                  <xsl:value-of select="@name"/>
                </td>
                <td>
                  <img src="{$typeIcon}"/>
                  <img src="../../HTML/gif/object/empty.gif"/>
                  <xsl:value-of select="@type"/>
                </td>
                <td>
                  <xsl:value-of select="$typeLength"/>
                </td>
                <td>
                  <xsl:value-of select="@defaultValue"/>
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </xsl:for-each>
        <br/>
        <xsl:for-each select="DBObject/types/type[@objectType='PL/SQL TABLE']">
          <xsl:variable name="datatype">
            <xsl:value-of select="@objectType"/>
          </xsl:variable>
          <xsl:variable name="typePng">
            <xsl:value-of select="concat('../../HTML/gif/object/', translate(@objectType, $uppercase, $lowercase),'.png')"/>
          </xsl:variable>
          <xsl:variable name="typeName">
            <xsl:value-of select="@name"/>
          </xsl:variable>
          <div class="level3">
            <a name="{$typeName}">
              <img border="0" src="{$typePng}" title=""/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@objectType"/>
              <img src="../../HTML/gif/object/empty.gif"/>
              <xsl:value-of select="@name"/>
            </a>
            <div class="oddCell">
              <xsl:text>TABLE OF </xsl:text>
              <xsl:value-of select="columns/column/@name"/>
              <xsl:text> INDEX BY BINARY_INTEGER</xsl:text>
            </div>
          </div>
          <br/>
        </xsl:for-each>
        <xsl:variable name="footer" select="document('../../HTML/FOOTER.XML',/)"/>
        <xsl:copy-of select="$footer/footers/footer[@level='2']/node()"/>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="/DBObject/subPrograms/subProgram">
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
      <xsl:value-of select="concat('../../HTML/gif/object/', translate(@objectType, $uppercase, $lowercase),'.png')"/>
    </xsl:variable>
    <xsl:variable name="subprogramName">
      <xsl:value-of select="@name"/>
    </xsl:variable>
    <tr class="{$bgcolor}">
      <td>
        <img src="{$typePng}"/>
        <img src="../../HTML/gif/object/empty.gif"/>
        <xsl:value-of select="@objectType"/>
      </td>
      <td>
        <a href="#{$subprogramName}">
          <xsl:value-of select="@name"/>
        </a>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="/DBObject/types/type">
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
      <xsl:value-of select="concat('../../HTML/gif/object/', translate(@objectType, $uppercase, $lowercase),'.png')"/>
    </xsl:variable>
    <xsl:variable name="subprogramName">
      <xsl:value-of select="@name"/>
    </xsl:variable>
    <tr class="{$bgcolor}">
      <td>
        <img src="{$typePng}"/>
        <img src="../../HTML/gif/object/empty.gif"/>
        <xsl:value-of select="@objectType"/>
      </td>
      <td>
        <a href="#{$subprogramName}">
          <xsl:value-of select="@name"/>
        </a>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
