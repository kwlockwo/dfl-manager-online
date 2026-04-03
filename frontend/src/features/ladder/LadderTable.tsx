import { useState } from 'react';
import type { Ladder } from '../../types/api';

type SortKey = 'pts' | 'percentage' | 'pointsFor';
type SortDir = 'asc' | 'desc';

interface SortCol {
  key: SortKey;
  dir: SortDir;
}

const DEFAULT_SORT: SortCol[] = [
  { key: 'pts', dir: 'desc' },
  { key: 'percentage', dir: 'desc' },
  { key: 'pointsFor', dir: 'desc' },
];

function isDefault(sort: SortCol[]): boolean {
  return (
    sort.length === DEFAULT_SORT.length &&
    sort.every((s, i) => s.key === DEFAULT_SORT[i].key && s.dir === DEFAULT_SORT[i].dir)
  );
}

function applySorts(entries: Ladder[], sort: SortCol[]): Ladder[] {
  return [...entries].sort((a, b) => {
    for (const { key, dir } of sort) {
      const diff = a[key] - b[key];
      if (diff !== 0) return dir === 'desc' ? -diff : diff;
    }
    return 0;
  });
}

interface Props {
  entries: Ladder[];
  title: string;
  showAverages: boolean;
}

export default function LadderTable({ entries, title, showAverages }: Props) {
  const [sort, setSort] = useState<SortCol[]>(DEFAULT_SORT);

  const handleSort = (key: SortKey, shiftKey: boolean) => {
    setSort(prev => {
      const existingIndex = prev.findIndex(s => s.key === key);

      if (shiftKey) {
        // Add/toggle as secondary sort
        if (existingIndex !== -1) {
          const updated = [...prev];
          updated[existingIndex] = {
            key,
            dir: updated[existingIndex].dir === 'desc' ? 'asc' : 'desc',
          };
          return updated;
        }
        return [...prev, { key, dir: 'desc' }];
      } else {
        // Single column sort — toggle if already primary, otherwise replace
        if (existingIndex === 0 && prev.length === 1) {
          return [{ key, dir: prev[0].dir === 'desc' ? 'asc' : 'desc' }];
        }
        return [{ key, dir: 'desc' }];
      }
    });
  };

  const sortIcon = (key: SortKey) => {
    const index = sort.findIndex(s => s.key === key);
    if (index === -1) return <span className="text-gray-300 ml-0.5">↕</span>;
    const arrow = sort[index].dir === 'desc' ? '↓' : '↑';
    const badge = sort.length > 1
      ? <sup className="text-gray-400 text-[9px] ml-0.5">{index + 1}</sup>
      : null;
    return <span className="ml-0.5">{arrow}{badge}</span>;
  };

  const thClass = (key: SortKey) => {
    const active = sort.some(s => s.key === key);
    return `px-3 py-2 text-right cursor-pointer select-none hover:bg-gray-200 whitespace-nowrap ${
      active ? 'bg-gray-200' : ''
    }`;
  };

  const sorted = applySorts(entries, sort);
  const showReset = !isDefault(sort);

  return (
    <div className="mb-6 rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200 flex items-center justify-between">
        <span>{title}</span>
        {showReset && (
          <button
            onClick={() => setSort(DEFAULT_SORT)}
            className="text-xs font-normal text-blue-600 hover:text-blue-800 hover:underline"
          >
            Reset sort
          </button>
        )}
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
            <tr>
              <th className="px-3 py-2 text-left">Team</th>
              <th className="px-3 py-2 text-right">W</th>
              <th className="px-3 py-2 text-right">L</th>
              <th className="px-3 py-2 text-right">D</th>
              <th className="px-3 py-2 text-right">For</th>
              {showAverages && <th className="px-3 py-2 text-right whitespace-nowrap">Ave For</th>}
              {showAverages && <th className="px-3 py-2 text-right">Agst</th>}
              {showAverages && <th className="px-3 py-2 text-right whitespace-nowrap">Ave Agst</th>}
              <th className={thClass('pts')} onClick={e => handleSort('pts', e.shiftKey)}>
                Pts{sortIcon('pts')}
              </th>
              <th className={thClass('percentage')} onClick={e => handleSort('percentage', e.shiftKey)}>
                %{sortIcon('percentage')}
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {sorted.map((entry, i) => (
              <tr key={entry.teamCode} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                <td className="px-3 py-2">{entry.displayName}</td>
                <td className="px-3 py-2 text-right">{entry.wins}</td>
                <td className="px-3 py-2 text-right">{entry.losses}</td>
                <td className="px-3 py-2 text-right">{entry.draws}</td>
                <td className="px-3 py-2 text-right">{entry.pointsFor}</td>
                {showAverages && <td className="px-3 py-2 text-right">{entry.averageFor.toFixed(2)}</td>}
                {showAverages && <td className="px-3 py-2 text-right">{entry.pointsAgainst}</td>}
                {showAverages && <td className="px-3 py-2 text-right">{entry.averageAgainst.toFixed(2)}</td>}
                <td className="px-3 py-2 text-right font-medium">{entry.pts}</td>
                <td className="px-3 py-2 text-right">{entry.percentage.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
