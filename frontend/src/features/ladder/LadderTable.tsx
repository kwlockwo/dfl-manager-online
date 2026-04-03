import { useState } from 'react';
import type { Ladder } from '../../types/api';

type SortKey = 'pts' | 'percentage' | 'pointsFor';
type SortDir = 'asc' | 'desc';

interface Props {
  entries: Ladder[];
  title: string;
  showAverages: boolean;
}

export default function LadderTable({ entries, title, showAverages }: Props) {
  const [sortKey, setSortKey] = useState<SortKey>('pts');
  const [sortDir, setSortDir] = useState<SortDir>('desc');

  const sorted = [...entries].sort((a, b) => {
    const mult = sortDir === 'desc' ? -1 : 1;
    return mult * (a[sortKey] - b[sortKey]);
  });

  const handleSort = (key: SortKey) => {
    if (key === sortKey) {
      setSortDir(d => (d === 'desc' ? 'asc' : 'desc'));
    } else {
      setSortKey(key);
      setSortDir('desc');
    }
  };

  const sortIcon = (key: SortKey) => {
    if (key !== sortKey) return ' ↕';
    return sortDir === 'desc' ? ' ↓' : ' ↑';
  };

  const thClass = (key: SortKey) =>
    `px-3 py-2 text-right cursor-pointer select-none hover:bg-gray-200 ${
      key === sortKey ? 'bg-gray-200' : ''
    }`;

  return (
    <div className="mb-6 rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200">{title}</div>
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
              <th className={thClass('pts')} onClick={() => handleSort('pts')}>
                Pts{sortIcon('pts')}
              </th>
              <th className={thClass('percentage')} onClick={() => handleSort('percentage')}>
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
