package chain;

import filter.Filter;
import metadata.Request;
import metadata.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义责任链
 */
public class FilterChain implements Filter {

    private List<Filter> filters = new ArrayList<>();
    private int index = 0;

    public FilterChain addFilters(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void doChain(Request request, Response response, FilterChain chain) {
        if (index == filters.size()) {
            return;
        }
        Filter filter = filters.get(index);
        index++;
        filter.doChain(request, response, chain);
    }
}
