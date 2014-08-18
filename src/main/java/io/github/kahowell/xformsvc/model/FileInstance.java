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

public class FileInstance {

    private File file;

    public FileInstance(File file) {
        this.file = file;
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }
    
    public String getName() {
        return file.getName();
    }

    public long getLastModified() {
        return file.lastModified();
    }
}
