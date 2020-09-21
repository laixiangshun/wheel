package filter;

import chain.FilterChain;
import metadata.Request;
import metadata.Response;

public class SensitiveFilter implements Filter {
    @Override
    public void doChain(Request request, Response response, FilterChain chain) {
        request.setRequestStr(request.getRequestStr()
                .replace("被就业", "就业").replace("敏感", "") +
                " ---sensitiveFilter()");
        chain.doChain(request, response, chain);
        response.setResponseStr(response.getResponseStr() + this.getClass().getName());
    }
}
