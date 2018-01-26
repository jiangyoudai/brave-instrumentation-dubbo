package com.jiangyoudai.commons.trace.dubbo.helper;

import brave.Tracing;

final public class DefaultDubboTracing extends DubboTracing {

  private final Tracing tracing;
  private final DubboClientParser clientParser;
  private final String serverName;
  private final DubboServerParser serverParser;
  private final DubboSampler clientSampler;
  private final DubboSampler serverSampler;

  private DefaultDubboTracing(Tracing tracing, DubboClientParser clientParser, String serverName,
      DubboServerParser serverParser, DubboSampler clientSampler, DubboSampler serverSampler) {
    this.tracing = tracing;
    this.clientParser = clientParser;
    this.serverName = serverName;
    this.serverParser = serverParser;
    this.clientSampler = clientSampler;
    this.serverSampler = serverSampler;
  }

  @Override
  public Tracing tracing() {
    return tracing;
  }

  @Override
  public DubboClientParser clientParser() {
    return clientParser;
  }

  @Override
  public String serverName() {
    return serverName;
  }

  @Override
  public DubboServerParser serverParser() {
    return serverParser;
  }

  @Override
  public DubboSampler clientSampler() {
    return clientSampler;
  }

  @Override
  public DubboSampler serverSampler() {
    return serverSampler;
  }

  @Override
  public String toString() {
    return "DubboTracing{" + "tracing=" + tracing + ", " + "clientParser=" + clientParser + ", "
        + "serverName=" + serverName + ", " + "serverParser=" + serverParser + ", "
        + "clientSampler=" + clientSampler + ", " + "serverSampler=" + serverSampler + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DubboTracing) {
      DubboTracing that = (DubboTracing) o;
      return (this.tracing.equals(that.tracing()))
          && (this.clientParser.equals(that.clientParser()))
          && (this.serverName.equals(that.serverName()))
          && (this.serverParser.equals(that.serverParser()))
          && (this.clientSampler.equals(that.clientSampler()))
          && (this.serverSampler.equals(that.serverSampler()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.tracing.hashCode();
    h *= 1000003;
    h ^= this.clientParser.hashCode();
    h *= 1000003;
    h ^= this.serverName.hashCode();
    h *= 1000003;
    h ^= this.serverParser.hashCode();
    h *= 1000003;
    h ^= this.clientSampler.hashCode();
    h *= 1000003;
    h ^= this.serverSampler.hashCode();
    return h;
  }

  @Override
  public DubboTracing.Builder toBuilder() {
    return new Builder(this);
  }

  static final class Builder extends DubboTracing.Builder {
    private Tracing tracing;
    private DubboClientParser clientParser;
    private String serverName;
    private DubboServerParser serverParser;
    private DubboSampler clientSampler;
    private DubboSampler serverSampler;

    Builder() {
    }

    private Builder(DubboTracing source) {
      this.tracing = source.tracing();
      this.clientParser = source.clientParser();
      this.serverName = source.serverName();
      this.serverParser = source.serverParser();
      this.clientSampler = source.clientSampler();
      this.serverSampler = source.serverSampler();
    }

    @Override
    public DubboTracing.Builder tracing(Tracing tracing) {
      if (tracing == null) {
        throw new NullPointerException("Null tracing");
      }
      this.tracing = tracing;
      return this;
    }

    @Override
    public DubboTracing.Builder clientParser(DubboClientParser clientParser) {
      if (clientParser == null) {
        throw new NullPointerException("Null clientParser");
      }
      this.clientParser = clientParser;
      return this;
    }

    @Override
    DubboTracing.Builder serverName(String serverName) {
      if (serverName == null) {
        throw new NullPointerException("Null serverName");
      }
      this.serverName = serverName;
      return this;
    }

    @Override
    public DubboTracing.Builder serverParser(DubboServerParser serverParser) {
      if (serverParser == null) {
        throw new NullPointerException("Null serverParser");
      }
      this.serverParser = serverParser;
      return this;
    }

    @Override
    public DubboTracing.Builder clientSampler(DubboSampler clientSampler) {
      if (clientSampler == null) {
        throw new NullPointerException("Null clientSampler");
      }
      this.clientSampler = clientSampler;
      return this;
    }

    @Override
    public DubboTracing.Builder serverSampler(DubboSampler serverSampler) {
      if (serverSampler == null) {
        throw new NullPointerException("Null serverSampler");
      }
      this.serverSampler = serverSampler;
      return this;
    }

    @Override
    public DubboTracing build() {
      String missing = "";
      if (this.tracing == null) {
        missing += " tracing";
      }
      if (this.clientParser == null) {
        missing += " clientParser";
      }
      if (this.serverName == null) {
        missing += " serverName";
      }
      if (this.serverParser == null) {
        missing += " serverParser";
      }
      if (this.clientSampler == null) {
        missing += " clientSampler";
      }
      if (this.serverSampler == null) {
        missing += " serverSampler";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new DefaultDubboTracing(this.tracing, this.clientParser, this.serverName,
          this.serverParser, this.clientSampler, this.serverSampler);
    }
  }

}
