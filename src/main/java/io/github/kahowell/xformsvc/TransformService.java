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

import io.github.kahowell.xformsvc.model.Config;
import io.github.kahowell.xformsvc.util.FileUtil;
import io.github.kahowell.xformsvc.util.TransformUtil;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

@Path("/transform")
public class TransformService {

    @POST
    @Path("{uuid}")
    public String transform(@PathParam("uuid") String uuid, @QueryParam("transformation") String transformation, String source) throws IOException, TransformerConfigurationException, TransformerException {
        Config config = Config.getInstance(uuid);
        TransformUtil transformUtil = new TransformUtil(FileUtil.findMatchingFiles(config.getTransformationDirectories(), Arrays.asList(config.getTransformationMatchPatterns()), Arrays.asList(config.getTransformationFilterPatterns())), config.isPrettyPrint());
        return transformUtil.transform(transformation, source);
    }
}
