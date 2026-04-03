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
  readonly showStats: boolean;
}

const STAT_KEYS: SortKey[] = ['kicks', 'handballs', 'disposals', 'marks', 'hitouts', 'freesFor', 'freesAgainst', 'tackles', 'goals', 'behinds', 'predictedScore', 'trend'];

const ALL_HEADERS: { label: string; shortLabel?: string; key: SortKey; left?: boolean; stat?: boolean }[] = [
  { label: 'No.',       key: 'teamPlayerId' },
  { label: 'Player',    key: 'name',          left: true },
  { label: 'Pos',       key: 'position' },
  { label: 'K',         key: 'kicks',         stat: true },
  { label: 'H',         key: 'handballs',     stat: true },
  { label: 'D',         key: 'disposals',     stat: true },
  { label: 'M',         key: 'marks',         stat: true },
  { label: 'HO',        key: 'hitouts',       stat: true },
  { label: 'FF',        key: 'freesFor',      stat: true },
  { label: 'FA',        key: 'freesAgainst',  stat: true },
  { label: 'T',         key: 'tackles',       stat: true },
  { label: 'G',         key: 'goals',         stat: true },
  { label: 'B',         key: 'behinds',       stat: true },
  { label: 'Score',     key: 'score' },
  { label: 'Predicted', shortLabel: 'Pred', key: 'predictedScore' },
  { label: 'Trend',     key: 'trend',          stat: true },
];

export default function PlayersTable({ players, team, showStats }: Props) {
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

  const isHidden = (stat?: boolean) => stat && !showStats;
  const colClass = (stat?: boolean, extra = '') =>
    `${isHidden(stat) ? 'hidden md:table-cell' : ''} ${extra}`.trim();

  const sorted = applySorts(players, sort);
  const showTwoFooterRows = team.currentPredictedScore !== team.score;

  // On mobile with stats hidden, footer colspan adjusts: 3 visible cols (No, Player, Pos) before Score
  const visibleLeadingCols = showStats ? 13 : 3;

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-xs border border-gray-200">
        <thead className="bg-gray-50 text-gray-600">
          <tr>
            <th colSpan={showStats ? 16 : 5} className="px-2 py-1 text-left border-b border-gray-200 font-semibold">
              <div className="flex items-center justify-between">
                <span>Team</span>
                <div className="flex items-center gap-3">
                  {userSorted && (
                    <button
                      onClick={() => { setSort(DEFAULT_SORT); setUserSorted(false); }}
                      className="text-xs font-normal text-blue-600 hover:text-blue-800 hover:underline"
                    >
                      Reset sort
                    </button>
                  )}
                </div>
              </div>
            </th>
          </tr>
          <tr className="border-b border-gray-200">
            {ALL_HEADERS.map(({ label, shortLabel, key, left, stat }) => {
              const active = sort.some(s => s.key === key);
              return (
                <th
                  key={label}
                  className={`px-2 py-1 whitespace-nowrap cursor-pointer select-none hover:bg-gray-200 ${active ? 'bg-gray-200' : ''} ${left ? 'text-left' : 'text-right'} ${colClass(stat)}`}
                  onClick={e => handleSort(key, e.shiftKey)}
                >
                  {shortLabel ? <><span className="md:hidden">{shortLabel}</span><span className="hidden md:inline">{label}</span></> : label}{sortIcon(key)}
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
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.kicks}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.handballs}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.disposals}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.marks}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.hitouts}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.freesFor}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.freesAgainst}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.tackles}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.goals}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.behinds}</td>
              <td className="px-2 py-1 text-right font-medium">{player.stats.score}</td>
              <td className="px-2 py-1 text-right">{player.stats.predictedScore}</td>
              <td className={colClass(true, 'px-2 py-1 text-right')}>{player.stats.trend}</td>
            </tr>
          ))}
        </tbody>
        <tfoot className="border-t border-gray-300 bg-gray-50 font-semibold text-xs">
          <tr>
            <td colSpan={visibleLeadingCols} className="px-2 py-1 text-right">Total</td>
            <td className="px-2 py-1 text-right">{team.score}</td>
            <td className="px-2 py-1 text-right">
              {showTwoFooterRows ? team.currentPredictedScore : team.predictedScore}
            </td>
            <td className={colClass(true, 'px-2 py-1 text-right')}>{team.trend}</td>
          </tr>
          {showTwoFooterRows && (
            <tr>
              <td colSpan={visibleLeadingCols} className="px-2 py-1 text-right">Pre-game</td>
              <td />
              <td className="px-2 py-1 text-right">{team.predictedScore}</td>
              <td className={colClass(true)} />
            </tr>
          )}
        </tfoot>
      </table>
    </div>
  );
}
