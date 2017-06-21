# BaselineVertx

Template of a Microservice for use as a Baseline, of the other Microservices of the project


---
# Build a Fat Jar

launch the script
```
mvn package
```

that build a file <artifact>-<version>-fat.jar, in the "target" directory of the project.


---
# Launch the app

```
java -jar proxycrm-0.0.1-SNAPSHOT-fat.jar

```

## App Customize Parameters

The order of take the parameters are:
1. Enviorment Vars
2. JVM System Property (-D)
3. Default Value

### vertx.logger-delegate-factory-class-name
Vert.x Logging Delegate, it's determining the Logging system that uses vert.x app, the possible values are: [http://vertx.io/docs/apidocs/io/vertx/core/logging/package-summary.html](http://vertx.io/docs/apidocs/io/vertx/core/logging/package-summary.html)

Default: io.vertx.core.logging.SLF4JLogDelegateFactory

### log4j.configurationFile
For default the app uses log4j2 as logging system, this parameter indicates the log4j2.xml config file that the system must use, relative or absolute path.

Default: it takes the log4j2.xml of the classpath

### hazelcast.cluster-config-file
This parameter indicates the cluster.xml config file that the system must use, relative or absolute path.
Referent Documentation [http://docs.hazelcast.org/docs/2.0/manual/html/ch13.html](http://docs.hazelcast.org/docs/2.0/manual/html/ch13.html)

Default: it takes the cluster.xml of the root of the classpath

### hazelcast.logging.type
This parameter indicates the logging system that uses hazelcast, the recomendation is to use the same system that the vert.x config (vertx.logger-delegate-factory-class-name)

Default: slf4j

## Log4j
The project uses a Facade pattern to logging, it's using SLF4J implements by LOG4J2 that uses async methods to do the work.
The default on jar config file used, is: 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
  <Appenders>
    <Console name="CONSOLE" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
    <RollingFile name="VERTXLOGS" append="true" fileName="/some/directory/of/the/local/machine/vertx.log" filePattern="/some/directory/of/the/local/machine/$${date:yyyy-MM}/vertx-%d{MM-dd-yyyy}-%i.log.gz">
      <PatternLayout pattern="%d{ISO8601} %-5p %c:%L - %m%n" />
      <Policies>
        <TimeBasedTriggeringPolicy />
        <SizeBasedTriggeringPolicy size="250 MB"/>
      </Policies>
      <DefaultRolloverStrategy max="20"/>
    </RollingFile>
    <Async name="ASYNC">
      <AppenderRef ref="CONSOLE"/>
      <AppenderRef ref="VERTXLOGS"/>
    </Async>
  </Appenders>
  <Loggers>
    <Root level="ALL">
      <AppenderRef ref="ASYNC"/>
    </Root>
  </Loggers>
</Configuration>

```
