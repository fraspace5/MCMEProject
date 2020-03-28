/*
 *  Copyright (C) 2020 MCME (Fraspace5)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcme.mcmeproject.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.server.v1_14_R1.SharedConstants;

/**
 *
 * @author Fraspace5
 */
public class FontCustom {

    public static void ModifyAllowedCharacters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = SharedConstants.class.getDeclaredField("allowedCharacters");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        String oldallowedchars = new String((char[]) field.get(null));
        String suits = "\u25A0\u00A7";
        String newvalue = oldallowedchars + suits;
        field.set(null, newvalue.toCharArray());
    }

}
