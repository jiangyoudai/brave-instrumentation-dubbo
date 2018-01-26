package com.jiangyoudai.commons.trace.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboSampler;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboTracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Injector;
import zipkin2.Endpoint;

public class DubboConsumerHandler {

  final Tracer tracer;
  final DubboSampler sampler;
  final CurrentTraceContext currentTraceContext;
  final String serverName;
  final boolean serverNameSet;
  final DubboConsumerAdapter adapter;

  DubboConsumerHandler(DubboTracing dubboTracing, DubboConsumerAdapter adapter) {
    this.tracer = dubboTracing.tracing().tracer();
    this.sampler = dubboTracing.clientSampler();
    this.currentTraceContext = dubboTracing.tracing().currentTraceContext();
    this.serverName = dubboTracing.serverName();
    this.serverNameSet = !serverName.equals("");
    this.adapter = adapter;
  }

  public Span handleSend(TraceContext.Injector<RpcContext> injector, Invocation invocation) {
    return handleSend(injector, invocation, nextSpan(invocation));
  }

  public void handleReceive(Result rpcResult, Throwable error, Span span) {
    if (span.isNoop())
      return;
    Tracer.SpanInScope ws = tracer.withSpanInScope(span);
    try {
      Integer httpStatus = rpcResult != null ? adapter.statusCode(rpcResult) : null;
      if (httpStatus != null && (httpStatus < 200 || httpStatus > 299)) {
        span.tag("http.status_code", String.valueOf(httpStatus));
      }
      String message = null;
      if (error != null) {
        message = error.getMessage();
        if (message == null)
          message = error.getClass().getSimpleName();
      } else if (httpStatus != null) {
        message = httpStatus < 200 || httpStatus > 399 ? String.valueOf(httpStatus) : null;
      }
      if (message != null)
        span.tag("error", message);
    } finally {
      ws.close();
      span.finish();
    }
  }

  private Span handleSend(Injector<RpcContext> injector, Invocation invocation, Span span) {
    injector.inject(span.context(), RpcContext.getContext());

    if (span.isNoop())
      return span;

    span.kind(Span.Kind.CLIENT);
    Tracer.SpanInScope ws = tracer.withSpanInScope(span);
    try {
      String name = adapter.method(invocation);
      String url = adapter.url(invocation);
      span.name(name);
      span.tag("dubbo.url", url);
      span.tag("dubbo.method", name);
    } finally {
      ws.close();
    }
    Endpoint.Builder remoteEndpoint = Endpoint.newBuilder().serviceName(serverName);
    if (adapter.parseServerAddress(invocation, remoteEndpoint) || serverNameSet) {
      span.remoteEndpoint(remoteEndpoint.build());
    }
    return span.start();
  }

  private Span nextSpan(Invocation invocation) {
    TraceContext parent = currentTraceContext.get();
    if (parent != null)
      return tracer.newChild(parent); // inherit the sampling decision

    Boolean sampled = sampler.trySample(adapter, null);
    if (sampled == null)
      return tracer.newTrace(); // defer sampling decision to trace ID
    return tracer.newTrace(sampled ? SamplingFlags.SAMPLED : SamplingFlags.NOT_SAMPLED);
  }

}
