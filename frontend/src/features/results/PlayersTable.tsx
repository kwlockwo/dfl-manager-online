import type { SelectedPlayer, TeamResults } from '../../types/api';

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

function rowClass(scrapingStatus: string | null): string {
  if (!scrapingStatus) return '';
  return STATUS_CLASS[scrapingStatus] ?? '';
}

interface Props {
  players: SelectedPlayer[];
  team: TeamResults;
}

export default function PlayersTable({ players, team }: Props) {
  const sorted = [...players].sort((a, b) => {
    if (a.position !== b.position) return a.position.localeCompare(b.position);
    if (b.stats.score !== a.stats.score) return b.stats.score - a.stats.score;
    return b.stats.trend - a.stats.trend;
  });

  const showTwoFooterRows = team.currentPredictedScore !== team.score;

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-xs border border-gray-200">
        <thead className="bg-gray-50 text-gray-600">
          <tr>
            <th colSpan={17} className="px-2 py-1 text-left border-b border-gray-200 font-semibold">
              Team
            </th>
          </tr>
          <tr className="border-b border-gray-200">
            {['No.','Player','Pos','K','H','D','M','HO','FF','FA','T','G','B','Score','Predicted','Trend'].map(h => (
              <th key={h} className="px-2 py-1 text-right first:text-left whitespace-nowrap">{h}</th>
            ))}
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
            <td colSpan={12} />
            <td className="px-2 py-1 text-right">Total</td>
            <td className="px-2 py-1 text-right">{team.score}</td>
            <td className="px-2 py-1 text-right">
              {showTwoFooterRows ? team.currentPredictedScore : team.predictedScore}
            </td>
            <td className="px-2 py-1 text-right">{team.trend}</td>
          </tr>
          {showTwoFooterRows && (
            <tr>
              <td colSpan={12} />
              <td colSpan={2} className="px-2 py-1 text-right">Pre-game</td>
              <td className="px-2 py-1 text-right">{team.predictedScore}</td>
              <td />
            </tr>
          )}
        </tfoot>
      </table>
    </div>
  );
}
