# brave-instrumentation-dubbo 

dubbo接入zipkin, 使用brave相关接口

## 集成说明

- 添加依赖

``` xml
<dependency>
  <groupId>com.jiangyoudai.commons</groupId>
  <artifactId>brave-instrumentation-dubbo</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

- 配置bean, 可以直接import XML文件

``` xml
 <import resource="classpath:/META-INF/commons-tracing.xml" />
 <import resource="classpath:/META-INF/commons-trace-dubbo.xml" />
```

- 给provider, comsumer配上filter

``` xml
 <dubbo:provider ... filter="providerFilter" />
 <dubbo:consumer ... filter="consumerFilter" />
```

- properties增加配置

``` properties
zipkin.server.url=http://host:port/api/v2/spans
zipkin.service-name=xxx-server
```