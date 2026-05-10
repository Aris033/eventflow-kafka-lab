import { FormEvent, useState } from 'react';
import { Link } from 'react-router-dom';
import { createOrder } from '../api/orderApi';
import type { OrderResponse } from '../types/order';
import { formatDateTime } from '../utils/dateUtils';
import { formatMoney } from '../utils/formatUtils';
import { StatusBadge } from '../components/StatusBadge';

const presets = [
  { label: 'Successful order', customerId: 'customer-1', totalAmount: 99.99 },
  { label: 'Failed payment order', customerId: 'customer-2', totalAmount: 1500 },
  { label: 'DLT test order', customerId: 'fail-payment-processing-1', totalAmount: 99.99 }
];

export const CreateOrderPage = () => {
  const [customerId, setCustomerId] = useState('customer-1');
  const [totalAmount, setTotalAmount] = useState('99.99');
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (event?: FormEvent) => {
    event?.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const created = await createOrder({
        customerId,
        totalAmount: Number(totalAmount)
      });
      setOrder(created);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to create order.');
    } finally {
      setLoading(false);
    }
  };

  const applyPreset = (preset: (typeof presets)[number]) => {
    setCustomerId(preset.customerId);
    setTotalAmount(String(preset.totalAmount));
  };

  return (
    <div className="grid gap-8 lg:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
      <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-ink">Create Order</h2>
        <p className="mt-2 text-sm leading-6 text-slate-600">
          Create an order through order-service and then inspect how Kafka moves the event through the system.
        </p>

        <div className="mt-5 flex flex-wrap gap-2">
          {presets.map((preset) => (
            <button
              key={preset.label}
              type="button"
              onClick={() => applyPreset(preset)}
              className="rounded-md border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            >
              {preset.label}
            </button>
          ))}
        </div>

        <form onSubmit={submit} className="mt-6 space-y-5">
          <label className="block">
            <span className="text-sm font-medium text-slate-700">customerId</span>
            <input
              value={customerId}
              onChange={(event) => setCustomerId(event.target.value)}
              className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 outline-none ring-slate-900/10 focus:ring-4"
              placeholder="customer-1"
              required
            />
          </label>

          <label className="block">
            <span className="text-sm font-medium text-slate-700">totalAmount</span>
            <input
              value={totalAmount}
              onChange={(event) => setTotalAmount(event.target.value)}
              className="mt-2 w-full rounded-md border border-slate-300 px-3 py-2 outline-none ring-slate-900/10 focus:ring-4"
              type="number"
              min="0.01"
              step="0.01"
              required
            />
          </label>

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-md bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
          >
            {loading ? 'Creating...' : 'Create Order'}
          </button>
        </form>

        {error && (
          <div className="mt-5 rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}
      </section>

      <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-ink">Latest Order</h2>
        {!order ? (
          <p className="mt-4 text-sm text-slate-600">Create an order to see the response here.</p>
        ) : (
          <div className="mt-5 space-y-5">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="break-all font-mono text-sm text-slate-900">{order.id}</p>
                <p className="mt-2 text-sm text-muted">{order.customerId}</p>
              </div>
              <StatusBadge value={order.status} />
            </div>

            <dl className="grid gap-4 text-sm sm:grid-cols-2">
              <div className="rounded-md bg-slate-50 p-4">
                <dt className="text-muted">Amount</dt>
                <dd className="mt-1 font-semibold text-slate-900">{formatMoney(order.totalAmount)}</dd>
              </div>
              <div className="rounded-md bg-slate-50 p-4">
                <dt className="text-muted">Created</dt>
                <dd className="mt-1 font-semibold text-slate-900">{formatDateTime(order.createdAt)}</dd>
              </div>
            </dl>

            <Link
              to={`/timeline?orderId=${order.id}`}
              className="inline-flex rounded-md bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800"
            >
              View timeline
            </Link>
          </div>
        )}
      </section>
    </div>
  );
};
