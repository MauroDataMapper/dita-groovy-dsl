/*
 * Copyright 2020-2024 University of Oxford and NHS England
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
import uk.ac.ox.softeng.maurodatamapper.dita.helpers.IdHelper

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class DitaProject {

    static final String FILE_SEPARATOR = System.getProperty('file.separator')

    String title
    String filename

    DitaMap mainMap

    Map<String, Topic> topicsById = [:]
    Map<String, String> topicHrefs = [:]
    Map<String, String> topicCustomFilenames = [:]

    Map<String, DitaMap> mapsById = [:]
    Map<String, String> mapHrefs = [:]
    Map<String, String> mapCustomFilenames = [:]

    Map<String, byte[]> imagesById = [:]
    Map<String, String> imageHrefs = [:]
    Map<String, String> imageFormats = [:]

    Map<String, String> externalKeyMap = [:]

    boolean useTopicsFolder = true

    List<String> topLevelFolders = [
        'filters',
        'images',
        'links',
        'maps',
        'reuse',
        'tasks',
        'topics'
    ]


    DitaProject(String projectTitle, String filename) {
        this.title = projectTitle
        this.filename = filename
        mainMap = DitaMap.build(id: IdHelper.makeValidId(filename)) {
            title this.title
        }
    }

    void registerTopic(String path, Topic topic, String customFilename = null) {
        if(!IdHelper.isValidId(topic.id)) {
            throw new Exception("The topic id '${topic.id}' is not valid")
        }
        if(topicsById[topic.id]) {
            throw new Exception("A topic with the id '${topic.id}' has already been registered")
        }
        topicsById[topic.id] = topic
        topicHrefs[topic.id] = path
        if(customFilename) {
            topicCustomFilenames[topic.id] = customFilename
        }
    }

    void registerMap(String path, DitaMap map, String customFilename = null) {
        if(!IdHelper.isValidId(map.id)) {
            throw new Exception("The map id '${map.id}' is not valid")
        }
        if(mapsById[map.id]) {
            throw new Exception("A map with the id '${map.id}' has already been registered")
        }
        mapsById[map.id] = map
        mapHrefs[map.id] = path
        if(customFilename) {
            mapCustomFilenames[map.id] = customFilename
        }
    }

    void registerImage(String path, String id, String format, byte[] image) {
        if(!IdHelper.isValidId(id)) {
            throw new Exception("The image id '${id}' is not valid")
        }

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
        Path topicsDirectoryPath = useTopicsFolder? getDirectory(directoryPath, 'topics') : directoryPath
        Path mapsDirectoryPath = getDirectory(directoryPath, 'maps')
        Path imagesDirectoryPath = getDirectory(directoryPath, 'images')

        topLevelFolders.each {folderName ->
            getDirectory(directoryPath, folderName)
        }

        topicsById.each {String id, Topic topic ->
            String localPath = '.dita'
            if(topicCustomFilenames[id]) {
                localPath = topicCustomFilenames[id] + localPath
            } else {
                localPath = id + localPath
            }
            if(topicHrefs[id]) {
                localPath = topicHrefs[id] + FILE_SEPARATOR + localPath
            }
            Path fullPath = topicsDirectoryPath.resolve(localPath)
            topic.writeToFile(fullPath)
        }

        mapsById.each {String id, DitaMap map ->
            String localPath = '.ditamap'
            if(mapCustomFilenames[id]) {
                localPath = mapCustomFilenames[id] + localPath
            } else {
                localPath = id + localPath
            }
            if(mapHrefs[id]) {
                localPath = mapHrefs[id] + FILE_SEPARATOR + localPath
            }
            Path fullPath = mapsDirectoryPath.resolve(localPath)
            map.writeToFile(fullPath)
        }

        imagesById.each {String id, byte[] bytes ->
            String path = imageHrefs[id]
            Path fullPath = path ? imagesDirectoryPath.resolve(path) : imagesDirectoryPath
            Files.createDirectories(fullPath.getParent())
            Files.write(fullPath, bytes, StandardOpenOption.CREATE)
        }


        writeInternalLinks(directoryPath)
        writeExternalLinks(directoryPath)
        Path mapPath = directoryPath.resolve("${filename}.ditamap")
        ['internalImageLinks','internalTopicLinks', 'internalMapLinks','externalLinks'].each {mapName ->
            mainMap.mapRef(
                href: "links/${mapName}.ditamap",
                processingRole: ProcessingRole.RESOURCE_ONLY,
                ) {}
        }
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
                String filename = topicCustomFilenames.get(key, key)
                String href = "${path}${FILE_SEPARATOR}${filename}.dita"
                while (href.startsWith(FILE_SEPARATOR)) {
                    href = href.substring(1)    // (replaceFirst() with an unescaped backslash gets gnarly)
                }
                if(useTopicsFolder) {
                    href = "..${FILE_SEPARATOR}topics${FILE_SEPARATOR}" + href
                } else {
                    href = "..${FILE_SEPARATOR}" + href
                }

                keyDef(
                    keys: [key],
                    href: href,
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
                String filename = mapCustomFilenames.get(key, key)
                String href = "${path}${FILE_SEPARATOR}${filename}.ditamap"
                while (href.startsWith(FILE_SEPARATOR)) {
                    href = href.substring(1)
                }
                keyDef(
                        keys: [key],
                        href: "..${FILE_SEPARATOR}maps${FILE_SEPARATOR}${href}",
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
                        href: "..${FILE_SEPARATOR}images${FILE_SEPARATOR}${path.replace(" ", "%20")}",
                        scope: Scope.LOCAL,
                        format: imageFormats[key]
                )
            }
        }
        ditamapFilename = getDirectory(directory, 'links').resolve('internalImageLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)
    }


    void addExternalKey(String key, String url) {
        if(!IdHelper.isValidId(key)) {
            throw new Exception("The external key '${key}' is not valid")
        }
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
