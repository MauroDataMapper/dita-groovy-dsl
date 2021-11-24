package uk.ac.ox.softeng.maurodatamapper.dita.processor

import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

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
        URL url = DitaProcessor.getClassLoader().getResource("dita-ot-3.6.1/")
        log.error("" + url.toString())
        if(!url) {
            log.error("Cannot get dita resource folder")
        } else {
            try {
                File dir = new File(url.toURI())
                pf = ProcessorFactory.newInstance(dir)
            } catch (Throwable e) {
                log.error ("Loading folder from jar file")
                Path ditaDir = Files.createTempDirectory("dita")
                copyResourcesRecursively(url, ditaDir.toFile())
                pf = ProcessorFactory.newInstance(ditaDir.toFile())

            }
        }
    }

    static byte[] generatePdf(TopLevelDitaElement ditaElement) {
        return performTransform(ditaElement, "pdf2")
    }

    static void generatePdf(TopLevelDitaElement ditaElement, String filename) {
        saveBytesToFile(generatePdf(ditaElement), filename)
    }

    static void generatePdf(TopLevelDitaElement ditaElement, File file) {
        saveBytesToFile(generatePdf(ditaElement), file)
    }



    static void saveBytesToFile(byte[] bytes, String filename) {
        File outputFile = new File(filename)
        Files.write(outputFile.toPath(), bytes)
    }

    static void saveBytesToFile(byte[] bytes, File file) {
        Files.write(file.toPath(), bytes)
    }


    static byte[] generateDocx(TopLevelDitaElement ditaElement) {
        return performTransform(ditaElement, "docx")
    }

    static void generateDocx(TopLevelDitaElement ditaElement, String filename) {
        saveBytesToFile(generateDocx(ditaElement), filename)
    }

    static void generateDocx(TopLevelDitaElement ditaElement, File file) {
        saveBytesToFile(generateDocx(ditaElement), file)
    }

    static byte[] performTransform(TopLevelDitaElement ditaElement, String transType) {

        // and set the temporary directory
        Path tempDir = Files.createTempDirectory("temp")
        Path outDir = Files.createTempDirectory("out")
        pf.setBaseTempDir(tempDir.toFile())

        File inputFile = Files.createTempFile(tempDir, "input", ditaElement.getFileSuffix()).toFile()

        // Output the DITA element into a temporary file
        ditaElement.outputAsFile(inputFile)

        // Create a processor using the factory and configure the processor
        Processor p = pf.newProcessor(transType)
            .setProperty("nav-toc", "partial")
            .setInput(inputFile)
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
        URLConnection urlConnection = originUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            copyJarResourceToFolder((JarURLConnection) urlConnection, destination)
        } else if (urlConnection instanceof FileURLConnection) {
            FileUtils.copyJarResourceToFolder(new File(originUrl.getPath()), destination);
        } else {
            throw new Exception("URLConnection[" + urlConnection.getClass().getSimpleName() +
                                "] is not a recognized/implemented connection type.");
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
            JarFile jarFile = jarConnection.getJarFile();

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {

                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();
                String jarConnectionEntryName = jarConnection.getEntryName();

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {

                    String filename = jarEntryName.startsWith(jarConnectionEntryName) ? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
                    File currentFile = new File(destDir, filename);

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            // TODO add logger
            e.printStackTrace()
        }

    }

}
