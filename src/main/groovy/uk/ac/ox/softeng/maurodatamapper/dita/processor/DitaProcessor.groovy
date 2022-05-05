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
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.dita.dost.Processor
import org.dita.dost.ProcessorFactory
import sun.net.www.protocol.file.FileURLConnection

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarFile

@Slf4j
class DitaProcessor {

    static ProcessorFactory pf
    static  {
        // Create a reusable processor factory with DITA-OT base directory
        URL url = DitaProcessor.getClassLoader().getResource("dita-ot-3.7")
        log.debug("Loading dita processor from " + url.toString())
        if(!url) {
            throw new IllegalStateException("Cannot find dita resource folder 'dita-ot-3.7'")
        } else {
            try {
                File dir = new File(url.toURI())
                pf = ProcessorFactory.newInstance(dir)
            } catch (Throwable e) {
                log.debug("Loading folder from jar file")
                Path ditaDir = Files.createTempDirectory("dita")
                copyResourcesRecursively(url, ditaDir.toFile())
                pf = ProcessorFactory.newInstance(ditaDir.toFile())

            }
        }
    }

    static byte[] generatePdf(DitaProject ditaProject) {
        return performTransform(ditaProject, "pdf2")
    }

    static void generatePdf(DitaProject ditaProject, String filename) {
        saveBytesToFile(generatePdf(ditaProject), filename)
    }

    static void generatePdf(DitaProject ditaProject, File file) {
        saveBytesToFile(generatePdf(ditaProject), file)
    }


    static byte[] runTransform(DitaProject ditaProject, String transtype) {
        return performTransform(ditaProject, transtype)
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
        return performTransform(ditaProject, "docx")
    }

    static void generateDocx(DitaProject ditaProject, String filename) {
        saveBytesToFile(generateDocx(ditaProject), filename)
    }

    static void generateDocx(DitaProject ditaProject, File file) {
        saveBytesToFile(generateDocx(ditaProject), file)
    }

    static byte[] performTransform(DitaProject ditaProject, String transType) {

        // and set the temporary directory
        Path tempDir = Files.createTempDirectory("temp")
        Path outDir = Files.createTempDirectory("out")
        pf.setBaseTempDir(tempDir.toFile())

        // Output the DITA element into a temporary file

        File mapFile = ditaProject.writeToDirectory(tempDir.toString())

        // Create a processor using the factory and configure the processor
        Processor p = pf.newProcessor(transType)
            .setProperty("nav-toc", "partial")
            .setInput(mapFile)
            .setOutputDir(outDir.toFile())

        // Run conversion
        p.run()
        File[] outputFiles = outDir.toFile().listFiles()
        if (outputFiles.length != 1) {
            log.error("More than one output file returned (${outputFiles.length})")
            outputFiles.each {log.error("  ${it.toURI()}")}
            return []
        }
        else {
            File outFile = outputFiles[0]
            return FileUtils.readFileToByteArray(outFile)
        }
    }

    static void copyResourcesRecursively(URL originUrl, File destination) throws Exception {
        URLConnection urlConnection = originUrl.openConnection()
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourceToFolder((JarURLConnection) urlConnection, destination)
        } else if (urlConnection instanceof FileURLConnection) {
            FileUtils.copyFileToDirectory(new File(originUrl.getPath()), destination)
        } else {
            throw new IllegalStateException("URLConnection[${urlConnection.getClass().getSimpleName()}] is not a recognized/implemented connection type.")
        }
    }

    /**
     * This method will copy resources from the jar file of the current thread and extract it to the destination folder.
     *
     * @param jarConnection
     * @param destDir
     * @throws IOException
     */
    static void copyJarResourceToFolder(JarURLConnection jarConnection, File destDir) {

        try {
            JarFile jarFile = jarConnection.getJarFile()

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {

                JarEntry jarEntry = e.nextElement()
                String jarEntryName = jarEntry.getName()
                String jarConnectionEntryName = jarConnection.getEntryName()

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {

                    String filename = jarEntryName.startsWith(jarConnectionEntryName) ? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName
                    File currentFile = new File(destDir, filename)

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs()
                    } else {
                        jarFile.getInputStream(jarEntry).withCloseable {is ->
                            FileUtils.openOutputStream(currentFile).withCloseable {out ->
                                IOUtils.copy(is, out)
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error('Could not copy jar to folder', e)
        }

    }

}
