# ZIO Quickstarts

This repository contains a collection of quickstarts for ZIO. Each quickstart is a self-contained project that demonstrates a particular aspect of ZIO. The quickstarts are designed to be easy to run and explore.

To learn about each quickstart, we have a dedicated article about it at the ZIO documentation website. You can find the list of quickstart articles [here](https://zio.dev/guides/#quickstart-guides).

## Running Quickstarts

First, open the console and clone the project using `git` (or you can simply download the project) and then to the directory of the quickstart you want to run, e.g. `zio-quickstart-restful-webservice`:

```bash
$ git clone https://github.com/zio/zio-quickstarts.git
$ cd zio-quickstarts/zio-quickstart-restful-webservice
```

Once you are inside the project directory, run the application:

```bash
$ sbt run
```

## List of Quickstarts

- [ZIO Cache](zio-quickstart-cache)
- [ZIO Json](zio-quickstart-encode-decode-json)
- [ZIO GraphQL Webservice](zio-quickstart-graphql-webservice)
- [ZIO Hello World](zio-quickstart-hello-world)
- [ZIO JUnit Tests](zio-quickstart-junit-integration)
- [ZIO Kafka](zio-quickstart-kafka)
- [ZIO Prelude](zio-quickstart-prelude)
- [ZIO Reloadable Service](zio-quickstart-reloadable-services)
- [ZIO RESTful webservice](zio-quickstart-restful-webservice)
- [ZIO RESTful webservice with configs](zio-quickstart-restful-webservice-configurable-app)
- [ZIO RESTful webservice with default logger](zio-quickstart-restful-webservice-logging)
- [ZIO RESTful webservice with custom logger](zio-quickstart-restful-webservice-custom-logger)
- [ZIO RESTful webservice with docker](zio-quickstart-restful-webservice-dockerize)
- [ZIO RESTful webservice with metrics](zio-quickstart-restful-webservice-metrics)
- [ZIO STM](zio-quickstart-stm) - many thanks to [@jorge-vasquez-2301](https://github.com/jorge-vasquez-2301) and his [article](https://scalac.io/blog/how-to-write-a-completely-lock-free-concurrent-lru-cache-with-zio-stm/) for this example
- [ZIO SQL](zio-quickstart-sql)
- [ZIO Streams](zio-quickstart-streams)
- [ZIO Schema](zio-quickstart-schema)
