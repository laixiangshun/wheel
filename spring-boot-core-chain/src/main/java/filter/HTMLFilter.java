package filter;

import chain.FilterChain;
import metadata.Request;
import metadata.Response;

public class HTMLFilter implements Filter {
    @Override
    public void doChain(Request request, Response response, FilterChain chain) {
        request.setRequestStr(request.getRequestStr()
                .replace('<', '[').replace('>', ']') + "----HTMLFilter()");
        chain.doChain(request, response, chain);
        response.setResponseStr(response.getResponseStr() + "---HTMLFilter()");
    }
}
