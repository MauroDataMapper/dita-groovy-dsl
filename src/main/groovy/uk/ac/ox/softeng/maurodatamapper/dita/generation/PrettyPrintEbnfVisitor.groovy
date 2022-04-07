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
