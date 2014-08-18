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
package io.github.kahowell.xformsvc;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest/xform")
public class XformApplication extends Application {

    private static Set<Class<?>> classes = new HashSet<Class<?>>();

    static {
        classes.add(TransformService.class);
        classes.add(ConfigService.class);
        classes.add(FileService.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
