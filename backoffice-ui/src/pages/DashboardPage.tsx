import { ArrowRight, CheckCircle2 } from 'lucide-react';
import { apiConfig } from '../config/apiConfig';
import { ServiceCard } from '../components/ServiceCard';

const serviceCards = [
  {
    name: 'Order Service',
    port: 'localhost:8081',
    description: 'Creates orders, stores outbox events and publishes OrderCreatedEvent.',
    href: `${apiConfig.orderServiceUrl}/swagger-ui.html`
  },
  {
    name: 'Payment Service',
    port: 'localhost:8082',
    description: 'Consumes order events, applies idempotency and emits payment results.',
    href: `${apiConfig.paymentServiceUrl}/swagger-ui.html`
  },
  {
    name: 'Notification Service',
    port: 'localhost:8083',
    description: 'Consumes payment events and simulates customer notifications.',
    href: `${apiConfig.notificationServiceUrl}/swagger-ui.html`
  },
  {
    name: 'Audit Service',
    port: 'localhost:8084',
    description: 'Stores the complete event timeline for every correlation and order.',
    href: `${apiConfig.auditServiceUrl}/swagger-ui.html`
  },
  {
    name: 'Kafka UI',
    port: 'localhost:8090',
    description: 'Inspect topics, partitions and DLT messages.',
    href: apiConfig.kafkaUiUrl
  },
  {
    name: 'Grafana',
    port: 'localhost:3000',
    description: 'View dashboards for HTTP, outbox, business and JVM metrics.',
    href: apiConfig.grafanaUrl
  }
];

const flow = [
  'Client',
  'order-service',
  'Kafka orders.events',
  'payment-service',
  'Kafka payments.events',
  'notification-service',
  'Kafka notifications.events',
  'audit-service'
];

const demonstrates = [
  'Event-driven microservices',
  'Apache Kafka',
  'Transactional Outbox Pattern',
  'Idempotent consumers',
  'Retries and DLT',
  'Audit timeline',
  'Prometheus and Grafana observability'
];

export const DashboardPage = () => (
  <div className="space-y-8">
    <section className="rounded-lg border border-slate-200 bg-white p-8 shadow-sm">
      <p className="text-sm font-semibold uppercase tracking-[0.18em] text-slate-500">Backoffice</p>
      <h2 className="mt-3 text-3xl font-bold text-ink">EventFlow Backoffice</h2>
      <p className="mt-3 max-w-3xl text-base leading-7 text-slate-600">
        Visual dashboard for the EventFlow Kafka Lab system.
      </p>
    </section>

    <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {serviceCards.map((card) => (
        <ServiceCard key={card.name} {...card} />
      ))}
    </section>

    <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <h2 className="text-lg font-semibold text-ink">Event Flow</h2>
      <div className="mt-5 flex flex-wrap items-center gap-3">
        {flow.map((item, index) => (
          <div key={item} className="flex items-center gap-3">
            <div className="rounded-md border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-800">
              {item}
            </div>
            {index < flow.length - 1 && <ArrowRight className="h-4 w-4 text-slate-400" />}
          </div>
        ))}
      </div>
    </section>

    <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <h2 className="text-lg font-semibold text-ink">What this project demonstrates</h2>
      <div className="mt-5 grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {demonstrates.map((item) => (
          <div key={item} className="flex items-center gap-3 rounded-md bg-slate-50 px-4 py-3 text-sm text-slate-700">
            <CheckCircle2 className="h-4 w-4 text-emerald-600" />
            {item}
          </div>
        ))}
      </div>
    </section>
  </div>
);
