import { Activity, GitBranch, PlusCircle, Wrench } from 'lucide-react';
import { NavLink } from 'react-router-dom';

const links = [
  { to: '/', label: 'Dashboard', icon: Activity },
  { to: '/orders', label: 'Create Order', icon: PlusCircle },
  { to: '/timeline', label: 'Timeline', icon: GitBranch },
  { to: '/tools', label: 'Tools', icon: Wrench }
];

export const Header = () => (
  <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/90 backdrop-blur">
    <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8">
      <div>
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">EventFlow Kafka Lab</p>
        <h1 className="mt-1 text-xl font-bold text-ink">Backoffice UI</h1>
      </div>
      <nav className="flex flex-wrap gap-2">
        {links.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition ${
                isActive ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
              }`
            }
          >
            <Icon className="h-4 w-4" />
            {label}
          </NavLink>
        ))}
      </nav>
    </div>
  </header>
);
