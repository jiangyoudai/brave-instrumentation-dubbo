package com.jiangyoudai.commons.trace.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboTracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext;

@Activate(group = {
    Constants.PROVIDER }, order = Integer.MAX_VALUE - 101)
public class ProviderTraceFilter implements Filter {
  static final Getter<RpcContext, String> GETTER = new Getter<RpcContext, String>() {

    @Override
    public String get(RpcContext carrier, String key) {
      return carrier.getAttachment(key);
    }

    @Override
    public String toString() {
      return "dubboProvider::getHeader";
    }
  };

  DubboTracing dubboTracing;
  Tracer tracer;
  TraceContext.Extractor<RpcContext> extractor;
  DubboProviderHandler handler;

  public DubboTracing getDubboTracing() {
    return dubboTracing;
  }

  public void setDubboTracing(DubboTracing dubboTracing) {
    this.tracer = dubboTracing.tracing().tracer();
    this.handler = new DubboProviderHandler(dubboTracing, new DubboProviderAdapter());
    this.extractor = dubboTracing.tracing().propagation().extractor(GETTER);
    this.dubboTracing = dubboTracing;
  }

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    if ("com.alibaba.dubbo.monitor.MonitorService".equals(invoker.getInterface().getName())) {
      return invoker.invoke(invocation);
    }

    if (this.dubboTracing == null) {
      return invoker.invoke(invocation);
    }
    Span span = handler.handleReceive(extractor, invocation);
    Throwable error = null;
    Result rpcResult = null;
    try {
      rpcResult = invoker.invoke(invocation);
      return rpcResult;
    } catch (Exception ex) {
      error = ex;
      throw ex;
    } finally {
      handler.handleSend(rpcResult, error, span);
    }
  }

}
