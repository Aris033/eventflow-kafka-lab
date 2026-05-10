interface StatusBadgeProps {
  value: string;
}

const styles: Record<string, string> = {
  CREATED: 'bg-blue-100 text-blue-700 ring-blue-200',
  PAYMENT_PENDING: 'bg-amber-100 text-amber-700 ring-amber-200',
  PAYMENT_COMPLETED: 'bg-emerald-100 text-emerald-700 ring-emerald-200',
  PAYMENT_FAILED: 'bg-red-100 text-red-700 ring-red-200',
  COMPLETED: 'bg-emerald-100 text-emerald-700 ring-emerald-200',
  FAILED: 'bg-red-100 text-red-700 ring-red-200',
  SENT: 'bg-violet-100 text-violet-700 ring-violet-200',
  ORDER_CREATED: 'bg-sky-100 text-sky-700 ring-sky-200',
  NOTIFICATION_SENT: 'bg-violet-100 text-violet-700 ring-violet-200',
  NOTIFICATION_FAILED: 'bg-orange-100 text-orange-700 ring-orange-200'
};

export const StatusBadge = ({ value }: StatusBadgeProps) => (
  <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ring-1 ${styles[value] || 'bg-slate-100 text-slate-700 ring-slate-200'}`}>
    {value}
  </span>
);
