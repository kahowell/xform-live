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
package io.github.kahowell.xformsvc.util;

import io.github.kahowell.xformsvc.model.DirectoryConfig;
import io.github.kahowell.xformsvc.model.FileMatchConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class FileUtil {
    public static class FileMatcher extends SimpleFileVisitor<Path> {

        private Collection<PathMatcher> includes;

        private Collection<PathMatcher> filters;

        private List<File> matches = new ArrayList<File>();

        public FileMatcher(Collection<PathMatcher> includes, Collection<PathMatcher> filters) {
            this.includes = includes;
            this.filters = filters;
        }

        public List<File> getMatches() {
            return matches;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            for (PathMatcher include : includes) {
                for (PathMatcher filter : filters) {
                    if (filter.matches(file.getFileName())) {
                        return FileVisitResult.CONTINUE;
                    }
                }
                if (include.matches(file.getFileName())) {
                    matches.add(file.toFile());
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public static List<File> findMatchingFiles(String root, Collection<FileMatchConfig> includeConfig, Collection<FileMatchConfig> filterConfig, boolean recursive) throws IOException {
        List<PathMatcher> includes = convertConfigsToPathMatcherCollection(includeConfig);
        List<PathMatcher> filters = convertConfigsToPathMatcherCollection(filterConfig);
        FileMatcher matcher = new FileMatcher(includes, filters);
        Files.walkFileTree(FileSystems.getDefault().getPath(root), EnumSet.of(FileVisitOption.FOLLOW_LINKS), recursive ? Integer.MAX_VALUE : 1, matcher);
        return matcher.getMatches();
    }

    public static List<File> findMatchingFiles(Collection<DirectoryConfig> dirs, Collection<FileMatchConfig> includeConfig, Collection<FileMatchConfig> filterConfig) throws IOException {
        List<PathMatcher> includes = convertConfigsToPathMatcherCollection(includeConfig);
        List<PathMatcher> filters = convertConfigsToPathMatcherCollection(filterConfig);
        FileMatcher matcher = new FileMatcher(includes, filters);
        for (DirectoryConfig directory : dirs) {
            Files.walkFileTree(FileSystems.getDefault().getPath(directory.getFullPath()), EnumSet.of(FileVisitOption.FOLLOW_LINKS), directory.isRecursive() ? Integer.MAX_VALUE : 0, matcher);
        }
        return matcher.getMatches();
    }

    private static List<PathMatcher> convertConfigsToPathMatcherCollection(
            Collection<FileMatchConfig> includeConfig) {
        List<PathMatcher> includes = new ArrayList<PathMatcher>(includeConfig.size());
        for (FileMatchConfig fileMatchConfig : includeConfig) {
            includes.add(fileMatchConfig.toPathMatcher(FileSystems.getDefault()));
        }
        return includes;
    }
}
