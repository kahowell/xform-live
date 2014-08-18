/*
 * Copyright (C) 2014 Xform-Live contributors
 *
 * This file is part of Xform-Live.
 *
 * Xform-Live is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Xform-Live is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Xform-Live.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.kahowell.xformsvc.model;

import java.io.File;

public class DirectoryConfig {

    private static final String RESTRICT_TO_PROPERTY_NAME = "XFORM_LIVE_RESTRICT_TO";

    private String fullPath;

    private boolean recursive;

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        String restrictTo = System.getProperty(RESTRICT_TO_PROPERTY_NAME);
        if (restrictTo != null) {
            String absolutePath = new File(fullPath).getAbsolutePath();
            if (!absolutePath.startsWith(restrictTo)) {
                throw new SecurityException(String.format("Attempted to configure access to %s, but %s is set to %s", absolutePath, RESTRICT_TO_PROPERTY_NAME, restrictTo));
            }
        }
        this.fullPath = fullPath;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
