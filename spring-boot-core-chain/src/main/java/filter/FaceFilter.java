package filter;

import chain.FilterChain;
import metadata.Request;
import metadata.Response;

public class FaceFilter implements Filter {
    @Override
    public void doChain(Request request, Response response, FilterChain chain) {
        request.setRequestStr(request.getRequestStr().replace(":):", "^V^")
                //后面添加的是便于我们观察代码执行步骤的字符串
                + "----FaceFilter()");
        chain.doChain(request, response, chain);
        response.setResponseStr(response.getResponseStr() + this.getClass().getName());
    }
}
