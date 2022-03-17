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

import uk.ac.ox.softeng.maurodatamapper.dita.elements.DitaMap
import uk.ac.ox.softeng.maurodatamapper.dita.elements.KeyDef
import uk.ac.ox.softeng.maurodatamapper.dita.elements.MapRef
import uk.ac.ox.softeng.maurodatamapper.dita.elements.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.elements.TopicRef
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

class DitaProject {

    String filename
    String title

    DitaProjectOptions options = new DitaProjectOptions()

    Map<String, List<Topic>> topicMap = [:]
    Map<String, String> externalKeyMap = [:]

    List<String> topLevelFolders = [
        "filters",
        "images",
        "links",
        "reuse",
        "tasks",
        "topics"
    ]


    static final String fileSeparator = System.getProperty("file.separator")

    void writeToDirectory(String directory) {

        File baseDirectory = createDirectory(directory, "")

        Map<String, File> folders = topLevelFolders.collectEntries {folderName ->
            [folderName, createDirectory(directory, folderName)]
        }
        writeDitaMap(directory)
        topicMap.each {String path, List<Topic> topicList ->
            String fullPath = "${directory}${fileSeparator}topics${fileSeparator}${path}"
            if(path == "") {
                fullPath = "${directory}${fileSeparator}topics"
            }
            outputTopics(fullPath, topicList)
        }
        createInternalLinks(directory)
        createExternalLinks(directory)

    }

    void outputTopics(String path, List<Topic> topicList) {
        topicList.each {topic ->
            String topicPath = "${path}${fileSeparator}${topic.id}.dita"
            if(path == "") {
                topicPath = "${topic.id}.dita"
            }
            if(options.filePerTopic) {
                topic.outputAsFile(topicPath, false)
                if(topic.subTopics) {
                    String newPath = "${path}${fileSeparator}${topic.id}"
                    if(path == "") {
                        newPath = "${topic.id}"
                    }
                    createDirectory(path, topic.id)
                    outputTopics(newPath, topic.subTopics)
                }
            } else {
                topic.outputAsFile(topicPath, true)
            }
        }
    }

    File createDirectory(String basePath, String dirName) {
        File newDir = new File("${basePath}${fileSeparator}${dirName}")
        if(!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    void writeDitaMap (String directory) {
        DitaMap ditaMap = DitaMap.build {
            title this.title
        }

        topicMap.each { path, topicList ->
            topicList.each {topic ->
                String newPath = "topics${fileSeparator}${path}"
                if(path == "") {
                    newPath = "topics"
                }
                ditaMap.topicRefs << addTopicRefToDitaMap(topic, newPath)
            }
        }
        ditaMap.mapRefs.add(new MapRef(href: "links/internalLinks.ditamap", processingRole: ProcessingRole.RESOURCE_ONLY))
        ditaMap.mapRefs.add(new MapRef(href: "links/externalLinks.ditamap", processingRole: ProcessingRole.RESOURCE_ONLY))
        String ditamapFilename = "${directory}${fileSeparator}${filename}.ditamap"
        ditaMap.outputAsFile(ditamapFilename)
    }

    void createInternalLinks (String directory) {
        DitaMap ditaMap = DitaMap.build {
            title "Keys for various linked topics"
        }

        topicMap.each { path, topicList ->
            topicList.each {topic ->
                String newPath = "../topics${fileSeparator}${path}"
                if(path == "") {
                    newPath = "../topics"
                }
                ditaMap.keyDefs.addAll(addTopicKeyDefToDitaMap(topic, newPath))
            }
        }
        String ditamapFilename = "${directory}${fileSeparator}links${fileSeparator}internalLinks.ditamap"
        ditaMap.outputAsFile(ditamapFilename)
    }


    void createExternalLinks (String directory) {
        DitaMap ditaMap = DitaMap.build {
            title "External Links Key Definitions"
        }

        externalKeyMap.each {key, url ->
            ditaMap.keyDefs.add(new KeyDef(
                keys: new SpaceSeparatedStringList([key]),
                href: url,
                scope: Scope.EXTERNAL,
                format: Format.HTML))
        }

        String ditamapFilename = "${directory}${fileSeparator}links${fileSeparator}externalLinks.ditamap"
        ditaMap.outputAsFile(ditamapFilename)
    }

    List<KeyDef> addTopicKeyDefToDitaMap(Topic topic, String path) {
        KeyDef keyDef = new KeyDef(
            keys: new SpaceSeparatedStringList([topic.id]), href: "${path}${fileSeparator}${topic.id}.dita")

        List<KeyDef> keyDefList = [keyDef]

        if(topic.subTopics && options.filePerTopic) {
            topic.subTopics.each {subTopic ->
                keyDefList.addAll(addTopicKeyDefToDitaMap(subTopic, "${path}${fileSeparator}${topic.id}"))
            }
        }
        return keyDefList

    }

    TopicRef addTopicRefToDitaMap(Topic topic, String path) {
        TopicRef topicRef = new TopicRef(
            topicRef: topic, href: "${path}${fileSeparator}${topic.id}.dita", toc: topic.toc)

        if(topic.subTopics && options.filePerTopic) {
            topicRef.subTopicRefs = topic.subTopics.collect {subTopic ->
                addTopicRefToDitaMap(subTopic, "${path}${fileSeparator}${topic.id}")
            }
        }
        return topicRef


    }


    void addTopic(String path, Topic topic) {
        List<Topic> existingTopics = topicMap[path]
        if(existingTopics) {
            existingTopics.add(topic)
        } else {
            topicMap[path] = [topic]
        }
    }

    void addExternalKey(String key, String url) {
        externalKeyMap[key] = url

    }


}
