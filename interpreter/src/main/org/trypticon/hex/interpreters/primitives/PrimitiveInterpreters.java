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

package org.trypticon.hex.interpreters.primitives;

import org.trypticon.hex.interpreters.Interpreter;

/**
 * Convenience class holding a bunch of instances to primitive interpreters.
 *
 * @author trejkaz
 */
public class PrimitiveInterpreters {
    public static final Interpreter<UByte> UINT8 = new UByteInterpreter();
    public static final Interpreter<UShort> UINT16_BE = new UShortInterpreterBE();
    public static final Interpreter<UShort> UINT16_LE = new UShortInterpreterLE();
    public static final Interpreter<UInt> UINT32_BE = new UIntInterpreterBE();
    public static final Interpreter<UInt> UINT32_LE = new UIntInterpreterLE();
    public static final Interpreter<ULong> UINT64_BE = new ULongInterpreterBE();
    public static final Interpreter<ULong> UINT64_LE = new ULongInterpreterLE();

    public static final Interpreter<Float> FLOAT32_BE = new FloatInterpreterBE();
    public static final Interpreter<Float> FLOAT32_LE = new FloatInterpreterLE();
    public static final Interpreter<Double> FLOAT64_BE = new DoubleInterpreterBE();
    public static final Interpreter<Double> FLOAT64_LE = new DoubleInterpreterLE();
}
