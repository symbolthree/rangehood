/******************************************************************************
 *
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
 *
******************************************************************************/

package symbolthree.oracle.doc.object;

import java.util.ArrayList;

public class Method {

  private String name;
  private ArrayList<Argument> args = new ArrayList<>();

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addArg(Argument arg) {
    args.add(arg);
  }

  public void setArgs(ArrayList<Argument> args) {
    this.args = args;
  }

  public ArrayList<Argument> getArgs() {
    return args;
  }
}
