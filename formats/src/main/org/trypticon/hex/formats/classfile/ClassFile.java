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

package org.trypticon.hex.formats.classfile;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.classfile.constantpool.CpInfo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Java class file structure.
 *
 * @author trejkaz
 */
public class ClassFile extends AbstractClassFileStructure {
    public ClassFile() {
        super("ClassFile");
    }

    public Annotation drop(Binary binary, long position) {
        long pos = position;

        Annotation magic             = u4(pos, "magic");               pos += 4;
        checkMagic(binary, magic, 0xCAFEBABE);

        Annotation minorVersion      = u2(pos, "minor_version");       pos += 2;
        Annotation majorVersion      = u2(pos, "major_version");       pos += 2;

        Annotation constantPoolCount = u2(pos, "constant_pool_count"); pos += 2;

        List<Annotation> constantPoolChildren = new LinkedList<Annotation>();
        int constantPoolLength = 0;
        for (int i = 1, count = ((Number) constantPoolCount.interpret(binary)).intValue(); i < count; i++) {
            Annotation constantPoolChild = new CpInfo().drop(binary, pos + constantPoolLength);
            constantPoolLength += constantPoolChild.getLength();
            constantPoolChildren.add(constantPoolChild);
        }
        GroupAnnotation constantPool = new SimpleMutableGroupAnnotation(pos, constantPoolLength, "constant_pool", constantPoolChildren);
        pos += constantPoolLength;

        Annotation accessFlags       = u2(pos, "access_flags");        pos += 2;
        Annotation thisClass         = u2(pos, "this_class");          pos += 2;
        Annotation superClass        = u2(pos, "super_class");         pos += 2;

        Annotation interfacesCount   = u2(pos, "interfaces_count");    pos += 2;

        List<Annotation> interfacesChildren = new LinkedList<Annotation>();
        int interfacesLength = 0;
        for (int i = 0, count = ((Number) interfacesCount.interpret(binary)).intValue(); i < count; i++) {
            interfacesChildren.add(u2(pos + interfacesLength, String.format("interfaces[%d]", i)));
            interfacesLength += 2;
        }
        GroupAnnotation interfaces = new SimpleMutableGroupAnnotation(pos, interfacesLength, "interfaces", interfacesChildren);
        pos += interfacesLength;

        Annotation fieldsCount       = u2(pos, "fields_count");        pos += 2;

        List<Annotation> fieldsChildren = new LinkedList<Annotation>();
        int fieldsLength = 0;
        for (int i = 0, count = ((Number) fieldsCount.interpret(binary)).intValue(); i < count; i++) {
            Annotation fieldsChild = new FieldInfo().drop(binary, pos + fieldsLength);
            fieldsLength += fieldsChild.getLength();
            fieldsChildren.add(fieldsChild);
        }
        GroupAnnotation fields = new SimpleMutableGroupAnnotation(pos, fieldsLength, "fields", fieldsChildren);
        pos += fieldsLength;

        Annotation methodsCount      = u2(pos, "methods_count");       pos += 2;

        List<Annotation> methodsChildren = new LinkedList<Annotation>();
        int methodsLength = 0;
        for (int i = 0, count = ((Number) methodsCount.interpret(binary)).intValue(); i < count; i++) {
            Annotation methodsChild = new MethodInfo().drop(binary, pos + methodsLength);
            methodsLength += methodsChild.getLength();
            methodsChildren.add(methodsChild);
        }
        GroupAnnotation methods = new SimpleMutableGroupAnnotation(pos, methodsLength, "methods", methodsChildren);
        pos += methodsLength;

        Annotation attributesCount   = u2(pos, "attributes_count");   pos += 2;

        List<Annotation> attributesChildren = new LinkedList<Annotation>();
        int attributesLength = 0;
        for (int i = 0, count = ((Number) attributesCount.interpret(binary)).intValue(); i < count; i++) {
            Annotation attributesChild = new AttributeInfo().drop(binary, pos + attributesLength);
            attributesLength += attributesChild.getLength();
            attributesChildren.add(attributesChild);
        }
        GroupAnnotation attributes = new SimpleMutableGroupAnnotation(pos, attributesLength, "attributes", attributesChildren);
        pos += attributesLength;

        int length = (int) (pos - position);
        return new SimpleMutableGroupAnnotation(position, length, getName(), Arrays.asList(
            magic, minorVersion, majorVersion,
            constantPoolCount, constantPool,
            accessFlags, thisClass, superClass,
            interfacesCount, interfaces,
            fieldsCount, fields,
            methodsCount, methods,
            attributesCount, attributes
        ));
    }
}
