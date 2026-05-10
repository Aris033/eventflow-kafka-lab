import { ExternalLink } from 'lucide-react';

interface LinkItem {
  label: string;
  href: string;
  description: string;
}

interface ExternalLinksPanelProps {
  title: string;
  links: LinkItem[];
}

export const ExternalLinksPanel = ({ title, links }: ExternalLinksPanelProps) => (
  <section className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
    <h2 className="text-lg font-semibold text-ink">{title}</h2>
    <div className="mt-5 grid gap-3 md:grid-cols-2">
      {links.map((link) => (
        <a
          key={link.href}
          href={link.href}
          target="_blank"
          rel="noreferrer"
          className="flex items-start justify-between gap-4 rounded-md border border-slate-200 p-4 transition hover:border-slate-300 hover:bg-slate-50"
        >
          <div>
            <p className="font-medium text-slate-900">{link.label}</p>
            <p className="mt-1 text-sm text-muted">{link.description}</p>
          </div>
          <ExternalLink className="mt-1 h-4 w-4 shrink-0 text-slate-400" />
        </a>
      ))}
    </div>
  </section>
);
