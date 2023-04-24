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
package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.DitaMap
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DitaProject3 {

    static final String FILE_SEPARATOR = System.getProperty('file.separator')

    String title
    String filename

    DitaMap mainMap

    Map<String, Topic> topicsById = [:]
    Map<String, String> topicHrefs = [:]

    Map<String, DitaMap> mapsById = [:]
    Map<String, String> mapHrefs = [:]

    Map<String, byte[]> imagesById = [:]
    Map<String, String> imageHrefs = [:]
    Map<String, String> imageFormats = [:]

    Map<String, String> externalKeyMap = [:]


    List<String> topLevelFolders = [
        'filters',
        'images',
        'links',
        'maps',
        'reuse',
        'tasks',
        'topics'
    ]

    DitaProject3(String projectTitle, String filename) {
        this.title = projectTitle
        this.filename = filename
        mainMap = DitaMap.build(id: filename) {
            title this.title

            ['internalImageLinks','internalTopicLinks', 'internalMapLinks','externalLinks'].each {mapName ->
                mapRef(
                    href: "links/${mapName}.ditamap",
                    processingRole: ProcessingRole.RESOURCE_ONLY,
                    ) {}
            }
        }
    }

    void registerTopic(String path, Topic topic) {
        if(topicsById[topic.id]) {
            throw new Exception("A topic with the id '${topic.id}' has already been registered")
        }
        topicsById[topic.id] = topic
        topicHrefs[topic.id] = path
    }

    void registerMap(String path, DitaMap map) {
        if(mapsById[map.id]) {
            throw new Exception("A map with the id '${map.id}' has already been registered")
        }
        mapsById[map.id] = map
        mapHrefs[map.id] = path
    }

    void registerImage(String path, String id, String format, byte[] image) {
        if(imagesById[id]) {
            throw new Exception("An image with the id '${id}' has already been registered")
        }
        imagesById[id] = image
        imageHrefs[id] = path
        imageFormats[id] = format
    }


    Path writeToDirectory(String directoryStr) {
        Path p = Paths.get(directoryStr)
        writeToDirectory(p)
    }

    Path writeToDirectory(Path directory) {
        Path directoryPath = getDirectory(directory)
        Path topicsDirectoryPath = getDirectory(directoryPath, 'topics')
        Path mapsDirectoryPath = getDirectory(directoryPath, 'maps')
        Path imagesDirectoryPath = getDirectory(directoryPath, 'images')

        topLevelFolders.each {folderName ->
            getDirectory(directoryPath, folderName)
        }

        topicsById.each {String id, Topic topic ->
            String path = topicHrefs[id]
            Path fullPath = topicsDirectoryPath.resolve(path + FILE_SEPARATOR + id + ".dita")
            topic.writeToFile(fullPath)
        }

        mapsById.each {String id, DitaMap map ->
            String path = mapHrefs[id]
            Path fullPath = mapsDirectoryPath.resolve(path + FILE_SEPARATOR + id + ".ditamap")
            map.writeToFile(fullPath)
        }

        imagesById.each {String id, byte[] bytes ->
            String path = imageHrefs[id]
            Path fullPath = path ? imagesDirectoryPath.resolve(path) : imagesDirectoryPath
            Files.createDirectories(fullPath.getParent())
            Files.createFile(fullPath)
            Files.write(fullPath, bytes)
        }


        writeInternalLinks(directoryPath)
        writeExternalLinks(directoryPath)
        Path mapPath = directoryPath.resolve("${filename}.ditamap")
        mainMap.writeToFile(mapPath)
    }

    void writeExternalLinks(Path directory) {
        DitaMap ditaMap = DitaMap.build {
            title 'External Links Key Definitions'

            externalKeyMap.each {key, url ->
                keyDef(
                    keys: [key],
                    href: url,
                    scope: Scope.EXTERNAL,
                    format: "html",
                    )
            }
        }
        Path ditamapFilename = getDirectory(directory, 'links').resolve('externalLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)
    }

    void writeInternalLinks(Path directory) {
        DitaMap ditaMap = DitaMap.build {
            title 'Internal Links Topic Key Definitions'

            topicHrefs.each {key, path ->
                keyDef(
                    keys: [key],
                    href: "..${FILE_SEPARATOR}topics${FILE_SEPARATOR}${path}${FILE_SEPARATOR}${key}.dita",
                    scope: Scope.LOCAL,
                    format: "dita",
                    )
            }
        }
        Path ditamapFilename = getDirectory(directory, 'links').resolve('internalTopicLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)

        ditaMap = DitaMap.build {
            title 'Internal Links Map Key Definitions'

            mapHrefs.each {key, path ->
                keyDef(
                        keys: [key],
                        href: "..${FILE_SEPARATOR}maps${FILE_SEPARATOR}${path}${FILE_SEPARATOR}${key}.ditamap",
                        scope: Scope.LOCAL,
                        format: "ditamap",
                )
            }
        }
        ditamapFilename = getDirectory(directory, 'links').resolve('internalMapLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)

        ditaMap = DitaMap.build {
            title 'Internal Links Image Key Definitions'
            imageHrefs.each {key, path ->
                keyDef(
                        keys: [key],
                        href: "..${FILE_SEPARATOR}images${FILE_SEPARATOR}${path}",
                        scope: Scope.LOCAL,
                        format: imageFormats[key]
                )
            }
        }
        ditamapFilename = getDirectory(directory, 'links').resolve('internalImageLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)
    }


    void addExternalKey(String key, String url) {
        externalKeyMap[key] = url
    }

    private static Path getDirectory(String basePath, String dirName) {
        getDirectory(Paths.get(basePath), dirName)
    }

    private static Path getDirectory(Path basePath, String dirName = null) {
        Path path = dirName ? basePath.resolve(dirName) : basePath
        if (Files.notExists(path)) {
            Files.createDirectories(path)
        }
        path
    }

}
