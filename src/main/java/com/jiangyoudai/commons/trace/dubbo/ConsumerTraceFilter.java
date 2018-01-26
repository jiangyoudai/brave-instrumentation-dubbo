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
import brave.propagation.Propagation.Setter;
import brave.propagation.TraceContext;

@Activate(group = {
    Constants.CONSUMER }, order = Integer.MAX_VALUE - 101)
public class ConsumerTraceFilter implements Filter {
  static final Setter<RpcContext, String> SETTER = new Setter<RpcContext, String>() {
    @Override
    public void put(RpcContext carrier, String key, String value) {
      carrier.setAttachment(key, value);
    }

    @Override
    public String toString() {
      return "RpcContext::set";
    }
  };

  DubboTracing dubboTracing;
  Tracer tracer;
  TraceContext.Injector<RpcContext> injector;
  DubboConsumerHandler handler;

  public DubboTracing getDubboTracing() {
    return dubboTracing;
  }

  public void setDubboTracing(DubboTracing dubboTracing) {
    this.tracer = dubboTracing.tracing().tracer();
    this.handler = new DubboConsumerHandler(dubboTracing, new DubboConsumerAdapter());
    this.injector = dubboTracing.tracing().propagation().injector(SETTER);
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
    Span span = handler.handleSend(injector, invocation);
    Throwable error = null;
    Result rpcResult = null;
    try {
      rpcResult = invoker.invoke(invocation);
      return rpcResult;
    } catch (Exception ex) {
      error = ex;
      throw ex;
    } finally {
      handler.handleReceive(rpcResult, error, span);
    }
  }

}
