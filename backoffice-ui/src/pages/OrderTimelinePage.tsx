import { FormEvent, useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getAuditEventsByOrderId } from '../api/auditApi';
import { getNotificationsByOrderId } from '../api/notificationApi';
import { getPaymentByOrderId } from '../api/paymentApi';
import { StatusBadge } from '../components/StatusBadge';
import { Timeline } from '../components/Timeline';
import type { AuditEventResponse } from '../types/audit';
import type { NotificationResponse } from '../types/notification';
import type { PaymentResponse } from '../types/payment';
import { formatDateTime } from '../utils/dateUtils';
import { formatMoney } from '../utils/formatUtils';

export const OrderTimelinePage = () => {
  const [searchParams] = useSearchParams();
  const [orderId, setOrderId] = useState(searchParams.get('orderId') || '');
  const [events, setEvents] = useState<AuditEventResponse[]>([]);
  const [payment, setPayment] = useState<PaymentResponse | null>(null);
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const sortedEvents = useMemo(
    () => [...events].sort((a, b) => new Date(a.receivedAt).getTime() - new Date(b.receivedAt).getTime()),
    [events]
  );

  const search = async (event?: FormEvent) => {
    event?.preventDefault();
    if (!orderId.trim()) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const [auditEvents, paymentResult, notificationsResult] = await Promise.allSettled([
        getAuditEventsByOrderId(orderId.trim()),
        getPaymentByOrderId(orderId.trim()),
        getNotificationsByOrderId(orderId.trim())
      ]);

      setEvents(auditEvents.status === 'fulfilled' ? auditEvents.value : []);
      setPayment(paymentResult.status === 'fulfilled' ? paymentResult.value : null);
      setNotifications(notificationsResult.status === 'fulfilled' ? notificationsResult.value : []);

      if (auditEvents.status === 'rejected') {
        setError(auditEvents.reason instanceof Error ? auditEvents.reason.message : 'Unable to load audit timeline.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (searchParams.get('orderId')) {
      void search();
    }
  }, []);

  return (
    <div className="space-y-8">
      <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-ink">Order Timeline</h2>
        <form onSubmit={search} className="mt-5 flex flex-col gap-3 sm:flex-row">
          <input
            value={orderId}
            onChange={(event) => setOrderId(event.target.value)}
            className="min-w-0 flex-1 rounded-md border border-slate-300 px-3 py-2 font-mono text-sm outline-none ring-slate-900/10 focus:ring-4"
            placeholder="orderId"
          />
          <button
            type="submit"
            disabled={loading}
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:bg-slate-400"
          >
            {loading ? 'Searching...' : 'Search timeline'}
          </button>
          <button
            type="button"
            onClick={() => search()}
            disabled={loading || !orderId}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:text-slate-400"
          >
            Refresh
          </button>
        </form>
        {error && <p className="mt-4 rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</p>}
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <div className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold text-ink">Payment</h3>
          {!payment ? (
            <p className="mt-3 text-sm text-slate-600">No payment found yet.</p>
          ) : (
            <div className="mt-4 space-y-3 text-sm">
              <StatusBadge value={payment.status} />
              <p className="font-semibold">{formatMoney(payment.amount)}</p>
              <p className="text-muted">Created: {formatDateTime(payment.createdAt)}</p>
              {payment.failureReason && <p className="text-red-700">{payment.failureReason}</p>}
            </div>
          )}
        </div>

        <div className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-lg font-semibold text-ink">Notifications</h3>
          {notifications.length === 0 ? (
            <p className="mt-3 text-sm text-slate-600">No notifications found yet.</p>
          ) : (
            <div className="mt-4 space-y-3">
              {notifications.map((notification) => (
                <div key={notification.id} className="rounded-md bg-slate-50 p-4 text-sm">
                  <div className="flex items-center justify-between gap-3">
                    <p className="font-medium text-slate-900">{notification.message}</p>
                    <StatusBadge value={notification.status} />
                  </div>
                  <p className="mt-2 text-muted">{notification.recipient}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>

      {sortedEvents.length === 0 ? (
        <section className="rounded-lg border border-dashed border-slate-300 bg-white p-8 text-center text-slate-600">
          No audit events found for this order yet. The event flow may still be processing.
        </section>
      ) : (
        <Timeline events={sortedEvents} />
      )}
    </div>
  );
};
