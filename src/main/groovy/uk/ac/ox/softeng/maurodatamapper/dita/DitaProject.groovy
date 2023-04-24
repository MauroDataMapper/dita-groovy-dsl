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

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.KeyDef
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.DitaMap
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.MapRef
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.TopicMeta
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.TopicRef

import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Toc

import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class DitaProject {

    static final String FILE_SEPARATOR = System.getProperty('file.separator')

    String filename
    String title
    TopicMeta topicMeta

    DitaProjectOptions options = new DitaProjectOptions()

    Map<String, List<Tuple2<Topic, Toc>>> topicMap = [:]
    Map<String, List<Tuple2<DitaMap, Toc>>> mapMap = [:]
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

    Path writeToDirectory(Path directory) {
        log.info('Writing DitaProject to {}', directory)
        Path directoryPath = getDirectory(directory)
        Path topicsDirectoryPath = getDirectory(directoryPath, 'topics')
        Path mapsDirectoryPath = getDirectory(directoryPath, 'maps')

        topLevelFolders.each {folderName ->
            getDirectory(directoryPath, folderName)
        }

        topicMap.each {String path, List<Tuple2<Topic, Toc>> topicList ->
            Path fullPath = path ? topicsDirectoryPath.resolve(path) : topicsDirectoryPath
            outputTopics(fullPath, topicList)
        }

        mapMap.each {String path, List<Tuple2<DitaMap, Toc>> mapList ->
            Path fullPath = path ? mapsDirectoryPath.resolve(path) : mapsDirectoryPath
            outputMaps(fullPath, mapList)
        }


        writeInternalLinks(directoryPath)
        writeExternalLinks(directoryPath)
        writeDitaMap(directoryPath)
    }

    void outputTopics(Path path, List<Tuple2<Topic, Toc>> topicList) {
        topicList.each {tuple ->
            Topic topic = tuple.getV1()
            Path topicPath = path.resolve("${topic.id}.dita")
            if (options.filePerTopic) topic.writeToFile(topicPath)
        }
    }

    void outputMaps(Path path, List<Tuple2<DitaMap, Toc>> mapList) {
        mapList.each {tuple ->
            DitaMap ditaMap = tuple.getV1()
            //            Toc toc = tuple.getV2()

            Path mapPath = path.resolve("${ditaMap.id}.ditamap")
            if (options.filePerTopic) ditaMap.writeToFile(mapPath)
        }
    }

    Path writeDitaMap(Path directory) {
        DitaMap mainDitaMap = DitaMap.build {
            title this.title
            topicMeta this.topicMeta
            mapRef(
                href: 'links/internalLinks.ditamap',
                processingRole: ProcessingRole.RESOURCE_ONLY,
                ) {}
            mapRef(
                href: 'links/externalLinks.ditamap',
                processingRole: ProcessingRole.RESOURCE_ONLY,
                ) {}
        }

        topicMap.each {path, topicList ->
            topicList.each {tuple ->
                Topic topic = tuple.getV1()
                Toc toc = tuple.getV2()
                String newPath = "topics${FILE_SEPARATOR}${path}"
                if (path == '') {
                    newPath = 'topics'
                }
                mainDitaMap.topicRef getTopicRefForTopic(topic, toc, newPath)
            }
        }
        mapMap.each {path, mapList ->
            mapList.each {tuple ->
                DitaMap thisDitaMap = tuple.getV1()
                Toc toc = tuple.getV2()
                String newPath = "maps${FILE_SEPARATOR}${path}"
                if (path == '') {
                    newPath = 'maps'
                }
                mainDitaMap.mapRef getMapRefForDitaMap(thisDitaMap, toc, newPath)
            }
        }
        Path ditamapFilepath = directory.resolve("${filename}.ditamap")
        mainDitaMap.writeToFile(ditamapFilepath)
    }

    void writeInternalLinks(Path directory) {
        DitaMap ditaMap = DitaMap.build {
            title 'Keys for various linked topics'

            topicMap.each {path, topicList ->
                topicList.each {tuple ->
                    Topic topic = tuple.getV1()
//                    Toc toc = tuple.getV2()
                    String newPath = "../topics${FILE_SEPARATOR}${path}"
                    if (path == '') {
                        newPath = '../topics'
                    }
                    getKeyDefsForTopic(topic, newPath).each {topicKeyDef ->
                        keyDef topicKeyDef
                    }
                }
            }
        }
        Path ditamapFilename = getDirectory(directory, 'links').resolve('internalLinks.ditamap')
        ditaMap.writeToFile(ditamapFilename)
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

    List<KeyDef> getKeyDefsForSubTopic(String href, Topic topic) {
        List<KeyDef> keyDefList = []
        keyDefList.add(new KeyDef(
            keys: [topic.id],
            href: href + '#' + topic.id
        ))
        topic.getTopics().each {subTopic ->
            keyDefList.addAll(getKeyDefsForSubTopic(href, subTopic))
        }

        keyDefList
    }

    List<KeyDef> getKeyDefsForTopic(Topic topic, String path) {
        String href = "${path}${FILE_SEPARATOR}${topic.id}.dita"
        List<KeyDef> keyDefList = []
        keyDefList.add(new KeyDef(
            keys: [topic.id],
            href: href
        ))
        topic.getTopics().each {subTopic ->
            keyDefList.addAll(getKeyDefsForSubTopic(href, subTopic))
        }
        keyDefList
    }

    TopicRef getTopicRefForTopic(Topic topic, Toc toc, String path) {
        TopicRef.build(
            href: "${path}${FILE_SEPARATOR}${topic.id}.dita",
            toc: toc,
            ) {
        }
    }

    MapRef getMapRefForDitaMap(DitaMap ditaMap, Toc toc, String path) {
        MapRef.build(
            href: "${path}${FILE_SEPARATOR}${ditaMap.id}.ditamap",
            toc: toc,
            ) {
        }
    }

    void addTopic(String path, Topic topic, Toc toc) {
        List<Tuple2<Topic, Toc>> existingTupleList = topicMap[path]

        if (existingTupleList) {
            existingTupleList.add(new Tuple2<Topic, Toc>(topic, toc))
        } else {
            topicMap[path] = new ArrayList<Tuple2<Topic, Toc>>([new Tuple2(topic, toc)])
        }
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
