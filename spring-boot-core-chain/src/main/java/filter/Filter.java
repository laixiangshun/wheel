package filter;

import chain.FilterChain;
import metadata.Request;
import metadata.Response;

public interface Filter {
    void doChain(Request request, Response response, FilterChain chain);
}
