package uk.ac.ox.softeng.maurodatamapper.dita.generation

import uk.ac.ox.softeng.ebnf.parser.EbnfBaseVisitor
import uk.ac.ox.softeng.ebnf.parser.EbnfParser

class SetContainmentEbnfVisitor extends EbnfBaseVisitor<Set<String>> {

    @Override
    Set<String> visitOneOrMore(EbnfParser.OneOrMoreContext ctx) {
        visit(ctx.expression())
    }

    @Override
    Set<String> visitChoice(EbnfParser.ChoiceContext ctx) {
        ctx.expression().collect {visit(it) }.flatten()
    }

    @Override
    Set<String> visitBrackets(EbnfParser.BracketsContext ctx) {
        visit(ctx.expression())
    }

    @Override
    Set<String> visitOptional(EbnfParser.OptionalContext ctx) {
        ctx.expression().collect {visit(it) }.flatten()
    }

    @Override
    Set<String> visitText(EbnfParser.TextContext ctx) {
        [ctx.getText()]
    }

    @Override
    Set<String> visitId(EbnfParser.IdContext ctx) {
        [ctx.getText()]
    }

    @Override
    Set<String> visitSequence(EbnfParser.SequenceContext ctx) {
        ctx.expression().collect {visit(it) }.flatten()
    }

    @Override
    Set<String> visitNoneOrMore(EbnfParser.NoneOrMoreContext ctx) {
        visit(ctx.expression())
    }

    @Override
    Set<String> visitText_(EbnfParser.Text_Context ctx) {
        [ctx.getText()]
    }

    @Override
    Set<String> visitId_(EbnfParser.Id_Context ctx) {
        [ctx.getText()]
    }

}
