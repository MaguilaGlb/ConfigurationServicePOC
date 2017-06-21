# Proxy CRM

Microriservice that act as proxy between users request and FOX service providers (Evergent, MPX).

The methods that proxy are:

* hasAccess (Evergent): Returns if a user (userToken) has permission to view an event (urn)
* getToken (MPX): Return a token to allow view an event

This proxy implements a circuit baker pattern that allow fail tolerance

---
# Endpoints

---
# Circuit Breaker Behaviour

---
# Build a Fat Jar

launch the script
```
mvn package
```

that build a file proxycrm-<version>-fat.jar, in the "target" directory of the project.


---
# Launch the app

```
java -jar proxycrm-0.0.1-SNAPSHOT-fat.jar

```

## App 

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
    <RollingFile name="VERTXLOGS" append="true" fileName="/home/h.gonzalez/Dev/foxafa/logs/vertx.log" filePattern="/home/h.gonzalez/Dev/foxafa/logs/$${date:yyyy-MM}/vertx-%d{MM-dd-yyyy}-%i.log.gz">
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

*Note:* Into launch script modifies the system property -Dlog4j.configurationFile=file:/home/h.gonzalez/Dev/foxafa/log4j/log4j2.xml  instead of the appropiate to the project

