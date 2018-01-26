package com.jiangyoudai.commons.trace.dubbo.helper;

import brave.Tracing;

public abstract class DubboTracing {
  public static DubboTracing create(Tracing tracing) {
    return newBuilder(tracing).build();
  }

  public static Builder newBuilder(Tracing tracing) {
    return new DefaultDubboTracing.Builder().tracing(tracing).serverName("")
        .clientParser(new DubboClientParser()).serverParser(new DubboServerParser())
        .clientSampler(DubboSampler.TRACE_ID).serverSampler(DubboSampler.TRACE_ID);
  }

  public abstract Tracing tracing();

  public abstract DubboClientParser clientParser();

  public abstract String serverName();

  public DubboTracing clientOf(String serverName) {
    return toBuilder().serverName(serverName).build();
  }

  public abstract DubboServerParser serverParser();

  public abstract DubboSampler clientSampler();

  public abstract DubboSampler serverSampler();

  public abstract Builder toBuilder();

  public static abstract class Builder {
    public abstract Builder tracing(Tracing tracing);

    public abstract Builder clientParser(DubboClientParser clientParser);

    public abstract Builder serverParser(DubboServerParser serverParser);

    public abstract Builder clientSampler(DubboSampler clientSampler);

    public abstract Builder serverSampler(DubboSampler serverSampler);

    public abstract DubboTracing build();

    abstract Builder serverName(String serverName);

    Builder() {
    }
  }

  DubboTracing() {
  }
}