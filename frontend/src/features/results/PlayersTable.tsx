import { useState } from 'react';
import type { SelectedPlayer, TeamResults } from '../../types/api';

const STATUS_CLASS: Record<string, string> = {
  InProgress: 'bg-yellow-50',
  Completed: 'bg-blue-100',
  Finalized: 'bg-green-100',
};

type SortKey = 'teamPlayerId' | 'name' | 'position' | 'kicks' | 'handballs' | 'disposals' | 'marks' | 'hitouts' | 'freesFor' | 'freesAgainst' | 'tackles' | 'goals' | 'behinds' | 'score' | 'predictedScore' | 'trend';
type SortDir = 'asc' | 'desc';

interface SortCol {
  key: SortKey;
  dir: SortDir;
}

const DEFAULT_SORT: SortCol[] = [
  { key: 'position', dir: 'asc' },
  { key: 'score', dir: 'desc' },
  { key: 'trend', dir: 'desc' },
];

function getValue(player: SelectedPlayer, key: SortKey): string | number {
  switch (key) {
    case 'teamPlayerId': return player.teamPlayerId;
    case 'name': return playerName(player);
    case 'position': return player.position;
    case 'kicks': return player.stats.kicks;
    case 'handballs': return player.stats.handballs;
    case 'disposals': return player.stats.disposals;
    case 'marks': return player.stats.marks;
    case 'hitouts': return player.stats.hitouts;
    case 'freesFor': return player.stats.freesFor;
    case 'freesAgainst': return player.stats.freesAgainst;
    case 'tackles': return player.stats.tackles;
    case 'goals': return player.stats.goals;
    case 'behinds': return player.stats.behinds;
    case 'score': return player.stats.score;
    case 'predictedScore': return player.stats.predictedScore;
    case 'trend': return player.stats.trend;
  }
}

function applySorts(players: SelectedPlayer[], sort: SortCol[]): SelectedPlayer[] {
  return [...players].sort((a, b) => {
    for (const { key, dir } of sort) {
      const av = getValue(a, key);
      const bv = getValue(b, key);
      const diff = typeof av === 'string' ? av.localeCompare(bv as string) : av - (bv as number);
      if (diff !== 0) return dir === 'desc' ? -diff : diff;
    }
    return 0;
  });
}

function playerName(player: SelectedPlayer): string {
  const suffix = player.replacementInd === '*' || player.replacementInd === '**'
    ? player.replacementInd
    : '';
  return player.name + suffix;
}

function rowClass(scrapingStatus: string | null): string {
  if (!scrapingStatus) return '';
  return STATUS_CLASS[scrapingStatus] ?? '';
}

interface Props {
  readonly players: SelectedPlayer[];
  readonly team: TeamResults;
}

const HEADERS: { label: string; key: SortKey; left?: boolean }[] = [
  { label: 'No.',       key: 'teamPlayerId',  left: true },
  { label: 'Player',    key: 'name',          left: true },
  { label: 'Pos',       key: 'position' },
  { label: 'K',         key: 'kicks' },
  { label: 'H',         key: 'handballs' },
  { label: 'D',         key: 'disposals' },
  { label: 'M',         key: 'marks' },
  { label: 'HO',        key: 'hitouts' },
  { label: 'FF',        key: 'freesFor' },
  { label: 'FA',        key: 'freesAgainst' },
  { label: 'T',         key: 'tackles' },
  { label: 'G',         key: 'goals' },
  { label: 'B',         key: 'behinds' },
  { label: 'Score',     key: 'score' },
  { label: 'Predicted', key: 'predictedScore' },
  { label: 'Trend',     key: 'trend' },
];

export default function PlayersTable({ players, team }: Props) {
  const [sort, setSort] = useState<SortCol[]>(DEFAULT_SORT);
  const [userSorted, setUserSorted] = useState(false);

  const handleSort = (key: SortKey, shiftKey: boolean) => {
    setUserSorted(true);
    setSort(prev => {
      const existingIndex = prev.findIndex(s => s.key === key);
      if (shiftKey) {
        if (existingIndex !== -1) {
          const updated = [...prev];
          updated[existingIndex] = { key, dir: updated[existingIndex].dir === 'desc' ? 'asc' : 'desc' };
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

  const sorted = applySorts(players, sort);
  const showTwoFooterRows = team.currentPredictedScore !== team.score;

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-xs border border-gray-200">
        <thead className="bg-gray-50 text-gray-600">
          <tr>
            <th colSpan={17} className="px-2 py-1 text-left border-b border-gray-200 font-semibold flex items-center justify-between">
              <span>Team</span>
              {userSorted && (
                <button
                  onClick={() => { setSort(DEFAULT_SORT); setUserSorted(false); }}
                  className="text-xs font-normal text-blue-600 hover:text-blue-800 hover:underline"
                >
                  Reset sort
                </button>
              )}
            </th>
          </tr>
          <tr className="border-b border-gray-200">
            {HEADERS.map(({ label, key, left }) => {
              const active = sort.some(s => s.key === key);
              return (
                <th
                  key={label}
                  className={`px-2 py-1 whitespace-nowrap cursor-pointer select-none hover:bg-gray-200 ${active ? 'bg-gray-200' : ''} ${left ? 'text-left' : 'text-right'}`}
                  onClick={e => handleSort(key, e.shiftKey)}
                >
                  {label}{sortIcon(key)}
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {sorted.map(player => (
            <tr key={player.teamPlayerId} className={rowClass(player.stats.scrapingStatus)}>
              <td className="px-2 py-1 text-right">{player.teamPlayerId}</td>
              <td className="px-2 py-1 whitespace-nowrap">{playerName(player)}</td>
              <td className="px-2 py-1 text-right">{player.position}</td>
              <td className="px-2 py-1 text-right">{player.stats.kicks}</td>
              <td className="px-2 py-1 text-right">{player.stats.handballs}</td>
              <td className="px-2 py-1 text-right">{player.stats.disposals}</td>
              <td className="px-2 py-1 text-right">{player.stats.marks}</td>
              <td className="px-2 py-1 text-right">{player.stats.hitouts}</td>
              <td className="px-2 py-1 text-right">{player.stats.freesFor}</td>
              <td className="px-2 py-1 text-right">{player.stats.freesAgainst}</td>
              <td className="px-2 py-1 text-right">{player.stats.tackles}</td>
              <td className="px-2 py-1 text-right">{player.stats.goals}</td>
              <td className="px-2 py-1 text-right">{player.stats.behinds}</td>
              <td className="px-2 py-1 text-right font-medium">{player.stats.score}</td>
              <td className="px-2 py-1 text-right">{player.stats.predictedScore}</td>
              <td className="px-2 py-1 text-right">{player.stats.trend}</td>
            </tr>
          ))}
        </tbody>
        <tfoot className="border-t border-gray-300 bg-gray-50 font-semibold text-xs">
          <tr>
            <td colSpan={13} className="px-2 py-1 text-right">Total</td>
            <td className="px-2 py-1 text-right">{team.score}</td>
            <td className="px-2 py-1 text-right">
              {showTwoFooterRows ? team.currentPredictedScore : team.predictedScore}
            </td>
            <td className="px-2 py-1 text-right">{team.trend}</td>
          </tr>
          {showTwoFooterRows && (
            <tr>
              <td colSpan={13} className="px-2 py-1 text-right">Pre-game</td>
              <td />
              <td className="px-2 py-1 text-right">{team.predictedScore}</td>
              <td />
            </tr>
          )}
        </tfoot>
      </table>
    </div>
  );
}
