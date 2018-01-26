package com.jiangyoudai.commons.trace.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboSampler;
import com.jiangyoudai.commons.trace.dubbo.helper.DubboTracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import zipkin2.Endpoint;

public class DubboProviderHandler {
  final Tracer tracer;
  final DubboSampler sampler;
  final CurrentTraceContext currentTraceContext;
  final String serverName;
  final boolean serverNameSet;
  final DubboProviderAdapter adapter;

  DubboProviderHandler(DubboTracing dubboTracing, DubboProviderAdapter adapter) {
    this.tracer = dubboTracing.tracing().tracer();
    this.sampler = dubboTracing.serverSampler();
    this.currentTraceContext = dubboTracing.tracing().currentTraceContext();
    this.serverName = dubboTracing.serverName();
    this.serverNameSet = !serverName.equals("");
    this.adapter = adapter;
  }

  public Span handleReceive(TraceContext.Extractor<RpcContext> extractor, Invocation invocation) {
    Span span = nextSpan(extractor.extract(RpcContext.getContext()), invocation);
    if (span.isNoop())
      return span;

    span.kind(Span.Kind.SERVER);
    tracer.withSpanInScope(span);
    Endpoint.Builder remoteEndpoint = Endpoint.newBuilder();
    if (adapter.parseClientAddress(invocation, remoteEndpoint)) {
      span.remoteEndpoint(remoteEndpoint.build());
    }
    return span.start();
  }

  private Span nextSpan(TraceContextOrSamplingFlags extracted, Invocation invocation) {
    if (extracted.sampled() == null) { // Otherwise, try to make a new decision
      extracted = extracted.sampled(sampler.trySample(adapter, invocation));
    }
    return extracted.context() != null ? tracer.joinSpan(extracted.context())
        : tracer.nextSpan(extracted);
  }

  public void handleSend(Result rpcResult, Throwable error, Span span) {
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

}
