/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
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

package org.trypticon.hex.formats.ruby;

import java.util.List;
import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;

/**
 * Utilities for annotation tests.
 * TODO: Consider moving to Hex Components, possibly as a separate module.
 *
 * @author trejkaz
 */
public class AnnotationTestUtils {

    /**
     * Creates a matcher which matches annotations which are the same as the one passed in.
     * {@code equals()} on annotations does not compare by contents, because this breaks other things.
     *
     * @param expected the expected annotation.
     * @return the matcher.
     */
    public static Matcher<Annotation> sameAnnotation(Annotation expected) {
        return new TypeSafeMatcher<Annotation>() {
            @Override
            public boolean matchesSafely(Annotation annotation) {
                return isSameAnnotation(expected, annotation);
            }

            @Override
            public void describeTo(@NonNls Description description) {
                description.appendText("same annotation as ");
                description.appendValue(expected);
            }
        };
    }

    private static boolean isSameAnnotation(Annotation annotation1, Annotation annotation2) {
        if (annotation1.getPosition() != annotation2.getPosition() ||
            annotation1.getLength() != annotation2.getLength() ||
            !Objects.equals(annotation1.getInterpreter(), annotation2.getInterpreter()) ||
            !Objects.equals(annotation1.getNote(), annotation2.getNote())) {
            return false;
        }

        if (annotation1 instanceof GroupAnnotation && !(annotation2 instanceof GroupAnnotation) ||
            !(annotation1 instanceof GroupAnnotation) && annotation2 instanceof GroupAnnotation) {
            return false;
        }

        if (annotation1 instanceof GroupAnnotation) {
            List<? extends Annotation> childAnnotations1 = ((GroupAnnotation) annotation1).getAnnotations();
            List<? extends Annotation> childAnnotations2 = ((GroupAnnotation) annotation2).getAnnotations();
            if (childAnnotations1.size() != childAnnotations2.size()) {
                return false;
            }

            for (int i = 0; i < childAnnotations1.size(); i++) {
                if (!isSameAnnotation(childAnnotations1.get(i), childAnnotations2.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

}
