/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

package org.trypticon.hex.formats.classfile.constantpool;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.formats.classfile.AbstractClassFileStructure;

/**
 * "cp_info" structure detailing a single entry in the constant pool.
 *
 * @author trejkaz
 */
public class CpInfo extends AbstractClassFileStructure {
    public CpInfo() {
        super("cp_info");
    }

    public Annotation drop(Binary binary, long position) {
        Annotation tag = u1(position, "tag");

        Structure structure = structureForTag(((Number) tag.interpret(binary)).intValue());
        return structure.drop(binary, position);
    }

    private Structure structureForTag(int tagValue) {
        switch (tagValue) {
            case 1: //CONSTANT_Utf8
                return new ConstantUtf8Info();
            case 3: //CONSTANT_Integer
                return new ConstantIntegerInfo();
            case 4: //CONSTANT_Float
                return new ConstantFloatInfo();
            case 5: //CONSTANT_Long
                return new ConstantLongInfo();
            case 6: //CONSTANT_Double
                return new ConstantDoubleInfo();
            case 7: //CONSTANT_Class
                return new ConstantClassInfo();
            case 8: //CONSTANT_String
                return new ConstantStringInfo();
            case 9: //CONSTANT_Fieldref
                return new ConstantFieldrefInfo();
            case 10: //CONSTANT_Methodref
                return new ConstantMethodrefInfo();
            case 11: //CONSTANT_InterfaceMethodref
                return new ConstantInterfaceMethodrefInfo();
            case 12: //CONSTANT_NameAndType
                return new ConstantNameAndTypeInfo();
            default:
                throw new IllegalArgumentException(String.format("Unsupported tag type: 0x%02x", tagValue));
        }
    }
}
