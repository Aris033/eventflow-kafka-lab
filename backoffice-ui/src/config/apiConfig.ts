const valueOrDefault = (value: string | undefined, fallback: string) => value || fallback;

export const apiConfig = {
  orderServiceUrl: valueOrDefault(import.meta.env.VITE_ORDER_SERVICE_URL, 'http://localhost:8081'),
  paymentServiceUrl: valueOrDefault(import.meta.env.VITE_PAYMENT_SERVICE_URL, 'http://localhost:8082'),
  notificationServiceUrl: valueOrDefault(import.meta.env.VITE_NOTIFICATION_SERVICE_URL, 'http://localhost:8083'),
  auditServiceUrl: valueOrDefault(import.meta.env.VITE_AUDIT_SERVICE_URL, 'http://localhost:8084'),
  kafkaUiUrl: valueOrDefault(import.meta.env.VITE_KAFKA_UI_URL, 'http://localhost:8090'),
  adminerUrl: valueOrDefault(import.meta.env.VITE_ADMINER_URL, 'http://localhost:8085'),
  prometheusUrl: valueOrDefault(import.meta.env.VITE_PROMETHEUS_URL, 'http://localhost:9090'),
  grafanaUrl: valueOrDefault(import.meta.env.VITE_GRAFANA_URL, 'http://localhost:3000')
};
