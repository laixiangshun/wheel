import chain.FilterChain;
import filter.FaceFilter;
import filter.HTMLFilter;
import filter.SensitiveFilter;
import metadata.Request;
import metadata.Response;

public class FilterMain {
    public static void main(String[] args) {
        String msg = ":):,<script>,敏感,被就业,网络授课";
        Request request = new Request();
        request.setRequestStr(msg);
        Response response = new Response();
        response.setResponseStr("response:");
        //定义责任链，采用链式调用
        FilterChain chain = new FilterChain();
        chain.addFilters(new HTMLFilter())
                .addFilters(new FaceFilter())
                .addFilters(new SensitiveFilter());
        chain.doChain(request, response, chain);
        System.out.println(request.getRequestStr());
        System.out.println(response.getResponseStr());
    }
}
