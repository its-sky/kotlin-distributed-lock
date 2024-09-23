package distribution.lock.util

import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

object CustomSpringELParser {
    fun getDynamicValue(parameterNames: Array<String>, args: Array<Any>, key: String): Any? {
        val parser = SpelExpressionParser()
        val context = StandardEvaluationContext()

        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], args[i])
        }

        return parser.parseExpression(key).getValue(context, Any::class.java)
    }
}