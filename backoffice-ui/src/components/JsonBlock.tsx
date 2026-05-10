import { prettyJson } from '../utils/formatUtils';

interface JsonBlockProps {
  payload: string;
}

export const JsonBlock = ({ payload }: JsonBlockProps) => (
  <details className="group rounded-md border border-slate-200 bg-slate-950">
    <summary className="cursor-pointer px-4 py-3 text-sm font-medium text-slate-100">
      Payload JSON
    </summary>
    <pre className="max-h-80 overflow-auto border-t border-slate-800 p-4 text-xs leading-relaxed text-slate-100">
      {prettyJson(payload)}
    </pre>
  </details>
);
