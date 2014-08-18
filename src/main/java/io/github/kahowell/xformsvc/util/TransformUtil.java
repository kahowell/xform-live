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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class TransformUtil {

    private class CustomURIResolver implements URIResolver {

        @Override
        public Source resolve(String href, String base) {
            for (File file : transformationSources) {
                if (file.getName().equals(href)) {
                    return new StreamSource(file);
                }
            }
            return null;
        }
    }

    private Collection<File> transformationSources;

    private boolean prettyPrint;

    public TransformUtil(Collection<File> sources, boolean prettyPrint) {
        transformationSources = sources;
        this.prettyPrint = prettyPrint;
    }

    public URIResolver getURIResolver() {
        return new CustomURIResolver();
    }

    public String transform(String transformationName, String source) throws TransformerConfigurationException, TransformerException, UnsupportedEncodingException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(getURIResolver());
        Transformer transformer = factory.newTransformer(lookupTransformer(transformationName));
        transformer.setOutputProperty(OutputKeys.INDENT, prettyPrint ? "yes" : "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(source.getBytes("UTF-8"))), new StreamResult(out));
        return out.toString("UTF-8");
    }

    private Source lookupTransformer(String transformationName) throws TransformerException {
        return getURIResolver().resolve(transformationName, null);
    }
}
