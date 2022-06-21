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
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Toc

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DitaProject2 {

    static final String FILE_SEPARATOR = System.getProperty('file.separator')

    String title
    String filename

    DitaMap mainMap

    Map<String, List<Topic>> topicMap = [:]
    Map<String, List<DitaMap>> mapsByPath = [:]
    Map<String, DitaMap> mapsById = [:]

    Map<String, String> internalKeyMap = [:]
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

    DitaProject2(String projectTitle, String filename) {
        this.title = projectTitle
        this.filename = filename
        mainMap = DitaMap.build(id: filename) {
            title this.title
            mapRef(
                href: 'links/internalLinks.ditamap',
                processingRole: ProcessingRole.RESOURCE_ONLY,
                ) {}
            mapRef(
                href: 'links/externalLinks.ditamap',
                processingRole: ProcessingRole.RESOURCE_ONLY,
                ) {}
        }
        mapsById[filename] = mainMap
    }

    void addTopic(String path, Topic topic) {
        List<Topic> topicsAtPath = topicMap[path]
        if(topicsAtPath) {
            topicsAtPath.add(topic)
        } else {
            topicMap[path] = [topic]
        }
    }

    void addTopicToMainMap(String path, Topic topic, Toc toc) {
        addTopic(path, topic)
        String href = "topics${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${topic.id}.dita"
        mainMap.topicRef (
            href: href,
            toc: toc,
        )
        internalKeyMap[topic.id] = """..${FILE_SEPARATOR}${href}""".toString()
    }

    void addTopicToMapById(String path, Topic topic, String mapId, Toc toc ) {
        addTopic(path, topic)
        DitaMap ditaMap = mapsById[mapId]
        String topicPath = "${FILE_SEPARATOR}topics${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${topic.id}.dita"
        String href = getMapPathToRoot(ditaMap)
        href += topicPath
        ditaMap.topicRef (
            href: href,
            toc: toc,
        )
        internalKeyMap[topic.id] = """..${FILE_SEPARATOR}${topicPath}""".toString()
    }

    void addMap(String path, DitaMap ditaMap) {
        List<DitaMap> mapsAtPath = mapsByPath[path]
        if(mapsAtPath) {
            mapsAtPath.add(ditaMap)
        } else {
            mapsByPath[path] = [ditaMap]
        }
        mapsById[ditaMap.id] = ditaMap
    }

    DitaMap addMap(String path, String id, String mapTitle) {
        DitaMap ditaMap = DitaMap.build(id: id) {
            title mapTitle
        }
        List<DitaMap> mapsAtPath = mapsByPath[path]
        if(mapsAtPath) {
            mapsAtPath.add(ditaMap)
        } else {
            mapsByPath[path] = [ditaMap]
        }
        mapsById[id] = ditaMap
        return ditaMap
    }

    void addMapToMainMap(String path, String id, String mapTitle, Toc toc) {
        DitaMap newMap = addMap(path, id, mapTitle)
        mainMap.mapRef (
            href: "maps${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${id}.ditamap",
            keys: [id],
            toc: toc,
        )
        mapsById[id] = newMap
    }

    void addMapToMainMap(String path, DitaMap ditaMap, Toc toc) {
        addMap(path, ditaMap)
        mainMap.mapRef (
            href: "maps${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${ditaMap.id}.ditamap",
            keys: [ditaMap.id],
            toc: toc
        )
        mapsById[ditaMap.id] = ditaMap
    }

    void addMapToMapById(String path, String id, String mapTitle, String mapId, Toc toc) {
        DitaMap newMap = addMap(path, id, mapTitle)
        DitaMap parentMap = mapsById[mapId]
        parentMap.mapRef (
            href: "maps${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${id}.ditamap",
            keys: [id],
            toc: toc,
        )
    }

    void addMapToMapById(String path, DitaMap ditaMap, String mapPath, Toc toc) {
        addMap(path, ditaMap)
        DitaMap parentDitaMap = mapsById[mapPath]
        parentDitaMap.mapRef (
            href: "maps${FILE_SEPARATOR}${path}${path!=''?FILE_SEPARATOR:''}${ditaMap.id}.ditamap",
            keys: [ditaMap.id],
            toc: toc
        )
    }

    Path writeToDirectory(String directoryStr) {
        Path p = Paths.get(directoryStr)
        writeToDirectory(p)
    }

    Path writeToDirectory(Path directory) {
        Path directoryPath = getDirectory(directory)
        Path topicsDirectoryPath = getDirectory(directoryPath, 'topics')
        Path mapsDirectoryPath = getDirectory(directoryPath, 'maps')

        topLevelFolders.each {folderName ->
            getDirectory(directoryPath, folderName)
        }

        topicMap.each {String path, List<Topic> topicList ->
            Path fullPath = path ? topicsDirectoryPath.resolve(path) : topicsDirectoryPath
            outputTopics(fullPath, topicList)
        }

        mapsByPath.each {String path, List<DitaMap> mapList ->
            Path fullPath = path ? mapsDirectoryPath.resolve(path) : mapsDirectoryPath
            outputMaps(fullPath, mapList)
        }


        writeInternalLinks(directoryPath)
        writeExternalLinks(directoryPath)
        Path mapPath = directoryPath.resolve("${filename}.ditamap")
        mainMap.writeToFile(mapPath)
    }

    void outputTopics(Path path, List<Topic> topicList) {
        topicList.each {topic ->
            Path topicPath = path.resolve("${topic.id}.dita")
            topic.writeToFile(topicPath)
        }
    }

    void outputMaps(Path path, List<DitaMap> mapList) {
        mapList.each {ditaMap ->
            Path mapPath = path.resolve("${ditaMap.id}.ditamap")
            ditaMap.writeToFile(mapPath)
        }
    }

    void writeExternalLinks(Path directory) {
        DitaMap ditaMap = DitaMap.build {
            title 'External Links Key Definitions'

            externalKeyMap.each {key, url ->
                keyDef(
                    keys: [key],
                    href: url,
                    scope: Scope.EXTERNAL,
                    format: Format.HTML,
                    )
            }
        }
        Path ditamapFilename = getDirectory(directory, 'links').resolve('externalLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)
    }

    void writeInternalLinks(Path directory) {
        DitaMap ditaMap = DitaMap.build {
            title 'Internal Links Key Definitions'

            internalKeyMap.each {key, url ->
                keyDef(
                    keys: [key],
                    href: url,
                    scope: Scope.LOCAL,
                    format: Format.DITA,
                    )
            }
        }
        Path ditamapFilename = getDirectory(directory, 'links').resolve('internalLinks.ditamap')
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


    String getMapPathToRoot(DitaMap ditaMap) {

        String path = mapsByPath.find{key, values -> values.contains(ditaMap)}.key

        path.count("/").collect{ ".."}.join("/")

    }



}
