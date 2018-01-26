package com.jiangyoudai.commons.trace.dubbo.helper;

import org.springframework.beans.factory.FactoryBean;

import brave.Tracing;

public class DubboTracingFactoryBean implements FactoryBean {

  Tracing tracing;
  DubboClientParser clientParser;
  DubboServerParser serverParser;
  DubboSampler clientSampler;
  DubboSampler serverSampler;

  @Override
  public DubboTracing getObject() throws Exception {
    DubboTracing.Builder builder = DubboTracing.newBuilder(tracing);
    if (clientParser != null)
      builder.clientParser(clientParser);
    if (serverParser != null)
      builder.serverParser(serverParser);
    if (clientSampler != null)
      builder.clientSampler(clientSampler);
    if (serverSampler != null)
      builder.serverSampler(serverSampler);
    return builder.build();
  }

  @Override
  public Class<? extends DubboTracing> getObjectType() {
    return DubboTracing.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  public void setTracing(Tracing tracing) {
    this.tracing = tracing;
  }

  public void setClientParser(DubboClientParser clientParser) {
    this.clientParser = clientParser;
  }

  public void setServerParser(DubboServerParser serverParser) {
    this.serverParser = serverParser;
  }

  public void setClientSampler(DubboSampler clientSampler) {
    this.clientSampler = clientSampler;
  }

  public void setServerSampler(DubboSampler serverSampler) {
    this.serverSampler = serverSampler;
  }
}
