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
package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Scale implements DitaEnum {
    FIFTY('50'),
    SIXTY('60'),
    SEVENTY('70'),
    EIGHTY('80'),
    NINETY('90'),
    ONE_HUNDRED('100'),
    ONE_HUNDRED_TEN('110'),
    ONE_HUNDRED_TWENTY('120'),
    ONE_HUNDRED_FORTY('140'),
    ONE_HUNDRED_SIXTY('160'),
    ONE_HUNDRED_EIGHTY('180'),
    TWO_HUNDRED('200'),
    DITA_USE_CONREF_TARGET('-dita-use-conref-target')

    Scale(String stringValue) {
        this.stringValue = stringValue
    }

}
