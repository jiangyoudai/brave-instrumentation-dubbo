package com.jiangyoudai.commons.trace.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboAdapter;

import zipkin2.Endpoint.Builder;

public class DubboProviderAdapter extends DubboAdapter<Invocation, Result> {

  public String method(Invocation invocation) {
    return invocation.getMethodName();
  }

  public String url(Invocation invocation) {
    String protocal = invocation.getInvoker().getUrl().getProtocol();
    String ip = invocation.getInvoker().getUrl().getIp();
    int port = invocation.getInvoker().getUrl().getPort();
    String path = invocation.getInvoker().getUrl().getAbsolutePath();
    return protocal + "://" + ip + ":" + port + path;
  }

  public String requestHeader(Invocation invocation, String name) {
    Object result = invocation.getAttachment(name);
    return result != null ? result.toString() : null;
  }

  public Integer statusCode(Result result) {
    return result.hasException() ? 500 : 200;
  }

  public boolean parseClientAddress(Invocation invocation, Builder remoteEndpoint) {
    return false;
  }

}
