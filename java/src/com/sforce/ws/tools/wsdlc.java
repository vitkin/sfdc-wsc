/*
 * Copyright (c) 2005, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sforce.ws.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sforce.ws.bind.NameMapper;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.template.Template;
import com.sforce.ws.template.TemplateException;
import com.sforce.ws.util.FileUtil;
import com.sforce.ws.util.Verbose;
import com.sforce.ws.wsdl.ComplexType;
import com.sforce.ws.wsdl.Definitions;
import com.sforce.ws.wsdl.Schema;
import com.sforce.ws.wsdl.SfdcApiType;
import com.sforce.ws.wsdl.SimpleType;
import com.sforce.ws.wsdl.Types;
import com.sforce.ws.wsdl.WsdlFactory;
import com.sforce.ws.wsdl.WsdlParseException;

/**
 * wsdlc is a tool that can generate java stubs from WSDL.
 *
 * @author http://cheenath
 * @author jesperudby
 * @version 1.0
 * @since 1.0  Nov 22, 2005
 */
public class wsdlc {
    private File tempDir;
    private TypeMapper typeMapper = new TypeMapper();
    private ArrayList<String> javaFiles = new ArrayList<String>();
    private String packagePrefix = null;
    private boolean laxMinOccursMode;
    private static final String LAX_MINOCCURS = "lax-minoccurs-checking";
    private static final String PACKAGE_PREFIX = "package-prefix";
    private static final String SOBJECT_TEMPLATE = "com/sforce/ws/tools/sobject.template";
    private static final String AGG_RESULT_TEMPLATE = "com/sforce/ws/tools/aggregateResult.template";


    public wsdlc(String wsdlFile, String jarFile, String temp)
            throws ToolsException, WsdlParseException, IOException, TemplateException {

        checkTargetFile(jarFile);
        createTempDir(temp);
        Verbose.log("Created temp dir: " + tempDir.getAbsolutePath());

        packagePrefix = System.getProperty(PACKAGE_PREFIX);
        laxMinOccursMode = System.getProperty(LAX_MINOCCURS) != null;
        		
        typeMapper.setPackagePrefix(packagePrefix);

        Definitions definitions = WsdlFactory.create(wsdlFile);
        SfdcApiType type = definitions.getApiType();
        Types types = definitions.getTypes();

        generateTypes(types);
        generateConnection(definitions);
        generateConnector(definitions);

        if (type != null && type.getSobjectNamespace() != null) {
            generateSObject(definitions);
            generateAggregateResult(definitions);
        }

        compileTypes();
        generateJarFile(jarFile);

        String delTempDir = System.getProperty("del-temp-dir");
        // only delete tempDir if specifically asked and if temp not given on cmd line
        if ("true".equalsIgnoreCase(delTempDir) || (delTempDir == null && temp == null)) {
            Verbose.log("Delete temp dir: " + tempDir.getAbsolutePath());
            if (delTempDir == null) {
            	Verbose.log("Set system property del-temp-dir=false to not delete temp dir.");
            }
            FileUtil.deleteDir(tempDir);
        }
    }

    private void createTempDir(String temp) throws IOException {
        if (temp == null) {
            tempDir = File.createTempFile("wsdlc-temp-", "-dir", null);
            tempDir.delete();
            tempDir.mkdir();
        } else {
            tempDir = new File(temp);
        }
    }

    private void checkTargetFile(String jarFile) throws ToolsException {
        if (!jarFile.endsWith(".jar") && !jarFile.endsWith(".zip")) {
            throw new ToolsException("<jar-file> must have a .jar/.zip extension");
        }

        File jf = new File(jarFile);

        if (jf.exists()) {
            throw new ToolsException("<jar-file> already exists");
        }

        File parentDir = jf.getParentFile();

        // !parentDir.exists() is in here twice, before and after the mkdirs, since the mkdirs can return false in
        // the case that some other process (typically, another parallel wsdlc process) comes in and makes the 
        // directory between the first exists() check and the mkdirs().  We still want to catch unusual failures,
        // other than that case, so we do a second exists() if mkdirs fails
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs() && !parentDir.exists()) {
            throw new ToolsException("<jar-file> failed while creating parent directory " + parentDir.getPath());
        }
    }


    private void generateConnector(Definitions definitions) throws IOException, TemplateException {
        ConnectionGenerator connectionGenerator =
                new ConnectionGenerator(definitions, tempDir, typeMapper, packagePrefix);
        String file = connectionGenerator.generateConnector();
        javaFiles.add(file);
    }

    private void generateConnection(Definitions definitions) throws IOException, TemplateException {
        ConnectionGenerator connectionGenerator = new ConnectionGenerator(definitions, tempDir, typeMapper, packagePrefix);
        String file = connectionGenerator.generateConnection();
        javaFiles.add(file);
    }

    private void generateAggregateResult(Definitions definitions) throws IOException, TemplateException {
        if (definitions.getApiType() == SfdcApiType.Enterprise) {

            String packageName = NameMapper.getPackageName(
                    definitions.getApiType().getSobjectNamespace(), packagePrefix);

            File dir = FileUtil.mkdirs(packageName, tempDir);
            Template template = new Template();
            template.setProperty("packageName", packageName);
            String className = "AggregateResult";
            File javaFile = new File(dir, className + ".java");
            template.exec(AGG_RESULT_TEMPLATE, javaFile.getAbsolutePath());
            javaFiles.add(javaFile.getAbsolutePath());
        }
    }

    private void generateSObject(Definitions definitions) throws IOException, TemplateException {
        if (definitions.getApiType() == SfdcApiType.Partner || definitions.getApiType() == SfdcApiType.CrossInstance
                || definitions.getApiType() == SfdcApiType.Internal || definitions.getApiType() == SfdcApiType.ClientSync
                || definitions.getApiType() == SfdcApiType.SyncApi) {
            String packageName = NameMapper.getPackageName(definitions.getApiType().getSobjectNamespace(), packagePrefix);
            File dir = FileUtil.mkdirs(packageName, tempDir);
            Template template = new Template();
            template.setProperty("packageName", packageName);
            String className = "SObject";
            File javaFile = new File(dir, className + ".java");
            template.exec(SOBJECT_TEMPLATE, javaFile.getAbsolutePath());
            javaFiles.add(javaFile.getAbsolutePath());
        }
    }

    private void generateJarFile(String jarFile) throws IOException {
        Verbose.log("Generating jar file ... " + jarFile);
        FileOutputStream out = new FileOutputStream(jarFile);
        InputStream manifestIo = getManifest();
        Manifest manifest = new Manifest(manifestIo);
        JarOutputStream jar = new JarOutputStream(out, manifest);

        int rootLen = tempDir.getAbsolutePath().length();

        int len = "java".length();
        for (String javaFile : javaFiles) {
            String classFile = javaFile.substring(0, javaFile.length() - len) + "class";
            String className = classFile.substring(rootLen + 1);
            addFileToJar(className, classFile, jar);

            String javaName = javaFile.substring(rootLen + 1);
            addFileToJar(javaName, javaFile, jar);
        }

        if (Boolean.parseBoolean(System.getProperty("standalone-jar", "false"))) {
            Verbose.log("Adding runtime classes to the jar");
            addRuntimeClasses(jar);
        } else {
            Verbose.log("To include runtime classes in the generated jar " +
                        "please set system property standalone-jar=true");
        }

        jar.close();
        out.close();
        Verbose.log("Generated jar file " + jarFile);
    }

    private void addFileToJar(String className, String classFile, JarOutputStream jar) throws IOException {
        className = className.replace('\\', '/');
        FileInputStream fio = new FileInputStream(classFile);
        jar.putNextEntry(new JarEntry(className));

        int cb;
        byte [] buffer = new byte[8192];
        while ((cb = fio.read(buffer)) != -1) {
            jar.write(buffer, 0, cb);
        }

        jar.closeEntry();
        fio.close();
    }

    private void addRuntimeClasses(JarOutputStream jar) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        ArrayList<String> runtimeClasses = getRuntimeClasses(cl);
        for (String c : runtimeClasses) {
            jar.putNextEntry(new JarEntry(c));
            InputStream in = cl.getResourceAsStream(c);
            int ch;
            while ((ch = in.read()) != -1) {
                jar.write((char) ch);
            }
            jar.closeEntry();
            in.close();
        }
    }

    private ArrayList<String> getRuntimeClasses(ClassLoader cl) throws IOException {
        ArrayList<String> classes = new ArrayList<String>();
        InputStream in = cl.getResourceAsStream("com/sforce/ws/runtime-classes.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            classes.add(line);
        }
        reader.close();

        return classes;
    }

    private InputStream getManifest() throws IOException {
        String m = "Manifest-Version: 1.0\n" +
                   "Created-By: " + System.getProperty("java.runtime.version") + " ("  + System.getProperty("java.vm.specification.vendor") + ")\n" +
                   "Built-By: " + System.getProperty("user.name") + " (WSC-" + VersionInfo.VERSION + ")\n";

        return new ByteArrayInputStream(m.getBytes("UTF-8"));
    }

    private void compileTypes() throws ToolsException {
        Compiler compiler = new Compiler();
        compiler.compile(javaFiles);
    }

    private void generateTypes(Types types) throws IOException, TemplateException {
        Verbose.log("Generating Java files from schema ...");
        Iterator<Schema> schemas = types.getSchemas();

        while (schemas.hasNext()) {
            generate(types, schemas.next());
        }

        Verbose.log("Generated " + javaFiles.size() + " java files.");
    }

    private void generate(Types types, Schema schema) throws IOException, TemplateException {
        Iterator<ComplexType> complexTypes = schema.getComplexTypes();
        while (complexTypes.hasNext()) {
            ComplexType complexType = complexTypes.next();
            if (!typeMapper.isWellKnownType(complexType.getSchema().getTargetNamespace(), complexType.getName())) {
                ComplexTypeGenerator typeGenerator =
                        new ComplexTypeGenerator(types, schema, complexType, tempDir, typeMapper, laxMinOccursMode);
                String file = typeGenerator.generate();
                javaFiles.add(file);
            }
        }

        Iterator<SimpleType> simpleTypes = schema.getSimpleTypes();
        while (simpleTypes.hasNext()) {
            SimpleType simpleType = simpleTypes.next();
            if (!typeMapper.isWellKnownType(simpleType.getSchema().getTargetNamespace(), simpleType.getName())) {
                SimpleTypeGenerator typeGenerator =
                        new SimpleTypeGenerator(types, schema, simpleType, tempDir, typeMapper);
                String file = typeGenerator.generate();
                javaFiles.add(file);
            }
        }
    }

    public static void main(String[] args)
            throws WsdlParseException, IOException, TemplateException {
        try {
            run(args);
        } catch (ToolsException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    static void run(String[] args) throws ToolsException, WsdlParseException, IOException, TemplateException {
        if (args.length == 2) {
            new wsdlc(args[0], args[1], null);
        } else if (args.length == 3) {
            new wsdlc(args[0], args[1], args[2]);
        } else {
            throw new ToolsException(" usage: java com.sforce.ws.tools.wsdlc <wsdl-file> <jar-file> [temp-dir]");
        }
    }


    static class ToolsJarClassLoader extends ClassLoader {
        private final JarFile toolsJar;

        ToolsJarClassLoader(ClassLoader parent, File file) throws IOException {
            super(parent);
            toolsJar = new JarFile(file);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] b;
            try {
                b = getBytes(name);
            } catch (IOException e) {
                throw new ClassNotFoundException(name);
            }
            return defineClass(null, b, 0, b.length);
        }

        private byte[] getBytes(String name) throws IOException, ClassNotFoundException {
            String className = name.replace(".", "/") + ".class";
            
            JarEntry entry = toolsJar.getJarEntry(className);
            if (entry == null) {
                throw new ClassNotFoundException(name);
            }

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            
        	InputStream io = toolsJar.getInputStream(entry);
            try {
                int ch;
                while((ch=io.read()) != -1) {
                    bout.write((char)ch);
                }
            } finally {
            	io.close();
            }
            
            return bout.toByteArray();
        }
    }

    static class Compiler {
        public Compiler() {
        }

        private JavaCompiler findCompiler() throws ToolsException {        	
    		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    		if (javaCompiler != null) {
    			return javaCompiler;
    		} else {
    			String javaHome = System.getProperty("java.home");
    			File dir;
				try {
					dir = new File(javaHome, "..").getCanonicalFile();
				} catch (IOException e) {
					throw new ToolsException("Cannot find JavaCompiler", e);
				}
				
    			final List<File> files = new ArrayList<File>();
    			dir.listFiles(new FileFilter() {
    				@Override
    				public boolean accept(File pathname) {
    					if (pathname.isDirectory()) {
    						pathname.listFiles(this);
    					} else if (pathname.getName().equals("tools.jar")) {
    						files.add(pathname);
    					}
    					return false;
    				}
    			});
    			
    			final String version = System.getProperty("java.version");
    			Collections.sort(files, new Comparator<File>() {
    				@Override
    				public int compare(File f1, File f2) {
    					try {
    						return f1.getCanonicalPath().compareTo(f2.getCanonicalPath());
    					} catch (IOException e) {
    						throw new RuntimeException(e);
    					}
    				}
    			});
    			File candidate = null;
    			for (File file : files) {
    				String f;
					try {
						f = file.getCanonicalPath();
					} catch (IOException e) {
						throw new ToolsException("Cannot find JavaCompiler", e);
					}
    				if (f.contains(version)) {
    					candidate = file;
    				}
    			}
    			if (candidate == null) { 
    				if (!files.isEmpty()) {
        				candidate = files.get(files.size() - 1);
        			} else {
        				throw new ToolsException("Unable to find compiler. Make sure that tools.jar is in your classpath");
        			}
    			}
    			try {
    				ClassLoader cl = new ToolsJarClassLoader(JavaCompiler.class.getClassLoader(), candidate);
    				Class<?> clazz = cl.loadClass("com.sun.tools.javac.api.JavacTool");
    				Method m = clazz.getMethod("create");
    				return (JavaCompiler) m.invoke(null);
    			} catch (Exception e) {
    				throw new ToolsException("Cannot find JavaCompiler", e);
    			}
    		}
        }
        
        public void compile(List<String> javaFiles) throws ToolsException {
            String target = System.getProperty("compileTarget");

            Verbose.log("Compiling to target " + (target!=null ? target : "default" ) + "... ");

            List<String> options = new ArrayList<String>();
            options.add("-g");

            if (target != null) {
            	options.add("-target");
            	options.add(target);
            }
            
			JavaCompiler compiler = findCompiler();

			DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
			
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, Charset.forName("UTF-8"));
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(javaFiles);
			
			compiler.getTask(null, fileManager, diagnosticsCollector, options, null, compilationUnits).call();

			List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
			boolean errors = false;
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
				if (diagnostic.getKind() == Kind.ERROR) {
					errors = true;
				}
				System.out.println(diagnostic.getMessage(Locale.getDefault()));
			}
			if (errors) {
				throw new ToolsException("Compilation failed");
			}
            Verbose.log("Compiled " + javaFiles.size() + " java files.");
        }
    }
}
