package optionalrest.core.services.mapping.providers;

import java.util.function.Function;

public interface Evaluator<T> extends Function<T, Boolean> {
}
