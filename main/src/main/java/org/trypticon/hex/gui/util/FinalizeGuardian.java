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

package org.trypticon.hex.gui.util;

/**
 * <p>A utility class held by another class which holds a resource. This class' job is to
 *    hang around until the finalizer is called and then execute the cleanup logic.</p>
 *
 * <p>This is OK for detaching listeners, but I wouldn't recommend using this pattern for
 *    I/O resources. If I start wanting to do that, this class should also extend
 *    {@code AutoCloseable} so that the caller can manually call it sooner.</p>
 *
 * @author trejkaz
 */
public final class FinalizeGuardian {
    private final Runnable cleanupLogic;

    /**
     * Constructs the guardian.
     *
     * @param cleanupLogic the cleanup logic to run.
     */
    public FinalizeGuardian(Runnable cleanupLogic) {
        this.cleanupLogic = cleanupLogic;
    }

    @Override
    protected final void finalize() throws Throwable {
        try {
            cleanupLogic.run();
        } finally {
            super.finalize();
        }
    }
}
