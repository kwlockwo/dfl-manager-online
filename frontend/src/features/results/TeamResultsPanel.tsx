import type { TeamResults } from '../../types/api';
import PlayersTable from './PlayersTable';
import EmergenciesTable from './EmergenciesTable';

const EMG_MESSAGES: Record<string, string> = {
  '*': '* Replaced in selected 22 by emergency',
  '**': '** Replaced in selected 22 by emergency',
  '*/**': '*/** Replaced in selected 22 by emergency',
};

interface Props {
  team: TeamResults;
  label: 'Home' | 'Away';
  showStats: boolean;
}

export default function TeamResultsPanel({ team, label, showStats }: Props) {
  return (
    <div className="rounded border border-gray-200 shadow-sm overflow-hidden">
      <div className="bg-gray-100 px-4 py-2 font-semibold border-b border-gray-200">
        {label}: {team.teamName}
      </div>
      <div className="p-3">
        <PlayersTable players={team.players} team={team} showStats={showStats} />
        <EmergenciesTable players={team.emergencies} showStats={showStats} />
        {team.emgInd && EMG_MESSAGES[team.emgInd] && (
          <p className="mt-2 text-xs text-red-600">{EMG_MESSAGES[team.emgInd]}</p>
        )}
      </div>
    </div>
  );
}
