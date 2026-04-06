import { useState } from 'react';
import type { Ladder } from '../../types/api';

type SortKey = 'wins' | 'losses' | 'draws' | 'pointsFor' | 'averageFor' | 'pointsAgainst' | 'averageAgainst' | 'pts' | 'percentage';
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
  const [userSorted, setUserSorted] = useState(false);
  const [showAllCols, setShowAllCols] = useState(false);

  const handleSort = (key: SortKey, shiftKey: boolean) => {
    setUserSorted(true);
    setSort(prev => {
      const existingIndex = prev.findIndex(s => s.key === key);

      if (shiftKey) {
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
        if (existingIndex === 0 && prev.length === 1) {
          return [{ key, dir: prev[0].dir === 'desc' ? 'asc' : 'desc' }];
        }
        return [{ key, dir: 'desc' }];
      }
    });
  };

  const sortIcon = (key: SortKey) => {
    const index = sort.findIndex(s => s.key === key);
    if (index === -1) return null;
    const arrow = sort[index].dir === 'desc' ? '↓' : '↑';
    const badge = userSorted && sort.length > 1
      ? <sup className="text-gray-400 text-[9px] ml-0.5">{index + 1}</sup>
      : null;
    return <span className="ml-0.5">{arrow}{badge}</span>;
  };

  const th = (key: SortKey, label: string, extraClass = '') => {
    const active = sort.some(s => s.key === key);
    return (
      <th
        className={`px-3 py-2 text-right cursor-pointer select-none hover:bg-gray-200 whitespace-nowrap ${active ? 'bg-gray-200' : ''} ${extraClass}`}
        onClick={e => handleSort(key, e.shiftKey)}
      >
        {label}{sortIcon(key)}
      </th>
    );
  };

  const sorted = applySorts(entries, sort);

  return (
    <div className="mb-6 rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200 flex items-center justify-between">
        <span>{title}</span>
        <div className="flex items-center gap-3">
          {userSorted && (
            <button
              onClick={() => { setSort(DEFAULT_SORT); setUserSorted(false); }}
              className="text-xs font-normal text-blue-600 hover:text-blue-800 hover:underline"
            >
              Reset sort
            </button>
          )}
          <button
            onClick={() => setShowAllCols(s => !s)}
            className="text-xs font-normal text-blue-600 hover:text-blue-800 hover:underline md:hidden"
          >
            {showAllCols ? 'Less' : 'More'}
          </button>
        </div>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
            <tr>
              <th className="px-3 py-2 text-left">Team</th>
              {th('wins', 'W', showAllCols ? '' : 'hidden md:table-cell')}
              {th('losses', 'L', showAllCols ? '' : 'hidden md:table-cell')}
              {th('draws', 'D', showAllCols ? '' : 'hidden md:table-cell')}
              {th('pointsFor', 'For', showAllCols ? '' : 'hidden md:table-cell')}
              {showAverages && th('averageFor', 'Ave For', showAllCols ? '' : 'hidden md:table-cell')}
              {showAverages && th('pointsAgainst', 'Agst', showAllCols ? '' : 'hidden md:table-cell')}
              {showAverages && th('averageAgainst', 'Ave Agst', showAllCols ? '' : 'hidden md:table-cell')}
              {th('pts', 'Pts')}
              {th('percentage', '%')}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {sorted.map((entry, i) => (
              <tr key={entry.teamCode} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                <td className="px-3 py-2">{entry.displayName}</td>
                <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.wins}</td>
                <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.losses}</td>
                <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.draws}</td>
                <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.pointsFor}</td>
                {showAverages && <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.averageFor.toFixed(2)}</td>}
                {showAverages && <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.pointsAgainst}</td>}
                {showAverages && <td className={`px-3 py-2 text-right ${showAllCols ? '' : 'hidden md:table-cell'}`}>{entry.averageAgainst.toFixed(2)}</td>}
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
