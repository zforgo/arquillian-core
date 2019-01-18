package org.jboss.arquillian.junit5;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface ExecutionConditionEvaluator {

    ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context,
        ConditionEvaluationResult previousResult);
}
