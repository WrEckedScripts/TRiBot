package scripts.utils.utility.searching.algorithms;

import scripts.utils.utility.searching.StringProcessor;

/**
 * @deprecated Use {@code ToStringFunction#NO_PROCESS} instead.
 */
@Deprecated
public class NoProcess extends StringProcessor {

    @Override
    @Deprecated
    public String process(String in) {
        return in;
    }

}
