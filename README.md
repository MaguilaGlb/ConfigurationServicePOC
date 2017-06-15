# BaselineVertx

Initial Baseline for Vert.x Project to FOX Lat
___

## Crear Fat Jar
```
mvn package
```

Se genera un archivo de la forma afa-<version>-fat.jar, en el directorio target del proyecto.

*Nota:* <version>: versión actual del proyecto, para este primer baseline es 0.0.1-SNAPSHOT
Nota 2: Como al empaquetar el proyecto se genera un FAT JAR, todas los jar de los que depende nuestro proyecto serán incluidos en el paquete a generar.

___
## Correr el proyecto
```
java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -Dlog4j.configurationFile=file:/home/h.gonzalez/Dev/foxafa/log4j/log4j2.xml -jar afa-0.0.1-SNAPSHOT-fat.jar

```

El proyecto se construyo de tal forma que el logging usado es SLF4J implementando por configuración el LOG4J2, con el fin de usar los appenders asincronos de esta nueva versión de la librería
Para que el proyecto se ejecute corractamente se debe tener el archivo de configuración del log4j2, a continuación cargamos un ejemplo 

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

*Nota:* En el comando de ejecución modificar la propiedad del sistema -Dlog4j.configurationFile=file:/home/h.gonzalez/Dev/foxafa/log4j/log4j2.xml  por la propia en el ambiente donde se vaya a ejecutar el proyecto

*Nota 2:* Este configuración básica del log4j2 envía los log del sistema a 2 diferentes partes a la consola y a un archivo, es necesario que en el tag RollingFile se modifique el atributo filePattern por el propio del ambiente donde se vaya a correr la aplicación

