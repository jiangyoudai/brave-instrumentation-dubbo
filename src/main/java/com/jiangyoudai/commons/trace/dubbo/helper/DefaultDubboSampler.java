package com.jiangyoudai.commons.trace.dubbo.helper;

import com.alibaba.dubbo.rpc.Invocation;

public class DefaultDubboSampler extends DubboSampler {

  @Override
  public Boolean trySample(DubboAdapter adapter, Invocation invocation) {
    return false;
  }

}
