import type { SelectedPlayer } from '../../types/api';

const STATUS_CLASS: Record<string, string> = {
  InProgress: 'bg-yellow-50',
  Completed: 'bg-blue-100',
  Finalized: 'bg-green-100',
};

function playerName(player: SelectedPlayer): string {
  const suffix = player.replacementInd === '*' || player.replacementInd === '**'
    ? player.replacementInd
    : '';
  return player.name + suffix;
}

interface Props {
  readonly players: SelectedPlayer[];
  readonly showStats: boolean;
}

export default function EmergenciesTable({ players, showStats }: Props) {
  const sorted = [...players].sort((a, b) => a.emgSort - b.emgSort);
  const colClass = (stat: boolean) => stat && !showStats ? 'hidden md:table-cell' : '';

  return (
    <div className="overflow-x-auto mt-3">
      <table className="w-full text-xs border border-gray-200">
        <thead className="bg-gray-50 text-gray-600">
          <tr>
            <th colSpan={showStats ? 16 : 5} className="px-2 py-1 text-left border-b border-gray-200 font-semibold">
              Emergencies
            </th>
          </tr>
          <tr className="border-b border-gray-200">
            {['No.', 'Player', 'Pos'].map(h => (
              <th key={h} className={`px-2 py-1 ${h === 'Player' ? 'text-left' : 'text-right'} whitespace-nowrap`}>{h}</th>
            ))}
            {['K','H','D','M','HO','FF','FA','T','G','B'].map(h => (
              <th key={h} className={`px-2 py-1 text-right whitespace-nowrap ${colClass(true)}`}>{h}</th>
            ))}
            <th className="px-2 py-1 text-right whitespace-nowrap">Score</th>
            <th className="px-2 py-1 text-right whitespace-nowrap">Predicted</th>
            <th className={`px-2 py-1 text-right whitespace-nowrap ${colClass(true)}`}>Trend</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {sorted.map(player => {
            const rowCls = player.stats.scrapingStatus
              ? (STATUS_CLASS[player.stats.scrapingStatus] ?? '')
              : '';
            return (
              <tr key={player.teamPlayerId} className={rowCls}>
                <td className="px-2 py-1 text-right">{player.teamPlayerId}</td>
                <td className="px-2 py-1 whitespace-nowrap">{playerName(player)}</td>
                <td className="px-2 py-1 text-right">{player.position}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.kicks}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.handballs}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.disposals}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.marks}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.hitouts}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.freesFor}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.freesAgainst}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.tackles}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.goals}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.behinds}</td>
                <td className="px-2 py-1 text-right font-medium">{player.stats.score}</td>
                <td className="px-2 py-1 text-right">{player.stats.predictedScore}</td>
                <td className={`px-2 py-1 text-right ${colClass(true)}`}>{player.stats.trend}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
