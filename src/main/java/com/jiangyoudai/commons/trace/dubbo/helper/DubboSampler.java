package com.jiangyoudai.commons.trace.dubbo.helper;

import com.alibaba.dubbo.rpc.Invocation;

import brave.internal.Nullable;

public abstract class DubboSampler {

  public static final DubboSampler TRACE_ID = new DubboSampler() {
    @Override
    @Nullable
    public Boolean trySample(DubboAdapter adapter, Invocation invocation) {
      return null;
    }

    @Override
    public String toString() {
      return "DeferDecision";
    }
  };

  public abstract Boolean trySample(DubboAdapter adapter, Invocation invocation);

}
