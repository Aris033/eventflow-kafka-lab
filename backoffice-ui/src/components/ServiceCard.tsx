import { ExternalLink } from 'lucide-react';

interface ServiceCardProps {
  name: string;
  port: string;
  description: string;
  href: string;
}

export const ServiceCard = ({ name, port, description, href }: ServiceCardProps) => (
  <a
    href={href}
    target="_blank"
    rel="noreferrer"
    className="group flex h-full flex-col justify-between rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-soft"
  >
    <div>
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-base font-semibold text-ink">{name}</h3>
          <p className="mt-1 text-sm text-muted">{port}</p>
        </div>
        <ExternalLink className="h-4 w-4 text-slate-400 transition group-hover:text-slate-700" />
      </div>
      <p className="mt-4 text-sm leading-6 text-slate-600">{description}</p>
    </div>
  </a>
);
