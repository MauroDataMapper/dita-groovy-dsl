/*
 * Copyright 2020-2023 University of Oxford and NHS England
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.dita.processor


import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.dita.dost.Processor
import org.dita.dost.ProcessorFactory
import uk.ac.ox.softeng.maurodatamapper.dita.DitaProject

import java.nio.file.FileSystemNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.zip.ZipOutputStream

@Slf4j
@SuppressWarnings('unused')
@CompileStatic
class DitaProcessor {

    private ProcessorFactory pf

    DitaProcessor() {
        initialise('dita-ot-3.7')
    }

    DitaProcessor(String ditaOtBaseDirectory) {
        initialise(ditaOtBaseDirectory)
    }

    private initialise(String ditaOtBaseDirectory) {
        Path baseDirPath = Paths.get(ditaOtBaseDirectory)
        if (Files.exists(baseDirPath)) {
            log.debug('Loading DITA-OT processor from {}', baseDirPath)
            pf = ProcessorFactory.newInstance(baseDirPath.toFile())
        } else {
            // Create a reusable processor factory with DITA-OT base directory
            URL url = DitaProcessor.getClassLoader().getResource(ditaOtBaseDirectory)
            log.debug('Loading DITA-OT processor from {}', url)
            if (url) {
                try {
                    pf = ProcessorFactory.newInstance(Paths.get(url.toURI()).toFile())
                } catch (FileSystemNotFoundException e) {
                    log.debug("Loading folder from jar file due to exception: ${e.message}")
                    Path ditaDir = Files.createTempDirectory('dita')
                    extractUrlToDirectory(url, ditaDir)
                    pf = ProcessorFactory.newInstance(ditaDir.toFile())
                }
            } else {
                throw new IllegalStateException("Cannot find DITA-OT resource folder [${ditaOtBaseDirectory}]")
            }
        }
    }

    private byte[] performTransform(DitaProject ditaProject, String transType, Map<String, String> properties = [:]) {
        Path mapFilePath = writeDitaProjectToExportPath(ditaProject)
        Path baseDir = mapFilePath.parent
        Path outDir = baseDir.resolve('out')
        Files.createDirectories(outDir)

        // Output the DITA element into a temporary file
        pf.baseTempDir = baseDir.toFile()
        // Create a processor using the factory and configure the processor
        pf.newProcessor(transType)
            .setProperty('nav-toc', 'partial')
            .setProperties(properties)
            .setInput(mapFilePath.toFile())
            .setOutputDir(outDir.toFile())
            .run()

        if (Files.newDirectoryStream(outDir).size() != 1) {
            log.error('More than one output file returned ({})', Files.newDirectoryStream(outDir).size())
            Files.newDirectoryStream(outDir).each {log.error('  >> {}', it)}
            return new byte[]{}
        }
        Files.readAllBytes(Files.newDirectoryStream(outDir).first())
    }

    static def <O extends OutputStream> O generateDitaMapZipToOutputStream(DitaProject ditaProject, O outputStream) {
        Path mapFilePath = writeDitaProjectToExportPath(ditaProject)
        Path baseDir = mapFilePath.parent
        log.debug('Creating zip file of {}', baseDir)
        new ZipOutputStream(outputStream).withCloseable {zipOutputStream ->
            Files.walkFileTree(baseDir, new ZipFileVisitor(zipOutputStream, baseDir))
        }
        outputStream
    }

    static void generateDitaMapZipToPath(DitaProject ditaProject, Path path) {
        path.withOutputStream {outputStream ->
            generateDitaMapZipToOutputStream(ditaProject, outputStream)
        }
    }

    byte[] generateTransType(DitaProject ditaProject, String transtype, Map<String, String> properties = [:]) {
        performTransform(ditaProject, transtype, properties)
    }

    void generateTransTypeToPath(DitaProject ditaProject, String transtype, String filepath, Map<String, String> properties = [:]) {
        generateTransTypeToPath(ditaProject, transtype, Paths.get(filepath), properties)
    }

    void generateTransTypeToPath(DitaProject ditaProject, String transtype, Path path, Map<String, String> properties = [:]) {
        saveBytesToPath(generateTransType(ditaProject, transtype, properties), path)
    }

    byte[] generatePdf(DitaProject ditaProject, Map<String, String> properties = [:]) {
        generateTransType(ditaProject, 'pdf2', properties)
    }

    byte[] generateDocx(DitaProject ditaProject, Map<String, String> properties = [:]) {
        generateTransType(ditaProject, 'docx', properties)
    }

    void generatePdfToPath(DitaProject ditaProject, String filepath, Map<String, String> properties = [:]) {
        saveBytesToPath(generatePdf(ditaProject, properties), filepath)
    }

    void generatePdfToPath(DitaProject ditaProject, Path path, Map<String, String> properties = [:]) {
        saveBytesToPath(generatePdf(ditaProject, properties), path)
    }

    void generateDocxToPath(DitaProject ditaProject, String filepath, Map<String, String> properties = [:]) {
        saveBytesToPath(generateDocx(ditaProject, properties), filepath)
    }

    void generateDocxToPath(DitaProject ditaProject, Path path, Map<String, String> properties = [:]) {
        saveBytesToPath(generateDocx(ditaProject, properties), path)
    }

    private static Path writeDitaProjectToExportPath(DitaProject ditaProject) {
        Path baseDir = Files.createTempDirectory('dita_export')
        log.debug("Temporary Base Dir for DITA generation:")
        log.debug(baseDir.toString())
        Files.createDirectories(baseDir)
        ditaProject.writeToDirectory(baseDir)
    }

    private static void zipDitaProjectToOutputStream(Path baseDir, OutputStream outputStream) {

    }

    static void saveBytesToPath(byte[] bytes, Path path) {
        Files.write(path, bytes)
    }

    static void saveBytesToPath(byte[] bytes, String path) {
        saveBytesToPath(bytes, Paths.get(path))
    }

    static void extractUrlToDirectory(URL originUrl, Path destination) throws Exception {
        URLConnection urlConnection = originUrl.openConnection()
        if (urlConnection instanceof JarURLConnection) {
            copyJarUrlToFolder((JarURLConnection) urlConnection, destination)
        } else {
            throw new IllegalStateException("URLConnection[${urlConnection.getClass().getSimpleName()}] is not a recognized/implemented connection type.")
        }
    }

    /*
     * This method will copy resources from the jar file of the current thread and extract it to the destination folder.
     */

    static void copyJarUrlToFolder(JarURLConnection jarConnection, Path destDir) {
        try {
            JarFile jarFile = jarConnection.getJarFile()
            String jarConnectionEntryName = jarConnection.getEntryName()
            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement()
                String jarEntryName = jarEntry.getName()

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    String filename = jarEntryName.replace("${jarConnectionEntryName}${DitaProject.FILE_SEPARATOR}", '')
                    // The first entry is the "jarConnectionEntryName/" which is the "root" directory of what we want
                    if (filename) {
                        Path destinationPath = destDir.resolve(filename)
                        copyJarResource(jarFile, jarEntry, destinationPath)
                    }
                }
            }
        } catch (IOException e) {
            log.error('Could not copy jar to folder', e)
        }
    }

    static void copyJarResource(JarFile jarFile, JarEntry jarEntry, Path destination) {
        if (jarEntry.isDirectory()) {
            Files.createDirectories(destination)
        } else {
            jarFile.getInputStream(jarEntry).withCloseable {is ->
                Files.write(destination, is.readAllBytes())
            }
        }
    }
}
