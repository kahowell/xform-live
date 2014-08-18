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
import io.github.kahowell.xformsvc.model.DirectoryConfig;
import io.github.kahowell.xformsvc.model.FileInstance;
import io.github.kahowell.xformsvc.model.FileMatchConfig;
import io.github.kahowell.xformsvc.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

@Path("/file")
public class FileService {

    private List<File> findFiles(List<DirectoryConfig> directories, Collection<FileMatchConfig> includes, Collection<FileMatchConfig> filters) throws IOException {
        List<File> files = new ArrayList<File>();
        for (DirectoryConfig directory : directories) {
            files.addAll(FileUtil.findMatchingFiles(directory.getFullPath(), includes, filters, directory.isRecursive()));
        }
        return files;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}/source_files")
    public List<FileInstance> findSourceFiles(@PathParam("uuid") String uuid) throws IOException {
        Config config = Config.getInstance(uuid);
        List<DirectoryConfig> directories = config.getSourceDirectories();
        return buildResponse(findFiles(directories, Arrays.asList(config.getSourceMatchPatterns()), Arrays.asList(config.getSourceFilterPatterns())));
    }

    @GET
    @Path("{uuid}/contents")
    public Response getContent(@PathParam("uuid") String uuid, @QueryParam("fullPath") String fullPath) throws FileNotFoundException, IOException {
        String absolutePath = new File(fullPath).getAbsolutePath();
        List<DirectoryConfig> allDirectories = new ArrayList<DirectoryConfig>();
        allDirectories.addAll(Config.getInstance(uuid).getSourceDirectories());
        allDirectories.addAll(Config.getInstance(uuid).getTransformationDirectories());
        boolean allowed = false;
        for (DirectoryConfig directory : allDirectories) {
            if (absolutePath.startsWith(directory.getFullPath())) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Response.status(403).entity("Not within a configured directory").build();
        }
        return Response.ok(IOUtils.toString(new FileInputStream(fullPath), "UTF-8")).build();
    }

    private List<FileInstance> buildResponse(List<File> files) {
        List<FileInstance> transformed = new ArrayList<FileInstance>(files.size());
        for (File file : files) {
            transformed.add(new FileInstance(file));
        }
        return transformed;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}/transformation_files")
    public List<FileInstance> findTransformationFiles(@PathParam("uuid") String uuid) throws IOException {
        Config config = Config.getInstance(uuid);
        List<DirectoryConfig> directories = config.getTransformationDirectories();
        return buildResponse(findFiles(directories, Arrays.asList(config.getTransformationMatchPatterns()), Arrays.asList(config.getTransformationFilterPatterns())));
    }
}
