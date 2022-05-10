package uk.ac.ox.softeng.maurodatamapper.dita.processor

import groovy.util.logging.Slf4j

import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @since 10/05/2022
 */
@Slf4j
class ZipFileVisitor implements FileVisitor<Path> {

    ZipOutputStream zipOutputStream
    Path baseDir

    ZipFileVisitor(ZipOutputStream zipOutputStream, Path baseDir) {
        this.zipOutputStream = zipOutputStream
        this.baseDir = baseDir
    }

    @Override
    FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        // If contents in dir then run it
        if (Files.newDirectoryStream(dir).size()) {
            zipOutputStream.putNextEntry(new ZipEntry(getFilePathRelativeToBase(dir, true)))
            return FileVisitResult.CONTINUE
        }
        FileVisitResult.SKIP_SUBTREE
    }

    @Override
    FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(getFilePathRelativeToBase(file, false)))
        Files.copy(file, zipOutputStream)
        zipOutputStream.closeEntry()
        FileVisitResult.CONTINUE
    }

    @Override
    FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        zipOutputStream.closeEntry()
        FileVisitResult.CONTINUE
    }

    String getFilePathRelativeToBase(Path path, boolean directory) {
        baseDir.relativize(path).toString() + "${directory ? '/' : ''}"
    }
}
