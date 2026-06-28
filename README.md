# Cloud Deploy Demo

云朵一键部署平台 Spring Boot demo。项目保留一个业务资源 `items` 的 CRUD，用 MySQL 持久化，启动时通过 Flyway 管理表结构，并内置 Swagger/OpenAPI、Actuator、Prometheus 指标和 OTEL tracing 出口。

## 技术栈

- Java 17
- Spring Boot 3.3.7
- Spring Web / Validation
- Spring Data JPA / Hibernate
- MySQL Connector/J
- Flyway
- springdoc-openapi
- Actuator / Micrometer / OpenTelemetry OTLP

## 本地启动

```bash
mvn spring-boot:run
```

默认连接：

```text
jdbc:mysql://192.168.50.18:3306/cloud_deploy_demo
username: root
password: 通过 SPRING_DATASOURCE_PASSWORD 环境变量注入
```

应用也支持通过环境变量覆盖，适合部署平台注入：

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/cloud_deploy_demo \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=secret \
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://otel-collector:4318/v1/traces \
java -jar target/cloud-deploy-demo.jar
```

## 接口

- `GET /api/items`：分页查询
- `GET /api/items/{id}`：按 ID 查询
- `POST /api/items`：创建
- `PUT /api/items/{id}`：更新
- `DELETE /api/items/{id}`：删除
- `GET /swagger-ui.html`：Swagger UI
- `GET /v3/api-docs`：OpenAPI JSON
- `GET /actuator/health`：健康检查
- `GET /actuator/prometheus`：Prometheus 指标

创建示例：

```bash
curl -X POST http://localhost:8080/api/items \
  -H 'Content-Type: application/json' \
  -d '{"name":"demo item","description":"created from curl"}'
```

## 数据库

数据库名：`cloud_deploy_demo`

表结构由 `src/main/resources/db/migration/V1__create_items.sql` 管理。JDBC URL 默认带 `createDatabaseIfNotExist=true`，应用可自动创建数据库；如果目标库里已有表但还没有 Flyway 历史，`baseline-on-migrate=true` 会让 Flyway 接管现有 schema。

我已经在 `192.168.50.18` 上创建并验证了：

```text
cloud_deploy_demo.items
```

## OTEL

应用使用 Micrometer Tracing + OpenTelemetry OTLP exporter。默认 trace endpoint：

```text
http://localhost:4318/v1/traces
```

部署到平台时建议注入：

```text
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://<otel-collector>:4318/v1/traces
DEPLOYMENT_ENVIRONMENT=dev
```

如果平台用 Java Agent 统一接入 OTEL，也可以继续通过 `JAVA_OPTS` 或 `JAVA_TOOL_OPTIONS` 注入 agent 参数；应用侧的 Actuator、Prometheus 和 resource attributes 已保留。

## 验证

```bash
mvn test
mvn -DskipTests package
java -jar target/cloud-deploy-demo.jar
```

Docker 构建：

```bash
docker build -t cloud-deploy-demo:local .
docker run --rm -p 8080:8080 cloud-deploy-demo:local
```
