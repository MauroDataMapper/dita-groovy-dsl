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
