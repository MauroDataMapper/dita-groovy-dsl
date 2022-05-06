/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
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

import uk.ac.ox.softeng.maurodatamapper.dita.DitaProject

import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils
import org.dita.dost.ProcessorFactory

import java.nio.charset.Charset
import java.nio.file.FileSystemNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarEntry
import java.util.jar.JarFile

@Slf4j
class DitaProcessor {
    static ProcessorFactory pf
    static {
        // Create a reusable processor factory with DITA-OT base directory
        URL url = DitaProcessor.getClassLoader().getResource('dita-ot-3.7')
        log.debug('Loading dita processor from {}', url)
        if (url) {
            try {
                pf = ProcessorFactory.newInstance(Paths.get(url.toURI()).toFile())
            } catch (FileSystemNotFoundException e) {
                log.debug("Loading folder from jar file due to exception: ${e.message}", e)
                Path ditaDir = Files.createTempDirectory('dita')
                extractUrlToDirectory(url, ditaDir)
                pf = ProcessorFactory.newInstance(ditaDir.toFile())
            }
        } else {
            throw new IllegalStateException('Cannot find dita resource folder [dita-ot-3.7]')
        }
    }

    static byte[] generatePdf(DitaProject ditaProject) {
        performTransform(ditaProject, 'pdf2')
    }

    static void generatePdf(DitaProject ditaProject, String filename) {
        saveBytesToFile(generatePdf(ditaProject), filename)
    }

    static void generatePdf(DitaProject ditaProject, File file) {
        saveBytesToFile(generatePdf(ditaProject), file)
    }

    static byte[] runTransform(DitaProject ditaProject, String transtype) {
        performTransform(ditaProject, transtype)
    }

    static void runTransform(DitaProject ditaProject, String transtype, String filename) {
        saveBytesToFile(runTransform(ditaProject, transtype), filename)
    }

    static void runTransform(DitaProject ditaProject, String transtype, File file) {
        saveBytesToFile(runTransform(ditaProject, transtype), file)
    }

    static void saveBytesToFile(byte[] bytes, String filename) {
        File outputFile = new File(filename)
        Files.write(outputFile.toPath(), bytes)
    }

    static void saveBytesToFile(byte[] bytes, File file) {
        Files.write(file.toPath(), bytes)
    }

    static byte[] generateDocx(DitaProject ditaProject) {
        performTransform(ditaProject, 'docx')
    }

    static void generateDocx(DitaProject ditaProject, String filename) {
        saveBytesToFile(generateDocx(ditaProject), filename)
    }

    static void generateDocx(DitaProject ditaProject, File file) {
        saveBytesToFile(generateDocx(ditaProject), file)
    }

    static byte[] performTransform(DitaProject ditaProject, String transType) {
        Path baseDir = Files.createTempDirectory('dita_export')
        Path outDir = baseDir.resolve('out')
        Files.createDirectories(outDir)

        // Output the DITA element into a temporary file
        pf.baseTempDir = baseDir.toFile()
        Path mapFilePath = ditaProject.writeToDirectory(baseDir)

        // Create a processor using the factory and configure the processor
        pf.newProcessor(transType)
            .setProperty('nav-toc', 'partial')
            .setInput(mapFilePath.toFile())
            .setOutputDir(outDir.toFile())
            .run()

        if (Files.newDirectoryStream(outDir).size() != 1) {
            log.error('More than one output file returned ({})', Files.newDirectoryStream(outDir).size())
            Files.newDirectoryStream(outDir).each {log.error('  >> {}', it)}
            return []
        }
        Files.readAllBytes(Files.newDirectoryStream(outDir).first())
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
                Files.newBufferedWriter(destination).withCloseable {out ->
                    IOUtils.copy(is, out, Charset.defaultCharset())
                }
            }
        }
    }
}
