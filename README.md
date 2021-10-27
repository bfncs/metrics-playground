# Metrics Playground

📈 Play around with Micrometer, Telegraf, InfluxDB 1 &  Grafana

The Java app provides data in Micrometer format at `GET /metrics`. This is collected by Telegraf and written to InfluxDB. Grafana visualizes the data in InfluxDB.

## Usage

```
docker-compose up
```

* [Grafana](http://localhost:3000/) (user: `admin`, password `admin`, look for dashboard `app`)
* [Java App Metrics Endpoint](http://localhost:8080/metrics)
* [Telegraf Metrics Endpoint](http://localhost:9273/metrics)
* Access InfluxDB directly:
  `docker-compose exec influxdb influx -username admin -password secret -database app_metrics`