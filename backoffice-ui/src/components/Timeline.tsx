import type { AuditEventResponse } from '../types/audit';
import { formatDateTime } from '../utils/dateUtils';
import { JsonBlock } from './JsonBlock';
import { StatusBadge } from './StatusBadge';

interface TimelineProps {
  events: AuditEventResponse[];
}

export const Timeline = ({ events }: TimelineProps) => (
  <div className="space-y-4">
    {events.map((event) => (
      <article key={event.id} className="relative rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <StatusBadge value={event.eventType} />
            <h3 className="mt-3 text-lg font-semibold text-ink">{event.sourceTopic}</h3>
          </div>
          <div className="text-right text-sm text-muted">
            <p>Occurred: {formatDateTime(event.occurredAt)}</p>
            <p>Received: {formatDateTime(event.receivedAt)}</p>
          </div>
        </div>

        <dl className="mt-5 grid gap-3 text-sm md:grid-cols-2">
          <div>
            <dt className="text-muted">eventId</dt>
            <dd className="break-all font-mono text-slate-900">{event.eventId}</dd>
          </div>
          <div>
            <dt className="text-muted">correlationId</dt>
            <dd className="break-all font-mono text-slate-900">{event.correlationId}</dd>
          </div>
          <div>
            <dt className="text-muted">orderId</dt>
            <dd className="break-all font-mono text-slate-900">{event.orderId || 'n/a'}</dd>
          </div>
          <div>
            <dt className="text-muted">messageKey</dt>
            <dd className="break-all font-mono text-slate-900">{event.messageKey || 'n/a'}</dd>
          </div>
        </dl>

        <div className="mt-5">
          <JsonBlock payload={event.payload} />
        </div>
      </article>
    ))}
  </div>
);
