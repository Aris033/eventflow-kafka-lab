import { apiConfig } from '../config/apiConfig';
import { ExternalLinksPanel } from '../components/ExternalLinksPanel';

const links = [
  { label: 'Order Swagger', href: `${apiConfig.orderServiceUrl}/swagger-ui.html`, description: 'Order REST API documentation' },
  { label: 'Payment Swagger', href: `${apiConfig.paymentServiceUrl}/swagger-ui.html`, description: 'Payment REST API documentation' },
  { label: 'Notification Swagger', href: `${apiConfig.notificationServiceUrl}/swagger-ui.html`, description: 'Notification REST API documentation' },
  { label: 'Audit Swagger', href: `${apiConfig.auditServiceUrl}/swagger-ui.html`, description: 'Audit REST API documentation' },
  { label: 'Kafka UI', href: apiConfig.kafkaUiUrl, description: 'Inspect topics, messages and DLT' },
  { label: 'Adminer', href: apiConfig.adminerUrl, description: 'Inspect PostgreSQL schemas and tables' },
  { label: 'Prometheus', href: apiConfig.prometheusUrl, description: 'Query metrics directly' },
  { label: 'Grafana', href: apiConfig.grafanaUrl, description: 'View EventFlow dashboards' }
];

const endpointGroups = [
  { service: 'Order', endpoints: ['POST /api/orders'] },
  { service: 'Payment', endpoints: ['GET /api/payments/order/{orderId}'] },
  { service: 'Notification', endpoints: ['GET /api/notifications/order/{orderId}'] },
  { service: 'Audit', endpoints: ['GET /api/audit/events/order/{orderId}', 'GET /api/audit/events/correlation/{correlationId}'] }
];

export const ToolsPage = () => (
  <div className="space-y-8">
    <ExternalLinksPanel title="External Tools" links={links} />

    <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <h2 className="text-lg font-semibold text-ink">Useful Endpoints</h2>
      <div className="mt-5 grid gap-4 md:grid-cols-2">
        {endpointGroups.map((group) => (
          <div key={group.service} className="rounded-md border border-slate-200 bg-slate-50 p-4">
            <h3 className="font-semibold text-slate-900">{group.service}</h3>
            <div className="mt-3 space-y-2">
              {group.endpoints.map((endpoint) => (
                <code key={endpoint} className="block rounded bg-white px-3 py-2 text-sm text-slate-800 shadow-sm">
                  {endpoint}
                </code>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  </div>
);
