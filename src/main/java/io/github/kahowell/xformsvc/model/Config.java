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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Config {

    private static Map<String, Config> instances = new HashMap<String, Config>();

    public Config() {}

    private Config(String uuid) {
        this.uuid = uuid;
    }

    public synchronized static Config getInstance(String uuid) {
        return instances.get(uuid);
    }

    public synchronized static Config newInstance() {
        String uuid = UUID.randomUUID().toString();
        Config config = new Config(uuid);
        instances.put(uuid, config);
        return config;
    }

    public synchronized static void replaceConfig(Config config) {
        instances.put(config.getUuid(), config);
    }

    private String uuid;

    private List<DirectoryConfig> sourceDirectories = new ArrayList<DirectoryConfig>();

    private List<DirectoryConfig> transformationDirectories = new ArrayList<DirectoryConfig>();

    private FileMatchConfig[] sourceMatchPatterns = {new FileMatchConfig("glob", "*.xml")};

    private FileMatchConfig[] sourceFilterPatterns = {new FileMatchConfig("glob", "pom.xml")};

    private FileMatchConfig[] transformationMatchPatterns = {new FileMatchConfig("glob", "*.xsl")};

    private FileMatchConfig[] transformationFilterPatterns = {};

    private boolean prettyPrint = true;

    public String getUuid() {
        return uuid;
    }

    public List<DirectoryConfig> getSourceDirectories() {
        return sourceDirectories;
    }

    public void setSourceDirectories(List<DirectoryConfig> directories) {
        this.sourceDirectories = directories;
    }

    public List<DirectoryConfig> getTransformationDirectories() {
        return transformationDirectories;
    }

    public void setTransformationDirectories(
            List<DirectoryConfig> transformationDirectories) {
        this.transformationDirectories = transformationDirectories;
    }

    public FileMatchConfig[] getSourceMatchPatterns() {
        return sourceMatchPatterns;
    }

    public void setSourceMatchPatterns(FileMatchConfig[] sourceMatchPatterns) {
        this.sourceMatchPatterns = sourceMatchPatterns;
    }

    public FileMatchConfig[] getSourceFilterPatterns() {
        return sourceFilterPatterns;
    }

    public void setSourceFilterPatterns(FileMatchConfig[] sourceFilterPatterns) {
        this.sourceFilterPatterns = sourceFilterPatterns;
    }

    public FileMatchConfig[] getTransformationMatchPatterns() {
        return transformationMatchPatterns;
    }

    public void setTransformationMatchPatterns(
            FileMatchConfig[] transformationMatchPatterns) {
        this.transformationMatchPatterns = transformationMatchPatterns;
    }

    public FileMatchConfig[] getTransformationFilterPatterns() {
        return transformationFilterPatterns;
    }

    public void setTransformationFilterPatterns(FileMatchConfig[] patterns) {
        this.transformationFilterPatterns = patterns;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
