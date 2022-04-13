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

package uk.ac.ox.softeng.maurodatamapper.dita.generation

import uk.ac.ox.softeng.ebnf.parser.EbnfBaseVisitor
import uk.ac.ox.softeng.ebnf.parser.EbnfParser

class PrettyPrintEbnfVisitor extends EbnfBaseVisitor<String> {

    @Override
    public String visitOneOrMore(EbnfParser.OneOrMoreContext ctx) {
        return visit(ctx.expression()) + "+"
    }

    @Override
    public String visitChoice(EbnfParser.ChoiceContext ctx) {
        return visit(ctx.expression(0)) + " | " + visit(ctx.expression(1))
    }

    @Override
    public String visitBrackets(EbnfParser.BracketsContext ctx) {
        return "(" + visit(ctx.expression()) + ")"
    }

    @Override
    public String visitOptional(EbnfParser.OptionalContext ctx) {
        return visit(ctx.expression()) + "?"
    }

    @Override
    public String visitText(EbnfParser.TextContext ctx) {
        return ctx.getText()
    }

    @Override
    public String visitId(EbnfParser.IdContext ctx) {
        return ctx.getText()
    }

    @Override public String visitSequence(EbnfParser.SequenceContext ctx) {
        return visit(ctx.expression(0)) + " " + visit(ctx.expression(1))
    }

    @Override
    public String visitNoneOrMore(EbnfParser.NoneOrMoreContext ctx) {
        return visit(ctx.expression()) + "*"
    }

    @Override
    public String visitText_(EbnfParser.Text_Context ctx) {
        return ctx.getText()
    }

    @Override
    public String visitId_(EbnfParser.Id_Context ctx) {
        return ctx.getText()
    }

}
