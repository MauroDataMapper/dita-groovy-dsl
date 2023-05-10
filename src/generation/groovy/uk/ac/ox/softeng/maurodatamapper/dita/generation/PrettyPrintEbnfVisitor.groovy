/*
 * Copyright 2020-2023 University of Oxford and NHS England
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
package uk.ac.ox.softeng.maurodatamapper.dita.generation

import uk.ac.ox.softeng.ebnf.parser.EbnfBaseVisitor
import uk.ac.ox.softeng.ebnf.parser.EbnfParser

class PrettyPrintEbnfVisitor extends EbnfBaseVisitor<String> {

    @Override
    String visitOneOrMore(EbnfParser.OneOrMoreContext ctx) {
        visit(ctx.expression()) + '+'
    }

    @Override
    String visitChoice(EbnfParser.ChoiceContext ctx) {
        visit(ctx.expression(0)) + ' | ' + visit(ctx.expression(1))
    }

    @Override
    String visitBrackets(EbnfParser.BracketsContext ctx) {
        '(' + visit(ctx.expression()) + ')'
    }

    @Override
    String visitOptional(EbnfParser.OptionalContext ctx) {
        visit(ctx.expression()) + '?'
    }

    @Override
    String visitText(EbnfParser.TextContext ctx) {
        ctx.getText()
    }

    @Override
    String visitId(EbnfParser.IdContext ctx) {
        ctx.getText()
    }

    @Override
    String visitSequence(EbnfParser.SequenceContext ctx) {
        visit(ctx.expression(0)) + ' ' + visit(ctx.expression(1))
    }

    @Override
    String visitNoneOrMore(EbnfParser.NoneOrMoreContext ctx) {
        visit(ctx.expression()) + '*'
    }

    @Override
    String visitText_(EbnfParser.Text_Context ctx) {
        ctx.getText()
    }

    @Override
    String visitId_(EbnfParser.Id_Context ctx) {
        ctx.getText()
    }

}
