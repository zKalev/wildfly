/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.web.deployment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Hack until the module classloader does that for us.
 *
 * @author Jean-Frederic Clere
 */

public class WebClassLoader extends ClassLoader {
    private static final int BUFFER_SIZE = 8192;
    private File file;
    private ClassLoader parent;

    public WebClassLoader(File file, ClassLoader parent) {
        this.file = file;
        this.parent = parent;
    }

    public InputStream getResourceAsStream(String name) {
        InputStream is = parent.getResourceAsStream(name);
        return is;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return (loadClass(name, false));
    }

    protected synchronized Class<?> loadClass(String className, boolean resolve)
    throws ClassNotFoundException {
        Class <?>cls = findLoadedClass(className);
        if (cls != null) {
            return cls;
        }

        String clsFile = file.getAbsolutePath() + "/" + className.replace('.', '/') + ".class";
        byte[] classBytes = null;
        try {
            FileInputStream in = new FileInputStream(new File (clsFile));
            if (in != null) {
                byte[] buffer = new byte[BUFFER_SIZE];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int n = -1;
                while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    out.write(buffer, 0, n);
                }
                classBytes = out.toByteArray();
            }
        }
        catch (IOException e) {
            // Ignore it.
         }
         if (classBytes != null) {
            try {
                cls = defineClass(className, classBytes, 0, classBytes.length);
                if (resolve) {
                    resolveClass(cls);
                }
            }
            catch (SecurityException e) {
                System.err.println("MERDE: " + e);
                cls = super.loadClass(className, resolve);
            }
        }
        if (cls == null)
            cls = parent.loadClass(className);
        if (cls == null)
            throw new ClassNotFoundException(className);

        return cls;

    }
}
