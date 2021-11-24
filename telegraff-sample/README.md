# Telegraff Sample

## Maven

### Prepare

install [jdk-11](https://adoptopenjdk.net/installation.html?variant=openjdk11&jvmVariant=hotspot#)

### Building

```shell
./mvnw clean package
# or for windows
./mvnw.cmd clean package
```

### Running

```shell
java -DTELEGRAM_ACCESSKEY=XXX:YYYYY -jar ./target/telegraff-sample-1.0.0.jar
```

## Docker

### Prepare

* install [jdk-11](https://adoptopenjdk.net/installation.html?variant=openjdk11&jvmVariant=hotspot#)
* install [docker](https://www.docker.com/get-started)

### Building

```shell
./mvnw clean package spring-boot:build-image

# or for windows
./mvnw.cmd clean package spring-boot:build-image
```

### Running

```shell
docker run --rm -e TELEGRAM_ACCESSKEY=XXX:YYYYY telegraff-sample:1.0.0
```
